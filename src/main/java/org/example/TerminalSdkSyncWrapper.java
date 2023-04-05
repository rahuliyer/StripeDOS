package org.example;

import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.external.callable.*;
import com.stripe.stripeterminal.external.models.*;
import com.stripe.stripeterminal.log.LogLevel;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TerminalSdkSyncWrapper {
    List<Reader> mReaderList = Collections.synchronizedList(new ArrayList<>());
    TerminalSdkSyncWrapper() {
        TokenProvider provider = new TokenProvider();
        provider.fetchConnectionToken(new ConnectionTokenCallback() {
            @Override
            public void onSuccess(@org.jetbrains.annotations.NotNull String s) {
                System.out.println(s);
            }

            @Override
            public void onFailure(@org.jetbrains.annotations.NotNull ConnectionTokenException e) {
                System.err.println(e);
            }
        });

        TerminalListener listener = new TerminalListener() {
            @Override
            public void onUnexpectedReaderDisconnect(@NotNull Reader reader) {
                System.err.println("Reader disconnected unexpectedly");
            }

            @Override
            public void onConnectionStatusChange(@NotNull ConnectionStatus status) {
                TerminalListener.super.onConnectionStatusChange(status);
                System.out.println("Connection status changed to: " + status);
            }

            @Override
            public void onPaymentStatusChange(@NotNull PaymentStatus status) {
                TerminalListener.super.onPaymentStatusChange(status);
                System.out.println("Payment status changed to: " + status);
            }
        };

        LogLevel logLevel = LogLevel.VERBOSE;


        if (!Terminal.isInitialized()) {
            Terminal.initTerminal(provider, listener, logLevel);
        }
    }

    CompletableFuture<Void> discoverReaders(int timeout) {
        CompletableFuture<Void> f = new CompletableFuture();
        Terminal.getInstance().discoverReaders(new DiscoveryConfiguration(),
                new ReadersCallback() {
                    @Override
                    public void onSuccess(List<Reader> readers) {
                        System.out.println("Finished discovering readers");
                        mReaderList.addAll(readers);
                        f.complete(null);
                    }

                    @Override
                    public void onFailure(@NotNull TerminalException e) {
                        e.printStackTrace();
                        f.completeExceptionally(e);
                    }
                });

        return f;
    }

    List<Reader> getReaderList() {
        return mReaderList;
    }


    CompletableFuture<Void> connectReader(Reader reader) {
        CompletableFuture<Void> f = new CompletableFuture<>();
        Terminal.getInstance().connectInternetReader(reader,
                new InternetConnectionConfiguration(true),
                new ReaderCallback() {
                    @Override
                    public void onSuccess(@NotNull Reader reader) {
                        System.out.println("Connected!");
                        f.complete(null);
                    }

                    @Override
                    public void onFailure(@NotNull TerminalException e) {
                        e.printStackTrace();
                        f.completeExceptionally(e);
                    }
                });

        return f;
    }

    CompletableFuture<PaymentIntent> createPaymentIntent(long amount, String currency) {
        CompletableFuture<PaymentIntent> f = new CompletableFuture<>();

        PaymentIntentParameters params = new PaymentIntentParameters.Builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setCaptureMethod(CaptureMethod.Automatic)
                .build();
        Terminal.getInstance().createPaymentIntent(params, new PaymentIntentCallback() {
            @Override
            public void onSuccess(PaymentIntent paymentIntent) {
                f.complete(paymentIntent);
            }

            @Override
            public void onFailure(TerminalException e) {
                f.completeExceptionally(e);
            }
        });

        return f;
    }

    CompletableFuture<PaymentIntent> collectPaymentMethod(PaymentIntent paymentIntent) {
        CompletableFuture<PaymentIntent> f = new CompletableFuture<>();

        CollectConfiguration collectConfig = new CollectConfiguration.Builder()
                .updatePaymentIntent(true)
                .build();
        Cancelable cancelable = Terminal.getInstance().collectPaymentMethod(paymentIntent,
                new PaymentIntentCallback() {
                    @Override
                    public void onSuccess(@NotNull PaymentIntent paymentIntent) {
                        f.complete(paymentIntent);
                    }

                    @Override
                    public void onFailure(@NotNull TerminalException e) {
                        f.completeExceptionally(e);
                    }
                },
                collectConfig);

        return f;
    }

    CompletableFuture<PaymentIntent> processPayment(PaymentIntent paymentIntent) {
        CompletableFuture<PaymentIntent> f = new CompletableFuture<>();

        Terminal.getInstance().processPayment(paymentIntent,
                new PaymentIntentCallback() {
                    @Override
                    public void onSuccess(PaymentIntent paymentIntent) {
                        f.complete(paymentIntent);
                    }

                    @Override
                    public void onFailure(TerminalException e) {
                        f.completeExceptionally(e);
                    }
                });

        return f;
    }

    CompletableFuture<Void> disconnectReader() {
        CompletableFuture f = new CompletableFuture();

        Terminal.getInstance().disconnectReader(new Callback() {
            @Override
            public void onSuccess() {
                f.complete(null);
            }

            @Override
            public void onFailure(@NotNull TerminalException e) {
                f.completeExceptionally(e);
            }
        });

        return f;
    }
}

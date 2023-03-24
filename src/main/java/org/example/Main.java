package org.example;

import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback;
import com.stripe.stripeterminal.external.callable.TerminalListener;
import com.stripe.stripeterminal.external.models.ConnectionStatus;
import com.stripe.stripeterminal.external.models.ConnectionTokenException;
import com.stripe.stripeterminal.external.models.PaymentStatus;
import com.stripe.stripeterminal.external.models.Reader;
import com.stripe.stripeterminal.log.LogLevel;
import org.jetbrains.annotations.NotNull;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

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

            }

            @Override
            public void onConnectionStatusChange(@NotNull ConnectionStatus status) {
                TerminalListener.super.onConnectionStatusChange(status);
            }

            @Override
            public void onPaymentStatusChange(@NotNull PaymentStatus status) {
                TerminalListener.super.onPaymentStatusChange(status);
            }
        };

// Choose the level of messages that should be logged to your console
        LogLevel logLevel = LogLevel.VERBOSE;


// Pass in the current application context, your desired logging level, your token provider, and the listener you created
        if (!Terminal.isInitialized()) {
            Terminal.initTerminal(provider, listener, logLevel);
        }

// Since the Terminal is a singleton, you can call getInstance whenever you need it
        Terminal.getInstance();

    }
}
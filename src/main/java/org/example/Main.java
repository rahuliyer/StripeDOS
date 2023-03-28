package org.example;

import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.external.callable.*;
import com.stripe.stripeterminal.external.models.*;
import com.stripe.stripeterminal.log.LogLevel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello world!");

        TerminalSdkSyncWrapper sdk = new TerminalSdkSyncWrapper();
// Since the Terminal is a singleton, you can call getInstance whenever you need it
        try {
            sdk.scheduleDiscoveryCancelation(2000);
            sdk.discoverReaders(1).get();
        } catch (Exception e) {
            if (!e.getMessage().equals("DiscoverReaders was canceled by the user")) {
                System.out.println("boo!");
                e.printStackTrace();
            }
        }

        System.out.println("Found " + sdk.getReaderList().size() + " readers");
        if (sdk.getReaderList().size() > 0) {
            System.out.println("First reader: " + sdk.getReaderList().get(0).getLabel());
            System.out.println("IP address: " + sdk.getReaderList().get(0).getIpAddress());
        }

        System.out.println("trying to connect");
        sdk.connectReader(sdk.getReaderList().get(0)).get();
        System.out.println("Ready to take a transaction");

        System.out.println("Creating a payment intent");
        PaymentIntent pi = sdk.createPaymentIntent(1337, "usd").get();

        System.out.println("Collecting payment method");
        pi = sdk.collectPaymentMethod(pi).get();

        System.out.println("Processing payment");
        sdk.processPayment(pi).get();

        System.out.println("Success!");

        System.out.println("Disconnecting reader");
        sdk.disconnectReader().get();
        System.out.println("Successfully disconnected");
    }
}
package ru.spbau.networks.client;

import mikera.cljutils.Clojure;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.Callable;

public class Client implements Callable<Long> {
    public static final int REQUESTS_PER_CONNECTION = 2;
    public static final int CONNECTIONS_PER_CLIENT = 5;
    private String hostName;
    private int portNumber;

    private String message;
    private String expectedResponse;

    public Client(String hostName, int portNumber, int requestLength) {
        this.hostName = hostName;
        this.portNumber = portNumber;

        message = generateConcatProgram(requestLength);
//        message = generateFibProgram(requestLength);
        expectedResponse = Clojure.eval(message).toString();
    }

    @Override
    public Long call() throws Exception {
        long sum = 0;
        int cnt = 0;

        for (int i = 0; i < CONNECTIONS_PER_CLIENT; i++) {
            long cur = sendRequest();

            if(cur >= 0) {
                sum += cur;
                cnt++;
            }
        }

        if(cnt != 0) {
            return sum / cnt;
        }
        else {
            return 0L;
        }
    }


    public long sendRequest() {
        try(
                Socket socket = new Socket(hostName, portNumber);
                DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                DataInputStream reader = new DataInputStream(socket.getInputStream())
                ) {
            long startTime = System.nanoTime();
            for (int i = 0; i < REQUESTS_PER_CONNECTION; i++) {
                writer.writeInt(message.length());
                writer.writeBytes(message);
                writer.flush();

                int responseLength = reader.readInt();
                byte[] responseMsg = new byte[responseLength];


                reader.readFully(responseMsg);

                String responseStr = new String(responseMsg);

                assert (responseStr.equals(expectedResponse));
            }
            long endTime = System.nanoTime();
            return (endTime - startTime) / REQUESTS_PER_CONNECTION;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String generateClojureConcats(String str, int from, int to) {
        if (to - from <= 10) {
            return "\"" + str.substring(from, to) + "\"";
        }

        int mid = (to - from) / 2 + from;
        return "(str " + generateClojureConcats(str, from, mid) + " " + generateClojureConcats(str, mid, to) + ")";
    }

    private static String generateConcatProgram(int length) {
        String str = randomString(length);
        String res = "(apply str (repeat 3 " + generateClojureConcats(str, 0, str.length()) + "))";

        str = str + str + str;
        assert str.equals(Clojure.eval(res));
        return res;
    }


    private static String randomString(int length) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }

        return sb.toString();
    }

    private static String generateFibProgram(int size) {
        String msg = "(do (defn fib [x] (if (<= x 1) 1 (+ (fib (- x 1)) (fib (- x 2))))) (fib " + String.valueOf(size) + "))";
        return msg;
    }

}

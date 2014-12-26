package ru.spbau.networks.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static long run(String hostName, int portNumber, int clientsNumber, int requestLength)
            throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(clientsNumber);
        ArrayList<Client> clients = new ArrayList<>();

        for (int i = 0; i < clientsNumber; i++) {
            clients.add(new Client(hostName, portNumber, requestLength));
        }

        List<Future<Long>> res = pool.invokeAll(clients);

        long sum = 0;
        for(Future<Long> future : res) {
            long cur = future.get();
            sum += cur;
        }

        pool.shutdown();
        return sum / clientsNumber;
    }

    public static void main(String[] args) {
        if(args.length < 3) {
            System.err.println("Not enough arguments");
            return;
        }

        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            int clientsNumber = Integer.parseInt(args[2]);
            int requestLength = (args.length < 4) ? 10 : Integer.parseInt(args[3]);

            long res = run(host, port, clientsNumber, requestLength);

            System.out.println(res/(1000*1000*1000.0));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Wrong arguments");
        }

    }

}

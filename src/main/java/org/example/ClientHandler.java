package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import com.google.gson.Gson;

public class ClientHandler implements Runnable {
    Socket clientSocket;
    BufferedReader in;
    PrintWriter out;
    static ArrayList<Cars> cars = new ArrayList<Cars>();

    ClientHandler (Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run () {
        this.buildCars();
        this.inizializeClientHandler();
        try {
            this.executeClientHandler();
        } catch (SocketException e) {
            System.out.println("error");
        }
    }

    void inizializeClientHandler () {
        try {
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("reader failed" + e);
        }

        out = null;

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void executeClientHandler() throws SocketException {
        out.println("The commands that are available on in application are:");
        out.println("more_expensive, which tells you which car has the highest price.");
        out.println("all, which tells you all the cars registered in the application.");
        out.println("all_sorted, which tells you all the cars sorted by their brand.");
        Gson gson = new Gson();
        String s;
        while (true) {
            s = receive();
            try {
                switch (s) {
                    default:
                        out.println(s + " is not a command");
                        break;
                    case "more_expensive":
                        out.println(gson.toJson(searchMaxPrice() + "€"));
                        break;

                    case "all":
                        out.println(gson.toJson(cars));
                        break;

                    case "all_sorted":
                        sort_by_Brand();
                        out.println(gson.toJson(cars));
                        break;
                }

            } catch (NullPointerException e) {
                System.out.println("Client: " + clientSocket.getLocalAddress() + " disconnected from the server");
                break;
            }

            if (s == "") break;
        }
    }



    String receive() {
        String s = "";
        try {
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void buildCars() {
        cars.add(new Cars("BMW",10,15000.00));
        cars.add(new Cars("Ferrari",15,25000.00));
        cars.add(new Cars("Maserati",1,35000.00));
    }


    Cars searchMaxPrice() {
        sort_by_price();
        return cars.get(0);
    }

    void sort_by_price() {
        cars.sort((o1, o2) -> {
            if (o1.getPrice() < o2.getPrice())
                return 1;
            if (o1.getPrice() > o2.getPrice())
                return -1;
            return 0;
        });
    }

    void sort_by_Brand() {
        cars.sort((o1, o2) -> {
            return o1.getBrand().compareTo(o2.getBrand());
        });
    }


}
package Main;

import CommonResources.BurningBarrel;
import CommonResources.NarrowTunnel;
import CommonResources.SchedulePlate;
import CommonResources.Storage;
import Constants.Parameters;
import Constants.Product;
import Threads.Dock;
import Threads.Generator;
import Threads.Hobo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {
        System.out.println("Input something to finish process");

        NarrowTunnel tunnel = new NarrowTunnel(Parameters.TUNNEL_CAPASITY);
        HashMap<String, Storage> storages = createStorages();
        BurningBarrel barrel = new BurningBarrel(Parameters.NOMBER_OF_HOBOS);
        SchedulePlate plate = new SchedulePlate(barrel, storages, Parameters.NOMBER_OF_HOBOS);

        Generator generator = new Generator(tunnel);
        Vector<Dock> docks = createDocks(Parameters.NUMBER_OF_DOCKS, tunnel, storages);
        Vector<Hobo> hobos = createHobos(Parameters.NOMBER_OF_HOBOS, barrel, plate, storages);

        generator.start();
        startDocks(docks);
        startHobos(hobos);

        try {
            System.in.read();
        } catch (IOException ignored) {
        }

        generator.finish();
        finishDocks(docks);
        finishHobos(hobos);
    }

    private static HashMap<String, Storage> createStorages() {
        HashMap<String, Storage> storages = new HashMap<>();

        for (String type : Product.TYPES) {
            storages.put(type, new Storage());
        }

        return storages;
    }

    private static Vector<Dock> createDocks(int number, NarrowTunnel tunnel, HashMap<String, Storage> storages) {
        Vector<Dock> docks = new Vector<>(number);

        for (int i = 1; i <= number; ++i) {
            docks.add(new Dock(tunnel, storages, i));
        }

        return docks;
    }

    private static Vector<Hobo> createHobos(int number, BurningBarrel barrel, SchedulePlate plate, HashMap<String, Storage> storages) {
        Vector<Hobo> hobos = new Vector<>(number);

        for (int i = 1; i <= number; ++i) {
            hobos.add(new Hobo(barrel, plate, storages, i));
        }

        return hobos;
    }

    private static void startDocks(Vector<Dock> docks) {
        for (Dock dock : docks) {
            dock.start();
        }
    }

    private static void startHobos(Vector<Hobo> hobos) {
        for (Hobo hobo : hobos) {
            hobo.start();
        }
    }

    private static void finishDocks(Vector<Dock> docks) {
        for (Dock dock : docks) {
            dock.finish();
        }
    }

    private static void finishHobos(Vector<Hobo> hobos) {
        for (Hobo hobo : hobos) {
            hobo.finish();
        }
    }
}
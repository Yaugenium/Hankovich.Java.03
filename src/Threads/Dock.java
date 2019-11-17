package Threads;

import CommonResources.NarrowTunnel;
import CommonResources.Storage;
import Ship.Ship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;

public class Dock extends Thread {
    private final NarrowTunnel tunnel;
    private final HashMap<String, Storage> storages;
    private int number;
    private Optional<Ship> ship;

    private final Logger logger = LoggerFactory.getLogger(Dock.class);
    private volatile boolean finish = false;

    public Dock(NarrowTunnel tunnel, HashMap<String, Storage> storages, int number) {
        this.tunnel = tunnel;
        this.storages = storages;
        this.number = number;
        ship = Optional.empty();

        setName("DockThread-" + number);
    }

    public void finish() {
        finish = true;
    }

    @Override
    public void run() {
        while (!finish) {
            takeShip();

            if (ship.isPresent()) {
                Ship ship = this.ship.get();

                unloadToStorage(ship);
                checkShip(ship);
            }
        }

        logger.info("\t\t\tDock #{} closed for repair", number);
    }

    private void takeShip() {
        if (ship.isEmpty()) {
            synchronized (tunnel) {
                if (tunnel.isNotEmpty()) {
                    ship = tunnel.getShip();

                    logger.info("\t\t\tShip #{} moored to Dock #{}", ship.map(Ship::getNumber).get(), number);
                }
            }
        }
    }

    private void unloadToStorage(Ship ship) {
        String type = ship.getType();
        Storage storage = storages.get(type);

        ship.unload();

        try {
            sleep(1000);
        } catch (InterruptedException ignored) {
        }

        synchronized (storages.get(type)) {
            storage.download();

            logger.info("\t\t\tShip #{} unloading by Dock #{}. There are {} product in {} storage", new Object[]{ship.getNumber(), number, storage.getAmount(), type});
        }
    }

    private void checkShip(Ship ship) {
        if (ship.isEmpty()) {
            logger.info("\t\t\tShip #{} unloaded by Dock #{}", ship.getNumber(), number);

            this.ship = Optional.empty();
        }
    }
}
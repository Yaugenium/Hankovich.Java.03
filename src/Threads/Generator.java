package Threads;

import CommonResources.NarrowTunnel;
import Constants.Product;
import Ship.Ship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Generator extends Thread {
    private final NarrowTunnel tunnel;
    private int number;

    private static Random random = new Random();
    private static int[] amounts = {10, 20, 30};

    private final Logger logger = LoggerFactory.getLogger(Generator.class);
    private volatile boolean finish = false;

    public Generator(NarrowTunnel tunnel) {
        this.tunnel = tunnel;

        setName("GeneratorThread");
    }

    public void finish() {
        finish = true;
    }

    @Override
    public void run() {
        while (!finish) {
            number++;
            goToTunnel(generateShip());
        }

        logger.info("\tThe Kraken population has risen sharply. There will be no more ships");
    }

    private Ship generateShip() {
        try {
            sleep(1000);
        } catch (InterruptedException ignored) {
        }

        Ship ship = new Ship(Product.TYPES[random.nextInt(3)], amounts[random.nextInt(3)], number);

        logger.info("\tShip #{} ({}, {}) appeared on the horizon", new Object[]{ship.getNumber(), ship.getType(), ship.getAmount()});

        return ship;
    }

    private void goToTunnel(Ship ship) {
        synchronized (tunnel) {
            if (!tunnel.isFull()) {
                tunnel.accept(ship);

                logger.info("\tShip #{} taken to the tunnel", ship.getNumber());
            } else {
                logger.info("\tShip #{} became a Kraken lunch", ship.getNumber());
            }
        }
    }
}
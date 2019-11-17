package Threads;

import CommonResources.BurningBarrel;
import CommonResources.SchedulePlate;
import CommonResources.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;

public class Hobo extends Thread {
    private final BurningBarrel barrel;
    private final SchedulePlate plate;
    private final HashMap<String, Storage> storages;
    private int number;

    private final Logger logger = LoggerFactory.getLogger(Hobo.class);
    private volatile boolean finish = false;

    public Hobo(BurningBarrel barrel, SchedulePlate plate, HashMap<String, Storage> storages, int number) {
        this.barrel = barrel;
        this.plate = plate;
        this.storages = storages;
        this.number = number;

        setName("HoboThread-" + number);
    }

    public void finish() {
        finish = true;
    }

    @Override
    public void run() {
        while (!finish) {
            if (isNotCook()) {
                Optional<String> storageType = getGoal();

                if (storageType.isPresent()) {
                    String type = storageType.get();

                    goToStorage(type);
                    steal(type);
                    goToBarrel(type);
                }

                checkReady();
                checkAlmostReady();
            }
        }

        logger.info("\t\t\t\t\tHobo #{} went to sleep", number);
    }

    private boolean isNotCook() {
        synchronized (plate) {
            return !plate.isCook(number);
        }
    }

    private Optional<String> getGoal() {
        synchronized (plate) {
            return plate.getStorageType();
        }
    }

    private void goToStorage(String type) {
        logger.info("\t\t\t\t\tHobo #{} goes to {} storage", number, type);

        try {
            sleep(1500);
        } catch (InterruptedException ignored) {
        }
    }

    private void steal(String type) {
        synchronized (plate) {
            Storage storage = storages.get(type);

            synchronized (storages.get(type)) {
                storage.steal();

                logger.info("\t\t\t\t\tHobo #{} stole some {} and goes back. There are {} product in {} storage", new Object[]{number, type, storage.getAmount(), type});

                plate.call(type);
            }
        }
    }

    private void goToBarrel(String type) {
        try {
            sleep(1500);
        } catch (InterruptedException ignored) {
        }

        synchronized (plate) {
            synchronized (barrel) {
                barrel.put(type);
                plate.cameBack(type);

                logger.info("\t\t\t\t\tHobo #{} came back. Hobos have {} {}", new Object[]{number, barrel.look(type), type});
            }
        }
    }

    private void checkReady() {
        synchronized (barrel) {
            if (barrel.isReady()) {
                logger.info("\t\t\t\t\tHobos gathered together");

                barrel.cook();

                logger.info("\t\t\t\t\tThe gang of cooks survived another day");

                synchronized (plate) {
                    plate.changeCooks();

                    boolean[] cooks = plate.getCooks();

                    for (int i = 0; i < plate.getNumber(); ++i) {
                        logger.info("\t\t\t\t\tNew staff: Hobo # {}{}", i + 1, cooks[i] ? " is cook" : " is runner");
                    }

                    plate.notifyAll();
                }
            }
        }
    }

    private void checkAlmostReady() {
        synchronized (plate) {
            if (plate.isAlmostReady()) {
                try {
                    plate.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
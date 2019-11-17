package CommonResources;

import Constants.Product;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

public class SchedulePlate {
    private final BurningBarrel barrel;
    private final HashMap<String, Storage> storages;
    private final HashMap<String, Integer> hobosWithoutProfit;
    private final HashMap<String, Integer> hobosWithProfit;
    private final boolean[] cooks;
    private int number;

    public SchedulePlate(BurningBarrel barrel, HashMap<String, Storage> storages, int number) {
        this.barrel = barrel;
        this.storages = storages;
        hobosWithoutProfit = new HashMap<>();
        hobosWithProfit = new HashMap<>();

        for (String type : Product.TYPES) {
            hobosWithoutProfit.put(type, 0);
            hobosWithProfit.put(type, 0);
        }

        this.number = number;
        cooks = new boolean[number];

        cooks[number - 1] = true;
        cooks[number - 2] = true;
    }

    public boolean[] getCooks() {
        return cooks;
    }

    public int getNumber() {
        return number;
    }

    public boolean isCook(int number) {
        return cooks[number - 1];
    }

    public void changeCooks() {
        Random random = new Random();

        Arrays.fill(cooks, false);

        int first = random.nextInt(number);
        int second;

        do {
            second = random.nextInt(number);
        } while (second == first);

        cooks[first] = true;
        cooks[second] = true;
    }

    public Optional<String> getStorageType() {
        Optional<String> type = Optional.empty();

        synchronized (barrel) {
            int min = barrel.look(Product.BREAD) + barrel.look(Product.SAUSAGES) + barrel.look(Product.MAYONNAISE) + number - 2;

            for (String typeName : Product.TYPES) {
                synchronized (storages.get(typeName)) {
                    int amount = barrel.look(typeName) + hobosWithoutProfit.get(typeName) + hobosWithProfit.get(typeName);

                    if (storages.get(typeName).getAmount() - hobosWithoutProfit.get(typeName) > 0 && amount <= min) {
                        type = Optional.of(typeName);
                        min = amount;
                    }
                }
            }
        }

        type.ifPresent(s -> hobosWithoutProfit.computeIfPresent(s, (k, v) -> v + 1));

        return type;
    }

    public void call(String type) {
        hobosWithoutProfit.computeIfPresent(type, (k, v) -> v - 1);
        hobosWithProfit.computeIfPresent(type, (k, v) -> v + 1);
    }

    public void cameBack(String type) {
        hobosWithProfit.computeIfPresent(type, (k, v) -> v - 1);
    }

    public boolean isAlmostReady() {
        for (String type : Product.TYPES) {
            if(barrel.look(type) + hobosWithoutProfit.get(type) + hobosWithProfit.get(type) < number) {
                return false;
            }
        }

        return true;
    }
}
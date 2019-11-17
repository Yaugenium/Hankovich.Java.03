package CommonResources;

import Constants.Product;

import java.util.HashMap;

public class BurningBarrel {
    private final HashMap<String, Integer> products;
    private int number;

    public BurningBarrel(int number) {
        products = new HashMap<>();

        for (String type : Product.TYPES) {
            products.put(type, 0);
        }

        this.number = number;
    }

    public void put(String type) {
        products.computeIfPresent(type, (k, v) -> v + 1);
    }

    public int look(String type) {
        return products.get(type);
    }

    public boolean isReady() {
        for (String type : Product.TYPES) {
            if (products.get(type) < number) {
                return false;
            }
        }

        return true;
    }

    public boolean cook() {
        if (!isReady()) {
            return false;
        }

        for (String type : Product.TYPES) {
            products.computeIfPresent(type, (k, v) -> v - number);
        }

        return true;
    }
}
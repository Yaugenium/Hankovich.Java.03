package CommonResources;

import Ship.Ship;

import java.util.Optional;
import java.util.Vector;

public class NarrowTunnel {
    private final Vector<Ship> tunnel;
    private int capacity;

    public NarrowTunnel(int capacity) {
        tunnel = new Vector<>(capacity);
        this.capacity = capacity;
    }

    public boolean isFull() {
        return capacity == tunnel.size();
    }

    public boolean isNotEmpty() {
        return !tunnel.isEmpty();
    }

    public boolean accept(Ship ship) {
        if (isFull()) {
            return false;
        }

        tunnel.add(ship);

        return true;
    }

    public Optional<Ship> getShip() {
        if (isNotEmpty()) {
            Ship ship = tunnel.firstElement();

            tunnel.remove(ship);

            return Optional.of(ship);
        }

        return Optional.empty();
    }
}
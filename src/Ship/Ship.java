package Ship;

public class Ship {
    private String type;
    private int amount;
    private int number;

    public Ship(String type, int amount, int number) {
        this.type = type;
        this.amount = amount;
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public int getNumber() {
        return number;
    }

    public boolean isEmpty() {
        return amount == 0;
    }

    public boolean unload() {
        amount -= 5;

        return amount >= 0;
    }
}
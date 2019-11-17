package CommonResources;

public class Storage {
    private int amount;

    public Storage() {
        amount = 0;
    }

    public int getAmount() {
        return amount;
    }

    public void download() {
        amount += 5;
    }

    public int steal() {
        int profit = Math.min(1, amount);

        amount -= profit;

        return profit;
    }
}
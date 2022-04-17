package antifraud.database.transaction;

public enum TransactionResult {
    ALLOWED(1), MANUAL_PROCESSING(2), PROHIBITED(3);

    private int number;

    TransactionResult(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

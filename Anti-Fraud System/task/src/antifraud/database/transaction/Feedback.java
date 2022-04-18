package antifraud.database.transaction;

import javax.validation.constraints.NotNull;

public class Feedback {
    @NotNull
    private long transactionId;
    @NotNull
    private TransactionResult feedback;

    public Feedback() {
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionResult getFeedback() {
        return feedback;
    }

    public void setFeedback(TransactionResult feedback) {
        this.feedback = feedback;
    }
}

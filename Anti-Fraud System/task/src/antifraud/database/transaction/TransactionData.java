package antifraud.database.transaction;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionData {
    @NotNull
    private long amount;
    @NotNull
    private String ip;
    @NotNull
    private String number;

    public TransactionData() {
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean ipIsValid() {
        Pattern pattern = Pattern.compile("(\\d{1,3}).(\\d{1,3}).(\\d{1,3}).(\\d{1,3})");
        Matcher matcher = pattern.matcher(ip);
        if (matcher.matches()) {
            for (int i = 1; i <= 4; i++) {
                int value = Integer.parseInt(matcher.group(i));
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean numberIsValid() {
        int nDigits = number.length();
        if (nDigits != 16) {
            return false;
        }

        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--)
        {

            int d = number.charAt(i) - '0';

            if (isSecond)
                d = d * 2;

            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }

    public boolean amountIsValid() {
        return amount > 0;
    }

    public TransactionResult amountResult() {
        if (amount <= 200) {
            return TransactionResult.ALLOWED;
        } else if (amount <= 1500) {
            return TransactionResult.MANUAL_PROCESSING;
        } else {
            return TransactionResult.PROHIBITED;
        }
    }
}

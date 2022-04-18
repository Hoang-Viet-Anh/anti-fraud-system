package antifraud.database.transaction;

import com.google.gson.JsonObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "transactions")
public class TransactionData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "amount", nullable = false)
    private long amount;

    @NotNull
    @Column(name = "ip", nullable = false)
    private String ip;

    @NotNull
    @Column(name = "number", nullable = false)
    private String number;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date dateData;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "region_codes", nullable = false)
    private RegionCodes region;

    @Column(name = "result")
    private TransactionResult result;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback")
    private TransactionResult feedback;

    @Transient
    public static long AllowLimit = 200;

    @Transient
    public static long ProhibitedLimit = 1500;

    @Transient
    private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @NotNull
    @Transient
    private String date;

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

    public Date getDateData() {
        return dateData;
    }
    public void setDateData(Date dateData) {
        this.dateData = dateData;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public RegionCodes getRegion() {
        return region;
    }
    public void setRegion(RegionCodes region) {
        this.region = region;
    }

    public TransactionResult getFeedback() {
        return feedback;
    }
    public void setFeedback(TransactionResult feedback) {
        this.feedback = feedback;
    }

    public TransactionResult getResult() {
        return result;
    }
    public void setResult(TransactionResult result) {
        this.result = result;
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

    public boolean dateIsValid() throws ParseException {
        String regexp = "([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(date);
        if (matcher.matches()) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            int hours = Integer.parseInt(matcher.group(4));
            int minutes = Integer.parseInt(matcher.group(5));
            int seconds = Integer.parseInt(matcher.group(6));

            if (year > 0 &&
                    month > 0 && month <= 12 &&
                    day > 0 && day <= 31 &&
                    hours >= 0 && hours < 24 &&
                    minutes >= 0 && minutes < 60 &&
                    seconds >= 0 && seconds < 60) {
                dateData = DATE_TIME_FORMAT.parse(date);
                return true;
            }
        }
        return false;
    }

    public TransactionResult amountResult() {
        if (amount <= AllowLimit) {
            return TransactionResult.ALLOWED;
        } else if (amount <= ProhibitedLimit) {
            return TransactionResult.MANUAL_PROCESSING;
        } else {
            return TransactionResult.PROHIBITED;
        }
    }

    public static void decreaseAllowLimit(long amount) {
        AllowLimit = (long) Math.ceil(0.8 * AllowLimit -
                0.2 * amount);
    }

    public static void increaseAllowLimit(long amount) {
        AllowLimit = (long) Math.ceil(0.8 * AllowLimit +
                0.2 * amount);
    }

    public static void decreaseProhibitedLimit(long amount) {
        ProhibitedLimit = (long) Math.ceil(0.8 * ProhibitedLimit -
                0.2 * amount);
    }

    public static void increaseProhibitedLimit(long amount) {
        ProhibitedLimit = (long) Math.ceil(0.8 * ProhibitedLimit +
                0.2 * amount);
    }
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("transactionId", id);
        object.addProperty("amount", amount);
        object.addProperty("ip", ip);
        object.addProperty("number", number);
        object.addProperty("region", region.toString());
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        object.addProperty("date", format.format(dateData));
        object.addProperty("result", result.toString());
        object.addProperty("feedback", feedback != null ? feedback.toString() : "");
        return object;
    }
}

package app.services.payments;
import java.util.Calendar;

public class CreditCardPayment implements PaymentStrategy {

    private final String cardNumber;
    private final String cardHolderName;
    private final int expiryMonth;
    private final int expiryYear;
    private final String cvv;

    public CreditCardPayment(String cardNumber, String cardHolderName, int expiryMonth, int expiryYear, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvv = cvv;
    }

    private boolean isExpired() {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        return expiryYear < y || (expiryYear == y && expiryMonth < m);
    }

    private String digitsOnly(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    private boolean validate() {
        if (cardHolderName == null || cardHolderName.trim().isEmpty()) return false;
        String num = digitsOnly(cardNumber);
        if (num.length() < 13 || num.length() > 19) return false;
        if (!num.matches("\\d+")) return false;
        if (isExpired()) return false;
        if (cvv == null || !cvv.matches("\\d{3,4}")) return false;
        if (expiryMonth < 1 || expiryMonth > 12) return false;
        return true;
    }

    @Override
    public void pay(double amount) {
        if (!validate()) {
            throw new IllegalArgumentException("Invalid credit card details");
        }
        System.out.println("Paid " + amount + " using Credit Card");
    }
}




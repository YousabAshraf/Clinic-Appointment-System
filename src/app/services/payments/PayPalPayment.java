package app.services.payments;

public class PayPalPayment implements PaymentStrategy {

    private final String email;
    private final String password;

    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
    }

    private boolean validate() {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return false;
        if (password == null || password.length() < 6) return false;
        return true;
    }

    @Override
    public void pay(double amount) {
        if (!validate()) {
            throw new IllegalArgumentException("Invalid PayPal credentials");
        }
        System.out.println("Paid " + amount + " using PayPal");
    }
}




package app.services.payments;

public class PayPalPayment implements PaymentStrategy {

    private final String email;
    private final String password;

    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
    }

    private boolean validate() {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return false;
        if (password == null || password.length() < 6) return false;
        return true;
    }

    @Override
    public void pay(double amount) {
        if (!validate()) {
            throw new IllegalArgumentException("Invalid PayPal credentials");
        }
        System.out.println("Paid " + amount + " using PayPal");
    }
}





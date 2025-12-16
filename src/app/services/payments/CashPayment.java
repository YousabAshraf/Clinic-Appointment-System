package app.services.payments;

public class CashPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("Amount " + amount + " will be collected in the clinic");
    }
}

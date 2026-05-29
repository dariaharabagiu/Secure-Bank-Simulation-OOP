package org.poo.bank.commission;

public class CommissionCalculator {
    private final CommissionStrategy commissionStrategy;

    public CommissionCalculator(final CommissionStrategy commissionStrategy) {
        this.commissionStrategy = commissionStrategy;
    }

    /**
     * Applies the commission calculation based on the selected strategy.
     *
     * @param amount The transaction amount.
     * @return The commission value.
     */
    public double applyCommission(final double amount) {
        return commissionStrategy.calculateCommission(amount);
    }
}


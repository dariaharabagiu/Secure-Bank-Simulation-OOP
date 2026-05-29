package org.poo.bank.commission;

public interface CommissionStrategy {
    /**
     * Calculate the commission based on the provided amount.
     *
     * @param amount The transaction amount.
     * @return The commission value.
     */
    double calculateCommission(double amount);
}

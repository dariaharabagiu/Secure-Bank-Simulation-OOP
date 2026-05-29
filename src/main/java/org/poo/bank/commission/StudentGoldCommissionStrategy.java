package org.poo.bank.commission;

public final class StudentGoldCommissionStrategy implements CommissionStrategy {
    @Override
    public double calculateCommission(final double amount) {
        return 0.0;
    }
}

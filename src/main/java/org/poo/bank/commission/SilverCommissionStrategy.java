package org.poo.bank.commission;

import org.poo.utils.Constants;

public final class SilverCommissionStrategy implements CommissionStrategy {
    @Override
    public double calculateCommission(final double amount) {
        Constants constants = Constants.getInstance();
        return amount < constants.getMaximumPayment() ? 0.0
                : amount * constants.getSilverCommision();
    }
}

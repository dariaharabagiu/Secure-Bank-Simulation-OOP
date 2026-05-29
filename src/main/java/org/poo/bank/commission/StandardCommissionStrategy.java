package org.poo.bank.commission;

import org.poo.utils.Constants;

public final class StandardCommissionStrategy implements CommissionStrategy {
    @Override
    public double calculateCommission(final double amount) {
        Constants constants = Constants.getInstance();
        return amount * constants.getStandardCommision();
    }
}

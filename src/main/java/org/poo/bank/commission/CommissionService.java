package org.poo.bank.commission;

public final class CommissionService {

    private CommissionService() {
    }
    /**
     * Determines the appropriate commission strategy based on account plan.
     *
     * @param plan The account plan type.
     * @return An instance of the corresponding CommissionStrategy.
     */
    public static CommissionStrategy getCommissionStrategy(final String plan) {
        switch (plan.toLowerCase()) {
            case "gold":
            case "student":
                return new StudentGoldCommissionStrategy();
            case "silver":
                return new SilverCommissionStrategy();
            case "standard":
                return new StandardCommissionStrategy();
            default:
                throw new IllegalArgumentException("Invalid plan type");
        }
    }
}

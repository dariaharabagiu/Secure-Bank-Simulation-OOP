package org.poo.bank;

import org.poo.utils.Constants;
import org.poo.utils.ExchangeRate;

import java.util.List;

public final class CashbackCalculator {

    private CashbackCalculator() {
    }

    /**
     * Calculates the cashback percentage based on the user's transactions, account type, and the
     * cashback strategy of the specified commerciant.
     *
     * @param user          The user for whom cashback is being calculated.
     * @param account       The account associated with the user.
     * @param commerciant   The commerciant involved in the transactions.
     * @param exchangeRates The list of exchange rates for currency conversion.
     * @return The calculated cashback percentage.
     */
    public static double calculateCashbackPercentage(final User user, final Account account,
                                                     final Commerciant commerciant,
                                                     final List<ExchangeRate> exchangeRates) {
        if (commerciant.getCashbackStrategy().equalsIgnoreCase("nrOfTransactions")) {
            int nrTransactions = getTransactionsCount(user, commerciant, account);
            return calculateTransactionBasedCashback(nrTransactions,
                                                    commerciant.getType(), account);
        } else if (commerciant.getCashbackStrategy().equalsIgnoreCase("spendingThreshold")) {
            double totalSpending = getTotalSpending(user, account, commerciant, exchangeRates);
            return calculateThresholdBasedCashback(totalSpending, user.getPlan());
        }
        return 0;
    }

    /**
     * Counts the number of transactions made by the user with the
     * specified commerciant and account.
     *
     * @param user        The user whose transactions are to be counted.
     * @param commerciant The commerciant involved in the transactions.
     * @param account     The account used for the transactions.
     * @return The number of matching transactions.
     */
    private static int getTransactionsCount(final User user, final Commerciant commerciant,
                                            final Account account) {
        int count = 0;
        for (Transaction transaction : user.getTransactions()) {
            if ((transaction.getReceiverIBAN() != null && transaction.getReceiverIBAN().
                    equals(commerciant.getAccountIBAN())) || transaction.getCommerciantAll() != null
                    && transaction.getCommerciantAll().getName().equals(commerciant.getName())
                    && account.getIban().equals(transaction.getAccountIBAN())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the total spending of the user at the specified commerciant
     * by converting amounts to RON.
     *
     * @param user          The user whose spending is being calculated.
     * @param account       The account used for transactions.
     * @param commerciant   The commerciant involved in the transactions.
     * @param exchangeRates The list of exchange rates for currency conversion.
     * @return The total spending amount in RON.
     */
    private static double getTotalSpending(final User user, final Account account,
                                            final Commerciant commerciant,
                                            final List<ExchangeRate> exchangeRates) {
        double total = 0.0;
        for (Transaction transaction : user.getTransactions()) {
            // We check if the transaction belongs to the specified account and commerciant
            if (transaction.getAccountIBAN() != null
                    && transaction.getAccountIBAN().equals(account.getIban())
                    && transaction.getCommerciantAll() != null
                    && transaction.getCommerciantAll().getCashbackStrategy().
                    equals(commerciant.getCashbackStrategy())) {

            List<ExchangeRate> bidirectionalRates = ExchangeRate.
                                                    generateBidirectionalRates(exchangeRates);
            double money = transaction.getMoney();
            if (money == 0 && transaction.getAmount() != null) {
                String[] parts = transaction.getAmount().split(" ");
                money = Double.parseDouble(parts[0]);
            }
            double convertedAmount = ExchangeRate.convertCurrency(account.getCurrency(),
                    "RON", money, bidirectionalRates);
            total += convertedAmount;
            }
        }
        return total;
    }

    /**
     * Calculates cashback based on the number of transactions
     * made with a specific commerciant type.
     *
     * @param count   The number of transactions made.
     * @param type    The type of commerciant (e.g., Food, Clothes, Tech).
     * @param account The account to which the cashback applies.
     * @return The cashback percentage based on the number of transactions.
     */
    private static double calculateTransactionBasedCashback(final int count,
                                                            final String type,
                                                            final Account account) {
        Constants constants = Constants.getInstance();

        if (type.equals("Food") && count > constants.getFoodThreshold()
                && !account.hasDiscount("Food")) {
            account.addDiscount("Food");
            return constants.getFoodDiscount();
        }
        if (type.equals("Clothes") && count > constants.getClothesThreshold()
                && !account.hasDiscount("Clothes")) {
            account.addDiscount("Clothes");
            return constants.getClothesDiscount();
        }
        if (type.equals("Tech") && count > constants.getTechThreshold()
                && !account.hasDiscount("Tech")) {
            account.addDiscount("Tech");
            return constants.getTechDiscount();
        }
        return 0;
    }

    /**
     * Calculates cashback based on the total spending amount and the user's account plan.
     *
     * @param spending The total spending amount.
     * @param plan     The user's account plan (e.g., standard, silver, gold).
     * @return The cashback percentage based on the spending threshold and plan.
     */
    private static double calculateThresholdBasedCashback(final double spending,
                                                          final String plan) {
        Constants constants = Constants.getInstance();

        if (spending >= constants.getThirdThreshold()) {
            switch (plan) {
                case "standard":
                case "student":
                    return constants.getThirdStudentDiscount();
                case "silver":
                    return constants.getThirdSilverDiscount();
                case "gold":
                    return constants.getThirdGoldDiscount();
                default:
                    throw new IllegalArgumentException("Invalid plan");
            }
        } else if (spending >= constants.getSecondThreshold()) {
            switch (plan) {
                case "standard":
                case "student":
                    return constants.getSecondStudentDiscount();
                case "silver":
                    return constants.getSecondSilverDiscount();
                case "gold":
                    return constants.getSecondGoldDiscount();
                default:
                    throw new IllegalArgumentException("Invalid plan");
            }
        } else if (spending >= constants.getFirstThreshold()) {
            switch (plan) {
                case "standard":
                case "student":
                    return constants.getFirstStudentDiscount();
                case "silver":
                    return constants.getFirstSilverDiscount();
                case "gold":
                    return constants.getFirstGoldDiscount();
                default:
                    throw new IllegalArgumentException("Invalid plan");
            }
        }
        return 0;
    }
}

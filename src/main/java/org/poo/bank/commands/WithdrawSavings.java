package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Account;
import org.poo.bank.Transaction;
import org.poo.utils.Constants;
import org.poo.utils.ExchangeRate;

import java.util.List;

public final class WithdrawSavings implements Command {
    private final UserService userService;
    private final List<ExchangeRate> exchangeRates;

    public WithdrawSavings(final UserService userService, final List<ExchangeRate> exchangeRates) {
        this.userService = userService;
        this.exchangeRates = exchangeRates;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String accountIBAN = input.get("account").asText();
        double amount = input.get("amount").asDouble();
        String currency = input.get("currency").asText();
        int timestamp = input.get("timestamp").asInt();

        User user = userService.getUserByAccount(accountIBAN);

        try {
            Account account = user.findAccountByIBAN(accountIBAN);
            Constants constants = Constants.getInstance();

            if (!(account.getType().equalsIgnoreCase("savings"))) {
                throw new IllegalArgumentException("Account is not of type savings");
            }

            if (!userService.isUserEligibleForWithdrawal(user, constants.getMinimumAge())) {
                throw new IllegalArgumentException("You don't have the minimum age required.");
            }

            Account targetAccount = user.findFirstClassicAccountByCurrency(currency);

            List<ExchangeRate> bidirectionalRates = ExchangeRate.
                    generateBidirectionalRates(exchangeRates);
            double convertedAmount = ExchangeRate.convertCurrency(account.getCurrency(), currency,
                                                                    amount, bidirectionalRates);

            if (account.getBalance() < convertedAmount) {
                throw new IllegalArgumentException("Insufficient funds");
            }

            account.setBalance(account.getBalance() - convertedAmount);
            targetAccount.setBalance(targetAccount.getBalance() + amount);

            Transaction transaction = new Transaction(amount, accountIBAN,
                    "Savings withdrawal", targetAccount.getIban(), timestamp);
            transaction.setAccountIBAN(accountIBAN);
            user.addTransaction(transaction);
            user.addTransaction(transaction);

        } catch (IllegalArgumentException e) {
            Transaction transaction = new Transaction(timestamp, e.getMessage());
            transaction.setAccountIBAN(accountIBAN);
            user.addTransaction(transaction);
        }

        return null;
    }
}

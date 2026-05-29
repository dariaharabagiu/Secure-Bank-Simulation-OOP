package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.Account;
import org.poo.bank.BusinessAccount;
import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Transaction;
import org.poo.utils.ExchangeRate;

import java.util.List;

public final class AddFunds implements Command {
    private final UserService userService;
    private final List<ExchangeRate> exchangeRates;

    public AddFunds(final UserService userService, final List<ExchangeRate> exchangeRates) {
        this.userService = userService;
        this.exchangeRates = exchangeRates;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String email = input.get("email").asText();
        String accountIBAN = input.get("account").asText();
        double amount = input.get("amount").asDouble();
        int timestamp = input.get("timestamp").asInt();

        try {
            Account account = userService.findAccountByIBAN(accountIBAN);
            User ownerAccount = userService.getUserByAccount(accountIBAN);
            User user = userService.getUserByEmail(email);

            if (!account.getType().equalsIgnoreCase("business")) {
                account.setBalance(account.getBalance() + amount);
                return null;
            }

            BusinessAccount businessAccount = (BusinessAccount) account;

            String role = businessAccount.getRole(email);
            List<ExchangeRate> bidirectionalRates = ExchangeRate.
                    generateBidirectionalRates(exchangeRates);
            double maxAmount = businessAccount.getDepositLimit();

            if (role == null) {
                throw new IllegalArgumentException("User not found");
            } else if (role.equalsIgnoreCase("manager")
                    || (role.equalsIgnoreCase("employee") && (amount < maxAmount))) {
                businessAccount.setBalance(businessAccount.getBalance() + amount);
                Transaction transaction = new Transaction(timestamp, amount, "deposit", email);
                transaction.setAccountIBAN(accountIBAN);
                ownerAccount.addTransaction(transaction);
            } else if (role.equalsIgnoreCase("employee") && (amount > maxAmount)) {
                Transaction transaction = new Transaction(timestamp, 0, "deposit", email);
                transaction.setAccountIBAN(accountIBAN);
                ownerAccount.addTransaction(transaction);
            } else {
                businessAccount.setBalance(businessAccount.getBalance() + amount);
            }

        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }

}

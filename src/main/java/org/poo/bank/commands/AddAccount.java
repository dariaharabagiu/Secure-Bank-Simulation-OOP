package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.Account;
import org.poo.bank.SavingsAccount;
import org.poo.bank.BusinessAccount;
import org.poo.bank.UserService;
import org.poo.bank.Transaction;
import org.poo.utils.ExchangeRate;

import java.util.List;

import static org.poo.utils.Utils.generateIBAN;

public final class AddAccount implements Command {
    private final UserService userService;
    private final List<ExchangeRate> exchangeRates;

    public AddAccount(final UserService userService, final List<ExchangeRate> exchangeRates) {
        this.userService = userService;
        this.exchangeRates = exchangeRates;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String email = input.get("email").asText();
        String currency = input.get("currency").asText();
        String accountType = input.get("accountType").asText();
        double interestRate = input.get("interestRate").asDouble();
        int timestamp = input.get("timestamp").asInt();

        try {
            var user = userService.getUserByEmail(email);
            String iban = generateIBAN();
            Account account;
            List<ExchangeRate> bidirectionalRates = ExchangeRate.
                    generateBidirectionalRates(exchangeRates);
            if ("savings".equalsIgnoreCase(accountType)) {
                account = new SavingsAccount(iban, 0.0, currency, "savings",
                        user.getPlan(), bidirectionalRates, interestRate);
                Transaction savingsTransaction = new Transaction(timestamp, "New account created");
                savingsTransaction.setAccountIBAN(iban);
                user.addTransaction(savingsTransaction);
            } else if ("classic".equalsIgnoreCase(accountType)) {
                account = new Account(iban, 0.0, currency, "classic",
                        user.getPlan(), bidirectionalRates);
                Transaction transaction = new Transaction(timestamp, "New account created");
                transaction.setAccountIBAN(iban);
                user.addTransaction(transaction);
            } else if ("business".equalsIgnoreCase(accountType)) {
                account = new BusinessAccount(iban, 0.0, currency, "business",
                        user.getPlan(), bidirectionalRates, email);
                Transaction transaction = new Transaction(timestamp, "New account created");
                transaction.setAccountIBAN(iban);
                user.addTransaction(transaction);
            } else {
                throw new IllegalArgumentException("Invalid account type: " + accountType);
            }

            user.addAccount(account);

        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }

}

package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.utils.ExchangeRate;
import org.poo.bank.SplitPaymentRequest;

import java.util.ArrayList;
import java.util.List;

public final class SplitPayment implements Command {
    private final UserService userService;
    private final List<ExchangeRate> exchangeRates;

    public SplitPayment(final UserService userService, final List<ExchangeRate> exchangeRates) {
        this.userService = userService;
        this.exchangeRates = exchangeRates;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String splitPaymentType = input.get("splitPaymentType").asText();
        List<String> accounts = new ArrayList<>();
        input.get("accounts").forEach(account -> accounts.add(account.asText()));
        List<Double> amounts = new ArrayList<>();
        input.get("amountForUsers").forEach(amount -> amounts.add(amount.asDouble()));
        String currency = input.get("currency").asText();
        int timestamp = input.get("timestamp").asInt();
        double totalAmount = input.get("amount").asDouble();

        List<String> emails = new ArrayList<>();
        for (String accountIBAN : accounts) {
            User user = userService.getUserByAccount(accountIBAN);
            emails.add(user.getEmail());
        }

        if (splitPaymentType.equals("equal")) {
            double amountPerAccount = totalAmount / accounts.size();
            List<Double> equalAmounts = new ArrayList<>();
            for (int i = 0; i < accounts.size(); i++) {
                equalAmounts.add(amountPerAccount);
            }
            SplitPaymentRequest request = new SplitPaymentRequest(accounts, equalAmounts,
                    currency, timestamp, splitPaymentType, totalAmount, userService);
            userService.saveSplitPaymentRequest(emails, splitPaymentType, request);
        } else {
            SplitPaymentRequest request = new SplitPaymentRequest(accounts, amounts,
                    currency, timestamp, splitPaymentType, totalAmount, userService);
            userService.saveSplitPaymentRequest(emails, splitPaymentType, request);
        }
        return null;
    }

}


package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Account;
import org.poo.bank.Transaction;
import org.poo.utils.ExchangeRate;

import java.util.List;

import org.poo.utils.Constants;

public final class UpgradePlan implements Command {
    private final UserService userService;
    private final List<ExchangeRate> exchangeRates;
    private final ObjectMapper objectMapper;

    public UpgradePlan(final UserService userService, final List<ExchangeRate> exchangeRates,
                       final ObjectMapper objectMapper) {
        this.userService = userService;
        this.exchangeRates = exchangeRates;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String newPlan = input.get("newPlanType").asText();
        String accountIBAN = input.get("account").asText();
        int timestamp = input.get("timestamp").asInt();


        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "upgradePlan");

        ObjectNode innerOutput = objectMapper.createObjectNode();

        try {
            Account account = userService.findAccountByIBAN(accountIBAN);
            User user = userService.getUserByAccount(accountIBAN);

            if (!user.getPlan().equals(newPlan)) {
                double fee = getUpgradeFee(user.getPlan(), newPlan);
                List<ExchangeRate> bidirectionalRates = ExchangeRate.
                        generateBidirectionalRates(exchangeRates);

                double convertedFee;
                if ("RON".equalsIgnoreCase(account.getCurrency())) {
                    convertedFee = fee;
                } else {
                    convertedFee = ExchangeRate.convertCurrency("RON",
                            account.getCurrency(), fee, bidirectionalRates);
                }
                if (account.getBalance() < convertedFee) {
                    Transaction transaction = new Transaction(timestamp, "Insufficient funds");
                    transaction.setAccountIBAN(accountIBAN);
                    user.addTransaction(transaction);
                    return null;
                }
                account.setBalance(account.getBalance() - convertedFee);
                try {
                    user.upgradePlan(newPlan);
                } catch (IllegalArgumentException e) {
                    Transaction transaction = new Transaction(timestamp, e.getMessage());
                    transaction.setAccountIBAN(accountIBAN);
                    user.addTransaction(transaction);
                }
            } else {
                Transaction transaction = new Transaction(timestamp,
                        "The user already has the " + newPlan + " plan.");
                transaction.setAccountIBAN(accountIBAN);
                user.addTransaction(transaction);
                return null;
            }

            Transaction transaction = new Transaction(timestamp,
                    "Upgrade plan", accountIBAN, newPlan);
            transaction.setAccountIBAN(accountIBAN);
            user.addTransaction(transaction);

        } catch (IllegalArgumentException e) {
            innerOutput.put("description", e.getMessage());
            innerOutput.put("timestamp", timestamp);
            output.set("output", innerOutput);
            output.put("timestamp", timestamp);
            return output;
        }

        return null;
    }

    private double getUpgradeFee(final String currentPlan, final String newPlan) {
        Constants constants = Constants.getInstance();

        if (currentPlan.equals("standard") || currentPlan.equals("student")) {
            if (newPlan.equals("silver")) {
                return constants.getSilverFee();
            }
            if (newPlan.equals("gold")) {
                return constants.getGoldFee();
            }
        }
        if (currentPlan.equals("silver") && newPlan.equals("gold")) {
            return constants.getSilverToGoldFee();
        }
        return 0;
    }
}

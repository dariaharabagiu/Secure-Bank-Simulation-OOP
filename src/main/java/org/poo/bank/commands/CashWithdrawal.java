package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Account;
import org.poo.bank.Transaction;
import org.poo.bank.commission.CommissionCalculator;
import org.poo.bank.commission.CommissionService;
import org.poo.bank.commission.CommissionStrategy;
import org.poo.utils.ExchangeRate;


import java.util.List;

public final class CashWithdrawal implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final List<ExchangeRate> exchangeRates;

    public CashWithdrawal(final UserService userService, final ObjectMapper objectMapper,
                          final List<ExchangeRate> exchangeRates) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.exchangeRates = exchangeRates;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String cardNumber = input.get("cardNumber").asText();
        double amount = input.get("amount").asDouble();
        String email = input.get("email").asText();
        int timestamp = input.get("timestamp").asInt();

        try {
            User user = userService.getUserByEmail(email);
            Account account = user.findAccountByCardNumber(cardNumber);

            List<ExchangeRate> bidirectionalRates = ExchangeRate.
                    generateBidirectionalRates(exchangeRates);

            double convertedAmount = ExchangeRate.convertCurrency("RON",
                    account.getCurrency(), amount, bidirectionalRates);

            CommissionStrategy strategy = CommissionService.
                    getCommissionStrategy(account.getPlan());
            CommissionCalculator calculator = new CommissionCalculator(strategy);
            double commision = calculator.applyCommission(amount);

            double convertedCommision = ExchangeRate.convertCurrency("RON", account.getCurrency(),
                    commision, bidirectionalRates);

            if (account.getBalance() - account.getMinBalance()
                    > convertedAmount + convertedCommision) {
                account.setBalance(account.getBalance() - convertedAmount - convertedCommision);
                user.addTransaction(new Transaction(amount, timestamp,
                        "Cash withdrawal of " + amount));

            } else if (account.getBalance() - account.getMinBalance()
                    < convertedAmount + convertedCommision) {
                user.addTransaction(new Transaction(timestamp, "Insufficient funds"));
            } else {
                user.addTransaction(new Transaction(timestamp,
                        "Cannot perform payment due to a minimum balance being set"));
            }

        } catch (IllegalArgumentException e) {
            ObjectNode output = objectMapper.createObjectNode();
            output.put("command", "cashWithdrawal");
            ObjectNode innerOutput = objectMapper.createObjectNode();
            innerOutput.put("timestamp", timestamp);
            innerOutput.put("description", e.getMessage());
            output.set("output", innerOutput);
            output.put("timestamp", timestamp);
            return output;
        }

        return null;
    }
}

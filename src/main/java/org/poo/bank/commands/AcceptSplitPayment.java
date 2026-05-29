package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Account;
import org.poo.bank.Transaction;
import org.poo.utils.ExchangeRate;
import org.poo.bank.SplitPaymentRequest;

import java.util.List;

public final class AcceptSplitPayment implements Command {
    private final UserService userService;
    private final List<ExchangeRate> exchangeRates;
    private final ObjectMapper objectMapper;

    public AcceptSplitPayment(final UserService userService, final List<ExchangeRate> exchangeRates,
                              final ObjectMapper objectMapper) {
        this.userService = userService;
        this.exchangeRates = exchangeRates;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String email = input.get("email").asText();
        int timestamp = input.get("timestamp").asInt();
        String splitPaymentType = input.get("splitPaymentType").asText();

        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "acceptSplitPayment");

        ObjectNode innerOutput = objectMapper.createObjectNode();

        try {
            User user = userService.getUserByEmail(email);

            SplitPaymentRequest request = userService.findSplitPaymentRequest(email,
                                                        splitPaymentType);

            request.acceptPayment(email, splitPaymentType, true);

            if (request.isFullyAccepted()) {
                processSplitPayment(request);
                for (String accountIBAN : request.getAccounts()) {
                    User participant = userService.getUserByAccount(accountIBAN);
                    userService.removeSplitPaymentRequest(participant.getEmail(), splitPaymentType);
                }
            }

        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("No pending split payment found for user")) {
                return null;
            }
            innerOutput.put("description", e.getMessage());
            innerOutput.put("timestamp", timestamp);
            output.set("output", innerOutput);
            output.put("timestamp", timestamp);
            return output;
        }

        return null;
    }


    private void processSplitPayment(final SplitPaymentRequest request) {
        List<String> accounts = request.getAccounts();
        List<Double> amounts = request.getAmounts();
        String currency = request.getCurrency();
        int timestamp = request.getTimestamp();
        double totalAmount = request.getTotalAmount();

        List<ExchangeRate> bidirectionalRates = ExchangeRate.
                generateBidirectionalRates(exchangeRates);

        String accountIBANError = null;

        for (int i = 0; i < accounts.size(); i++) {
            String accountIBAN = accounts.get(i);
            double amountForUser = amounts.get(i);

            Account account = userService.findAccountByIBAN(accountIBAN);
            double convertedAmount = ExchangeRate.convertCurrency(currency, account.getCurrency(),
                    amountForUser, bidirectionalRates);

            // Check if the user has enough funds
            if (account.getBalance() < convertedAmount) {
                accountIBANError = accountIBAN;
                break;
            }
        }

        if (accountIBANError != null) {
            for (String accountIBAN : accounts) {
                User user = userService.getUserByAccount(accountIBAN);
                String description = String.format("Split payment of %.2f %s",
                        totalAmount, currency);
                String error = "Account " + accountIBANError
                        + " has insufficient funds for a split payment.";
                if (request.getSplitPaymentType().equals("equal")) {
                    Transaction errortransaction = new Transaction(amounts.get(1),
                            timestamp, description, request.getSplitPaymentType(),
                            currency, accounts, error);
                    errortransaction.setAccountIBAN(accountIBAN);
                    user.addTransaction(errortransaction);
                    user.setSplitTransaction(true);
                } else {
                    Transaction errortransaction = new Transaction(timestamp, description,
                            request.getSplitPaymentType(), currency, amounts, accounts, error);
                    errortransaction.setAccountIBAN(accountIBAN);
                    user.addTransaction(errortransaction);
                    user.setSplitTransaction(true);
                }

            }
        } else {
            for (int i = 0; i < accounts.size(); i++) {
                String accountIBAN = accounts.get(i);
                double amountForUser = amounts.get(i);

                Account account = userService.findAccountByIBAN(accountIBAN);
                double convertedAmount = ExchangeRate.convertCurrency(currency,
                        account.getCurrency(), amountForUser, bidirectionalRates);

                account.setBalance(account.getBalance() - convertedAmount);

                User user = userService.getUserByAccount(accountIBAN);
                if (request.getSplitPaymentType().equals("custom")) {
                    Transaction transaction = new Transaction(
                            timestamp,
                            String.format("Split payment of %.2f %s", totalAmount, currency),
                            request.getSplitPaymentType(),
                            currency,
                            amounts,
                            accounts
                    );
                    transaction.setAccountIBAN(accountIBAN);
                    user.addTransaction(transaction);
                } else {
                    Transaction transaction = new Transaction(amounts.get(1), timestamp,
                            String.format("Split payment of %.2f %s", totalAmount, currency),
                            request.getSplitPaymentType(), currency, accounts);
                    transaction.setAccountIBAN(accountIBAN);
                    user.addTransaction(transaction);
                }
            }
        }
    }
}

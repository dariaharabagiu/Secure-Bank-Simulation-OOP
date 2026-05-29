package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Transaction;
import org.poo.bank.SplitPaymentRequest;
import java.util.List;

public final class RejectSplitPayment implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public RejectSplitPayment(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String email = input.get("email").asText();
        String splitPaymentType = input.get("splitPaymentType").asText();
        int timestamp = input.get("timestamp").asInt();

        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "rejectSplitPayment");

        ObjectNode innerOutput = objectMapper.createObjectNode();

        try {
            User user = userService.getUserByEmail(email);

            // Find the payment request for the specific user and type
            SplitPaymentRequest request = userService.
                    findSplitPaymentRequest(email, splitPaymentType);

            // We mark the user as rejecting the payment
            request.acceptPayment(email, splitPaymentType, false);

            // Remove the request from the system
            rejectSplitPayment(request);

            for (String accountIBAN : request.getAccounts()) {
                User participant = userService.getUserByAccount(accountIBAN);
                userService.removeSplitPaymentRequest(participant.getEmail(), splitPaymentType);
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


    private void rejectSplitPayment(final SplitPaymentRequest request) {
        List<String> accounts = request.getAccounts();
        List<Double> amounts = request.getAmounts();
        String currency = request.getCurrency();
        int timestamp = request.getTimestamp();

        for (String accountIBAN : accounts) {
            User user = userService.getUserByAccount(accountIBAN);
            String description = String.format(
                    "Split payment of %.2f %s",
                    request.getTotalAmount(), request.getCurrency()
            );
            String error = "One user rejected the payment.";
            if (request.getSplitPaymentType().equals("equal")) {
                Transaction errorTransaction = new Transaction(amounts.get(1),
                        timestamp, description, request.getSplitPaymentType(),
                        currency, accounts, error);
                errorTransaction.setAccountIBAN(accountIBAN);
                user.addTransaction(errorTransaction);
                user.setSplitTransaction(true);
            } else {
                Transaction errorTransaction = new Transaction(timestamp,
                        description, request.getSplitPaymentType(),
                        currency, amounts, accounts, error);
                errorTransaction.setAccountIBAN(accountIBAN);
                user.addTransaction(errorTransaction);
                user.setSplitTransaction(true);
            }
        }
    }
}

package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.Transaction;
import org.poo.bank.User;
import org.poo.bank.UserService;


import java.util.HashMap;
import java.util.Map;

public final class SpendingsReport implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public SpendingsReport(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        int timestamp = input.get("timestamp").asInt();
        String accountIBAN = input.get("account").asText();
        int startTimestamp = input.get("startTimestamp").asInt();
        int endTimestamp = input.get("endTimestamp").asInt();

        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "spendingsReport");

        ObjectNode innerOutput = objectMapper.createObjectNode();
        ArrayNode transactionsArray = objectMapper.createArrayNode();
        ArrayNode commerciantsArray = objectMapper.createArrayNode();
        Map<String, Double> commerciantTotals = new HashMap<>();

        try {
            Account account = userService.findAccountByIBAN(accountIBAN);

            if (account.getType().equalsIgnoreCase("savings")) {
                innerOutput.put("error", "This kind of report is"
                        + " not supported for a saving account");
                output.set("output", innerOutput);
                output.put("timestamp", timestamp);
                return output;
            }

            innerOutput.put("IBAN", account.getIban());
            innerOutput.put("balance", account.getBalance());
            innerOutput.put("currency", account.getCurrency());

            User user = userService.getUserByAccount(accountIBAN);

            for (Transaction transaction : user.getTransactions()) {
                if (transaction.getTimestamp() >= startTimestamp
                        && transaction.getTimestamp() <= endTimestamp
                        && accountIBAN.equalsIgnoreCase(transaction.getAccountIBAN())
                        && "Card payment".equalsIgnoreCase(transaction.getDescription())) {

                    ObjectNode transactionNode = transaction.toJson(objectMapper);
                    transactionsArray.add(transactionNode);

                    String commerciant = transaction.getCommerciant();
                    double amount = transaction.getMoney();
                    commerciantTotals.merge(commerciant, amount, Double::sum);

                }
            }

            innerOutput.set("transactions", transactionsArray);


            // Add commerciant totals to the commerciants array
            commerciantTotals.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()) // Sort alphabetically by commerciant name
                    .forEach(entry -> {
                        ObjectNode commerciantNode = objectMapper.createObjectNode();
                        commerciantNode.put("commerciant", entry.getKey());
                        commerciantNode.put("total", entry.getValue());
                        commerciantsArray.add(commerciantNode);
                    });

            innerOutput.set("commerciants", commerciantsArray);

        } catch (IllegalArgumentException e) {
            innerOutput.put("description", e.getMessage());
            innerOutput.put("timestamp", timestamp);
        }

        output.set("output", innerOutput);
        output.put("timestamp", timestamp);
        return output;
    }
}

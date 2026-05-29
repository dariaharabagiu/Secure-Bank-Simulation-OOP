package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.Transaction;
import org.poo.bank.User;
import org.poo.bank.UserService;

public final class Report implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public Report(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        int timestamp = input.get("timestamp").asInt();
        String accountIBAN = input.get("account").asText();
        int endTimestamp = input.get("endTimestamp").asInt();
        int startTimestamp = input.get("startTimestamp").asInt();

        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "report");

        ObjectNode innerOutput = objectMapper.createObjectNode();
        ArrayNode transactions = objectMapper.createArrayNode();

        try {
            Account account = userService.findAccountByIBAN(accountIBAN);

            innerOutput.put("IBAN", account.getIban());
            innerOutput.put("balance", account.getBalance());
            innerOutput.put("currency", account.getCurrency());

            User user = userService.getUserByAccount(accountIBAN);
            for (Transaction transaction : user.getTransactions()) {
                if (transaction.getTimestamp() >= startTimestamp
                        && transaction.getTimestamp() <= endTimestamp
                        && accountIBAN.equalsIgnoreCase(transaction.getAccountIBAN())) {
                    ObjectNode transactionNode = transaction.toJson(objectMapper);
                    transactions.add(transactionNode);
                }
            }
            innerOutput.set("transactions", transactions);
        } catch (IllegalArgumentException e) {
            innerOutput.put("description", e.getMessage());
            innerOutput.put("timestamp", timestamp);
        }
        output.set("output", innerOutput);
        output.put("timestamp", timestamp);
        return output;
    }
}

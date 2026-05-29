package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Transaction;
import org.poo.bank.User;
import org.poo.bank.UserService;

import java.util.List;
import java.util.Comparator;

public final class PrintTransactions implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public PrintTransactions(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String email = input.get("email").asText();
        int timestamp = input.get("timestamp").asInt();

        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "printTransactions");

        try {
            User user = userService.getUserByEmail(email);
            List<Transaction> transactions = user.getTransactions();

            //Sort transactions by timestamp
            transactions.sort(Comparator.comparingInt(Transaction::getTimestamp));

            ArrayNode transactionsArray = objectMapper.createArrayNode();

            for (Transaction transaction : transactions) {
                ObjectNode transactionNode = transaction.toJson(objectMapper);
                transactionsArray.add(transactionNode);
            }

            output.set("output", transactionsArray);
            output.put("timestamp", timestamp);

        } catch (IllegalArgumentException e) {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("description", e.getMessage());
            output.set("output", errorNode);
        }
        return output;
    }
}

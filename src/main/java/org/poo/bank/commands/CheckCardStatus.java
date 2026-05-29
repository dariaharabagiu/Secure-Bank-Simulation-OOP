package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.Card;
import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Transaction;


public final class CheckCardStatus implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public CheckCardStatus(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        int timestamp = input.get("timestamp").asInt();
        String cardNumber = input.get("cardNumber").asText();

        ObjectNode output = objectMapper.createObjectNode();

        try {
            User user = userService.getUserByCardNumber(cardNumber);
            Card card = user.findCardByCardNumber(cardNumber);
            Account account = user.findAccountByCardNumber(cardNumber);

            if (account.getBalance() == 0.0) {
                String description = "You have reached the minimum amount of funds,"
                        + " the card will be frozen";
                user.addTransaction(new Transaction(timestamp, description));
            }

            if (account.getBalance() <= account.getMinBalance()) {
                card.setStatus("frozen");
            }

        } catch (IllegalArgumentException e) {
            output.put("command", "checkCardStatus");
            ObjectNode innerOutput = objectMapper.createObjectNode();
            innerOutput.put("timestamp", timestamp);
            innerOutput.put("description", "Card not found");
            output.set("output", innerOutput);
            output.put("timestamp", timestamp);
            return output;
        }
        return null;
    }
}

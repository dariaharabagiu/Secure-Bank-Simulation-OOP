package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.BusinessAccount;
import org.poo.bank.User;
import org.poo.bank.UserService;

public final class ChangeDepositLimit implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public ChangeDepositLimit(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String accountIBAN = input.get("account").asText();
        String email = input.get("email").asText();
        double newLimit = input.get("amount").asDouble();
        int timestamp = input.get("timestamp").asInt();

        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "changeDepositLimit");

        ObjectNode innerOutput = objectMapper.createObjectNode();

        try {
            Account account = userService.findAccountByIBAN(accountIBAN);
            User user = userService.getUserByAccount(accountIBAN);

            if (!account.getType().equals("business")) {
                throw new IllegalArgumentException("Not business account");
            }

            BusinessAccount businessAccount = (BusinessAccount) account;

            if (!businessAccount.getOwnerEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("You must be owner"
                                + " in order to change deposit limit.");
            }

            businessAccount.setDepositLimit(email, newLimit);
        } catch (IllegalArgumentException e) {
            innerOutput.put("description", e.getMessage());
            innerOutput.put("timestamp", timestamp);
            output.set("output", innerOutput);
            output.put("timestamp", timestamp);
            return output;
        }

        return null;
    }
}

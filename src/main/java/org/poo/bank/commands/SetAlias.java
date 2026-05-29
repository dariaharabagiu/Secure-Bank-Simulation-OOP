package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Alias;
import org.poo.bank.User;
import org.poo.bank.UserService;

public final class SetAlias implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public SetAlias(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String email = input.get("email").asText();
        String alias = input.get("alias").asText();
        String accountIBAN = input.get("account").asText();
        int timestamp = input.get("timestamp").asInt();

        try {
            User user = userService.getUserByEmail(email);
            user.findAccountByIBAN(accountIBAN);

            Alias newAlias = new Alias(email, alias, accountIBAN);
            user.addAlias(newAlias);

        } catch (IllegalArgumentException e) {
            ObjectNode output = objectMapper.createObjectNode();
            output.put("command", "setAlias");
            output.put("timestamp", timestamp);
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("timestamp", timestamp);
            errorNode.put("description", e.getMessage());
            output.set("output", errorNode);
        }
        return null;
    }
}

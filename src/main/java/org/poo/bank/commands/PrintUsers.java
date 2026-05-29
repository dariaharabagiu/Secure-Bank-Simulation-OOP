package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.UserService;

public final class PrintUsers implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public PrintUsers(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "printUsers");

        ArrayNode usersArray = objectMapper.createArrayNode();
        userService.getAllUsers().forEach(user -> usersArray.add(user.toJson(objectMapper)));

        output.set("output", usersArray);
        output.put("timestamp", input.get("timestamp").asInt());
        return output;
    }
}

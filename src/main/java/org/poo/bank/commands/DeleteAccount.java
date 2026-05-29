package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.Transaction;
import org.poo.bank.User;
import org.poo.bank.UserService;

public final class DeleteAccount implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public DeleteAccount(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String email = input.get("email").asText();
        String accountIBAN = input.get("account").asText();
        int timestamp = input.get("timestamp").asInt();

        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "deleteAccount");

        ObjectNode innerOutput = objectMapper.createObjectNode();

        try {
            User user = userService.getUserByEmail(email);
            Account account = user.findAccountByIBAN(accountIBAN);

            // Check if the account balance is zero
            if (account.getBalance() != 0) {
                user.addTransaction(new Transaction(timestamp, "Account couldn't be deleted "
                        + "- there are funds remaining"));
                innerOutput.put("error", "Account couldn't be deleted "
                        + "- see org.poo.transactions for details");
                innerOutput.put("timestamp", timestamp);
                output.set("output", innerOutput);
                output.put("timestamp", timestamp);
                return output;
            }

            user.deleteAccount(account, timestamp);

            innerOutput.put("success", "Account deleted");
            innerOutput.put("timestamp", timestamp);
        } catch (IllegalArgumentException e) {
            innerOutput.put("description", e.getMessage());
        }
        output.set("output", innerOutput);
        output.put("timestamp", timestamp);
        return output;
    }
}

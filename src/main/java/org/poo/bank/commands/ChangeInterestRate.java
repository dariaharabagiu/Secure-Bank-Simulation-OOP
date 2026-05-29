package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.SavingsAccount;
import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Transaction;


public final class ChangeInterestRate implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public ChangeInterestRate(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        double interestRate = input.get("interestRate").asDouble();
        String accountIBAN = input.get("account").asText();
        String command = input.get("command").asText();
        int timestamp = input.get("timestamp").asInt();
        ObjectNode output = objectMapper.createObjectNode();

        try {
            User user = userService.getUserByAccount(accountIBAN);
            Account account = user.findAccountByIBAN(accountIBAN);

            if (account.getType().equalsIgnoreCase("savings")) {
                SavingsAccount savingsAccount = (SavingsAccount) account;
                savingsAccount.setInterestRate(interestRate);
                String description = "Interest rate of the account changed to " + interestRate;
                Transaction transaction = new Transaction(timestamp, description);
                transaction.setAccountIBAN(accountIBAN);
                user.addTransaction(transaction);
            } else {
                throw new IllegalArgumentException("This is not a savings account");
            }

        } catch (IllegalArgumentException e) {
            output.put("command", command);
            ObjectNode innerOutput = objectMapper.createObjectNode();
            innerOutput.put("description", e.getMessage());
            innerOutput.put("timestamp", timestamp);
            output.set("output", innerOutput);
            output.put("timestamp", timestamp);
            return output;
        }
        return null;
    }
}

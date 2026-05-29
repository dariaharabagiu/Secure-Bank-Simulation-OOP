package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.UserService;

public final class SetMinimumBalance implements Command {
    private final UserService userService;

    public SetMinimumBalance(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String accountIBAN = input.get("account").asText();
        double minBalance = input.get("amount").asDouble();

        try {
            Account account = userService.findAccountByIBAN(accountIBAN);
            account.setMinBalance(minBalance);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }
}

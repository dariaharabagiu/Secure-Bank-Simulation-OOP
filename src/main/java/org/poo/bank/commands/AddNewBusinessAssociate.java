package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.BusinessAccount;
import org.poo.bank.User;
import org.poo.bank.UserService;

public final class AddNewBusinessAssociate implements Command {
    private final UserService userService;

    public AddNewBusinessAssociate(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String accountIBAN = input.get("account").asText();
        String role = input.get("role").asText();
        String email = input.get("email").asText();

        try {
            Account account = userService.findAccountByIBAN(accountIBAN);
            User user = userService.getUserByAccount(accountIBAN);
            if (!account.getType().equalsIgnoreCase("business")) {
                throw new IllegalArgumentException("This is not a business account");
            }
            BusinessAccount businessAccount = (BusinessAccount) account;
            if (!businessAccount.getOwnerEmail().equals(user.getEmail())) {
                throw new IllegalArgumentException("This is not the owner");
            }

            if (businessAccount.isAssociate(email)) {
                return null;
            }

            businessAccount.addAssociate(email, role);
    } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }
}

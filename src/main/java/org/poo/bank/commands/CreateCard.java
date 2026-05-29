package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.BusinessAccount;
import org.poo.bank.Card;
import org.poo.bank.UserService;
import org.poo.bank.Transaction;


import static org.poo.utils.Utils.generateCardNumber;

public final class CreateCard implements Command {
    private final UserService userService;

    public CreateCard(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String accountIBAN = input.get("account").asText();
        String email = input.get("email").asText();
        int timestamp = input.get("timestamp").asInt();

        try {
            var user = userService.getUserByEmail(email);
            Account account = user.findAccountByIBAN(accountIBAN);

            if (account.getType().equalsIgnoreCase("business")) {
                BusinessAccount businessAccount = (BusinessAccount) account;
                String role = businessAccount.getRole(email);

                if (role == null) {
                    throw new IllegalArgumentException("You are not"
                            + " authorized to create this card");
                }

                if (role.equals("employee") && !user.equals(userService.
                        getUserByAccount(accountIBAN))) {
                    throw new IllegalArgumentException("Employees can only "
                            + "create cards for themselves");
                }
            }

            String cardNumber = generateCardNumber();
            Card card = new Card(cardNumber, "active");
            account.addCard(card);
            Transaction transaction = new Transaction(timestamp, "New card created",
                    cardNumber, email, accountIBAN);
            transaction.setAccountIBAN(accountIBAN);
            user.addTransaction(transaction);
        } catch (IllegalArgumentException e) {
            System.err.println("An exception occurred: " + e.getMessage());
        }
        return null;
    }
}


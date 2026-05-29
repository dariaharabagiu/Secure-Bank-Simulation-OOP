package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.Card;
import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Transaction;


public final class DeleteCard implements Command {
    private final UserService userService;

    public DeleteCard(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String email = input.get("email").asText();
        String cardNumber = input.get("cardNumber").asText();
        int timestamp = input.get("timestamp").asInt();

        try {
            User user = userService.getUserByEmail(email);
            Account account = user.findAccountByCardNumber(cardNumber);
            if (account.getBalance() > account.getMinBalance()) {
                return null;
            }
            Card card = user.findCardByCardNumber(cardNumber);
            account.deleteCard(card);
            user.addTransaction(new Transaction(timestamp, "The card has been destroyed",
                    cardNumber, email, account.getIban()));

        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }
}

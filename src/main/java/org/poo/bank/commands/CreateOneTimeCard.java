package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.Card;
import org.poo.bank.Transaction;
import org.poo.bank.UserService;

import static org.poo.utils.Utils.generateCardNumber;

public final class CreateOneTimeCard implements Command {
    private final UserService userService;

    public CreateOneTimeCard(final UserService userService) {
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
            String cardNumber = generateCardNumber();
            Card card = new Card(cardNumber, "active");
            card.setOneTime(true);
            account.addCard(card);
            Transaction transaction = new Transaction(timestamp, "New card created",
                    cardNumber, email, accountIBAN);
            transaction.setAccountIBAN(accountIBAN);
            user.addTransaction(transaction);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }
}

package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.utils.ExchangeRate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


@Setter
@Getter
public class Account {
    private final List<ExchangeRate> exchangeRates;
    private String iban;
    private double balance;
    private String currency;
    private String type;
    private String plan;
    private List<Card> cards;
    private double minBalance;
    private Map<String, Boolean> discount = new HashMap<>();

    public Account(final String iban, final double balance,
                   final String currency, final String type, final String plan,
                   final List<ExchangeRate> exchangeRates) {
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
        this.type = type;
        this.plan = plan;
        this.cards = new ArrayList<>();
        this.minBalance = 0;
        this.exchangeRates = exchangeRates;
    }


    /**
     * Adds a card to the list of cards associated with this account.
     *
     * @param card the {@code Card} to be added
     */
    public void addCard(final Card card) {
        cards.add(card);
    }

    /**
     * Removes a card from the list of cards associated with this account.
     *
     * @param card the {@code Card} to be removed
     */
    public void deleteCard(final Card card) {
        cards.remove(card);
    }

    /**
     * Converts the account and its associated details into a JSON representation.
     *
     * @param objectMapper the {@code ObjectMapper} used to create the JSON structure
     * @return an {@code ObjectNode} containing the JSON representation of the account
     */
    public ObjectNode toJson(final ObjectMapper objectMapper) {
        ObjectNode accountNode = objectMapper.createObjectNode();
        accountNode.put("IBAN", iban);
        accountNode.put("balance", balance);
        accountNode.put("currency", currency);
        accountNode.put("type", type);

        ArrayNode cardsArray = objectMapper.createArrayNode();
        for (Card card : cards) {
            cardsArray.add(card.toJson(objectMapper));
        }

        accountNode.set("cards", cardsArray);
        return accountNode;
    }

    /**
     * Checks if a discount of a given type has already been received.
     *
     * @param disscountType The type of discount to check.
     * @return {@code true} if the discount of the specified type
     * has been received, otherwise {@code false}.
     */
    public boolean hasDiscount(final String disscountType) {
        return discount.getOrDefault(disscountType, false);
    }

    /**
     * Adds a discount of the specified type to the record.
     *
     * @param disscountType The type of discount to add.
     */
    public void addDiscount(final String disscountType) {
        discount.put(disscountType, true);
    }
}

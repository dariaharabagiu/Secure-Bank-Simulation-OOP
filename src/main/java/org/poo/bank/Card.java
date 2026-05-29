package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class Card {
    private String cardNumber;
    private String status;
    private boolean oneTime;

    public Card(final String cardNumber, final String status) {
        this.cardNumber = cardNumber;
        this.status = status;
        this.oneTime = false;
    }

    /**
     * Converts the current Card object into a JSON representation.
     *
     * @param objectMapper the ObjectMapper instance used to create the JSON object.
     * @return an ObjectNode containing the card's number and status as JSON fields.
     */
    public ObjectNode toJson(final ObjectMapper objectMapper) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("cardNumber", cardNumber);
        cardNode.put("status", status);
        return cardNode;
    }
}

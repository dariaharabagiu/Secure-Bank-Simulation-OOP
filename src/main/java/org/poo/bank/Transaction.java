package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public final class Transaction {
    private int timestamp;
    private String description;
    private String senderIBAN;
    private String receiverIBAN;
    private String amount;
    private String transferType;
    private String cardNumber;
    private String cardHolder;
    private String account;
    private double money;
    private String commerciant;
    private Commerciant commerciantAll;
    private String currency;
    private List<String> involvedAccounts;
    private String accountIBAN;
    private String error;
    private String plan;
    private String iban;
    private String splitPaymentType;
    private List<Double> amountForUsers;
    private String type;
    private String responsible;
    private String targetAccount;
    private String savingsAccount;
    private String commerciantName;
    private String currencyForPlan;

    public Transaction(final int timestamp, final String description, final String senderIBAN,
                       final String receiverIBAN, final String amount, final String transferType) {
        this.timestamp = timestamp;
        this.description = description;
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.transferType = transferType;
    }

    public Transaction(final int timestamp, final String description, final String senderIBAN,
                       final String receiverIBAN, final String amount, final String transferType,
                       final String commerciantName) {
        this.timestamp = timestamp;
        this.description = description;
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.transferType = transferType;
        this.commerciantName = commerciantName;
    }

    public Transaction(final int timestamp, final String description, final String senderIBAN,
                       final String receiverIBAN, final String amount, final String transferType,
                       final String commerciantName, final Commerciant commerciantAll) {
        this.timestamp = timestamp;
        this.description = description;
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.transferType = transferType;
        this.commerciantName = commerciantName;
        this.commerciantAll = commerciantAll;
    }

    public Transaction(final int timestamp, final String description, final String cardNumber,
                       final String cardHolder, final String account) {
        this.timestamp = timestamp;
        this.description = description;
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.account = account;
    }

    public Transaction(final int timestamp, final String description) {
        this.timestamp = timestamp;
        this.description = description;
    }

    public Transaction() {
    }

    public Transaction(final int timestamp, final String description,
                       final double money, final String commerciant, final String type,
                       final  String responsible) {
        this.timestamp = timestamp;
        this.description = description;
        this.money = money;
        this.commerciant = commerciant;
        this.commerciantName = commerciant;
        this.type = type;
        this.responsible = responsible;
    }

    public Transaction(final int timestamp, final String description,
                   final double money, final String commerciant, final String type,
                       final String responsible, final Commerciant commerciantAll,
                       final String currencyForPlan) {
        this.timestamp = timestamp;
        this.description = description;
        this.money = money;
        this.commerciant = commerciant;
        this.commerciantName = commerciant;
        this.type = type;
        this.responsible = responsible;
        this.commerciantAll = commerciantAll;
        this.currencyForPlan = currencyForPlan;
    }

    public Transaction(final int timestamp, final String description, final String currency,
                       final double money, final List<String> involvedAccounts) {
        this.timestamp = timestamp;
        this.description = description;
        this.currency = currency;
        this.money = money;
        this.involvedAccounts = involvedAccounts;
    }

    public Transaction(final int timestamp, final String description,
                       final String currency, final double money,
                       final List<String> involvedAccounts, final String error) {
        this.timestamp = timestamp;
        this.description = description;
        this.currency = currency;
        this.money = money;
        this.involvedAccounts = involvedAccounts;
        this.error = error;
    }

    public Transaction(final int timestamp, final String description,
                       final String accountIBAN, final String plan) {
        this.timestamp = timestamp;
        this.description = description;
        this.iban = accountIBAN;
        this.plan = plan;
    }

    public Transaction(final double money, final int timestamp, final String description) {
        this.money = money;
        this.timestamp = timestamp;
        this.description = description;
    }

    public Transaction(final double money, final String currency,
                       final String description, final int timestamp) {
        this.money = money;
        this.currency = currency;
        this.description = description;
        this.timestamp = timestamp;
    }

    public Transaction(final int timestamp, final String description, final String splitPaymentType,
                       final String currency, final List<Double> amountForUsers,
                       final List<String> involvedAccounts) {
        this.timestamp = timestamp;
        this.description = description;
        this.splitPaymentType = splitPaymentType;
        this.currency = currency;
        this.amountForUsers = amountForUsers;
        this.involvedAccounts = involvedAccounts;
    }

    public Transaction(final String error, final int timestamp, final String description,
                       final String splitPaymentType, final String currency,
                       final List<Double> amountForUsers, final List<String> involvedAccounts) {
        this.timestamp = timestamp;
        this.description = description;
        this.splitPaymentType = splitPaymentType;
        this.currency = currency;
        this.amountForUsers = amountForUsers;
        this.involvedAccounts = involvedAccounts;
        this.error = error;
    }

    public Transaction(final double money, final int timestamp, final String description,
                       final String splitPaymentType, final String currency,
                       final List<String> involvedAccounts) {
        this.money = money;
        this.timestamp = timestamp;
        this.description = description;
        this.splitPaymentType = splitPaymentType;
        this.currency = currency;
        this.involvedAccounts = involvedAccounts;
    }

    public Transaction(final int timestamp, final String description, final String splitPaymentType,
                       final String currency, final List<Double> amountForUsers,
                       final List<String> involvedAccounts, final String error) {
        this.timestamp = timestamp;
        this.description = description;
        this.splitPaymentType = splitPaymentType;
        this.currency = currency;
        this.amountForUsers = amountForUsers;
        this.involvedAccounts = involvedAccounts;
        this.error = error;
    }

    public Transaction(final double money, final int timestamp, final String description,
                       final String splitPaymentType, final String currency,
                       final List<String> involvedAccounts, final String error) {
        this.money = money;
        this.timestamp = timestamp;
        this.description = description;
        this.splitPaymentType = splitPaymentType;
        this.currency = currency;
        this.involvedAccounts = involvedAccounts;
        this.error = error;
    }

    public Transaction(final int timestamp, final double money,
                       final String type, final String responsible) {
        this.timestamp = timestamp;
        this.money = money;
        this.type = type;
        this.responsible = responsible;
    }

    public Transaction(final double money, final String savingsAccount, final String description,
                       final String targetAccount, final int timestamp) {
        this.money = money;
        this.savingsAccount = savingsAccount;
        this.description = description;
        this.targetAccount = targetAccount;
        this.timestamp = timestamp;
    }

    /**
     * Converts this {@code Transaction} object into a JSON representation.
     *
     * @param objectMapper the {@code ObjectMapper} used to create the JSON structure
     * @return an {@code ObjectNode} representing this transaction in JSON format
     *         with fields for timestamp, description, sender/receiver IBANs,
     *         amount, transfer type, card details, account details, currency,
     *         involved accounts, and error (if applicable)
     */
    public ObjectNode toJson(final ObjectMapper objectMapper) {
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("timestamp", timestamp);
        transactionNode.put("description", description);

        if (iban != null) {
            transactionNode.put("accountIBAN", iban);
        }
        if (plan != null) {
            transactionNode.put("newPlanType", plan);
        }
        if (senderIBAN != null) {
            transactionNode.put("senderIBAN", senderIBAN);
        }
        if (receiverIBAN != null) {
            transactionNode.put("receiverIBAN", receiverIBAN);
        }
        if (amount != null) {
            transactionNode.put("amount", amount);
        }
        if (transferType != null) {
            transactionNode.put("transferType", transferType);
        }
        if (cardNumber != null) {
            transactionNode.put("card", cardNumber);
        }
        if (cardHolder != null) {
            transactionNode.put("cardHolder", cardHolder);
        }
        if (account != null) {
            transactionNode.put("account", account);
        }
        if (currency != null) {
            transactionNode.put("currency", currency);
        }
        if (money != 0) {
            transactionNode.put("amount", money);
        }
        if (commerciant != null) {
            transactionNode.put("commerciant", commerciant);
        }
        if (error != null) {
            transactionNode.put("error", error);
        }
        if (involvedAccounts != null) {
            ArrayNode accountsArray = objectMapper.createArrayNode();
            for (String involvedAccount : involvedAccounts) {
                accountsArray.add(involvedAccount);
            }
            transactionNode.set("involvedAccounts", accountsArray);
        }
        if (amountForUsers != null) {
            ArrayNode amountArray = objectMapper.createArrayNode();
            for (double amountForUser : amountForUsers) {
                amountArray.add(amountForUser);
            }
            transactionNode.set("amountForUsers", amountArray);
        }
        if (splitPaymentType != null) {
            transactionNode.put("splitPaymentType", splitPaymentType);
        }
        if (savingsAccount != null) {
            transactionNode.put("savingsAccountIBAN", savingsAccount);
        }
        if (targetAccount != null) {
            transactionNode.put("classicAccountIBAN", targetAccount);
        }

        return transactionNode;
    }

}

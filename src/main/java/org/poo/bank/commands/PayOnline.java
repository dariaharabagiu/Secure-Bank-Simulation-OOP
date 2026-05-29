package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.Card;
import org.poo.bank.CommerciantService;
import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Account;
import org.poo.bank.BusinessAccount;
import org.poo.bank.Transaction;
import org.poo.bank.Commerciant;
import org.poo.bank.commission.CommissionCalculator;
import org.poo.bank.commission.CommissionService;
import org.poo.bank.commission.CommissionStrategy;
import org.poo.utils.ExchangeRate;
//import org.poo.bank.commission.CommisionCalculator;
import org.poo.bank.CashbackCalculator;


import java.util.List;

import static org.poo.utils.Utils.generateCardNumber;

public final class PayOnline implements Command {
    private final UserService userService;
    private final List<ExchangeRate> exchangeRates;
    private final ObjectMapper objectMapper;
    private final CommerciantService commerciantService;

    public PayOnline(final UserService userService, final List<ExchangeRate> exchangeRates,
                     final ObjectMapper objectMapper, final CommerciantService commerciantService) {
        this.userService = userService;
        this.exchangeRates = exchangeRates;
        this.objectMapper = objectMapper;
        this.commerciantService = commerciantService;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String cardNumber = input.get("cardNumber").asText();
        double amount = input.get("amount").asDouble();
        String paymentCurrency = input.get("currency").asText();
        String email = input.get("email").asText();
        int timestamp = input.get("timestamp").asInt();
        String commerciantName = input.get("commerciant").asText();

        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "payOnline");

        ObjectNode innerOutput = objectMapper.createObjectNode();
        innerOutput.put("timestamp", timestamp);

        if (amount == 0) {
            return null;
        }

        try {
            var user = userService.getUserByEmail(email);
            Card card = userService.findCardByCardNumber(cardNumber);

            if (card.getStatus().equals("frozen")) {
                user.addTransaction(new Transaction(timestamp, "The card is frozen"));
                return null;
            }

            User owner = userService.getUserByCardNumber(cardNumber);
            Account ownerAccount = owner.findAccountByCardNumber(cardNumber);

            Account account;
            double maxAmount = 0;
            boolean isEmployee = false;

            if (ownerAccount.getType().equals("business")) {
                BusinessAccount businessAccount = (BusinessAccount) ownerAccount;
                String role = businessAccount.getRoleByEmail(email);
                if (role != null && role.equalsIgnoreCase("manager")) {
                    account = ownerAccount;
                    user = owner;
                } else if (role != null && role.equalsIgnoreCase("employee")) {
                    account = ownerAccount;
                    user = owner;
                    maxAmount = businessAccount.getSpendingLimit();
                    isEmployee = true;
                } else if (role != null && role.equalsIgnoreCase("owner")) {
                    account = ownerAccount;
                    user = owner;
                } else {
                    throw new IllegalArgumentException("Card not found");
                }
            } else {
                account = user.findAccountByCardNumber(cardNumber);
            }

            Commerciant commerciant = commerciantService.findCommerciantByName(commerciantName);

            List<ExchangeRate> bidirectionalRates = ExchangeRate.
                    generateBidirectionalRates(exchangeRates);
            double convertedAmount = ExchangeRate.convertCurrency(paymentCurrency,
                    account.getCurrency(), amount, bidirectionalRates);

            if (convertedAmount > maxAmount && isEmployee) {
                user.addTransaction(new Transaction(timestamp, "Card payment",
                        0, commerciantName, "spent", email));
                return null;
            }

            if (account.getBalance() < convertedAmount) {
                user.addTransaction(new Transaction(timestamp, "Insufficient funds"));
                return null;
            }

            Transaction transaction = new Transaction(timestamp, "Card payment",
                    convertedAmount, commerciantName, "spent", email, commerciant, paymentCurrency);
            transaction.setAccountIBAN(account.getIban());
            user.addTransaction(transaction);

            double cashbackPercentage = CashbackCalculator.calculateCashbackPercentage(user,
                    account, commerciant, exchangeRates);
            double cashback = cashbackPercentage * convertedAmount;

            double convertedToRON = ExchangeRate.convertCurrency(paymentCurrency,
                    "RON", amount, bidirectionalRates);

            CommissionStrategy strategy = CommissionService.
                    getCommissionStrategy(account.getPlan());
            CommissionCalculator calculator = new CommissionCalculator(strategy);
            double commision = calculator.applyCommission(convertedToRON);

            double convertedCommision = ExchangeRate.convertCurrency("RON", account.getCurrency(),
                    commision, bidirectionalRates);

            account.setBalance(account.getBalance() - convertedAmount
                                + cashback - convertedCommision);

            if (user.checkForAutomaticUpgrade(bidirectionalRates)) {
                Transaction transactionUpgrade = new Transaction(timestamp,
                        "Upgrade plan", account.getIban(), "gold");
                transactionUpgrade.setAccountIBAN(account.getIban());
                user.addTransaction(transactionUpgrade);
            }

            if (card.isOneTime()) {
                String newCardNumber = generateCardNumber();
                card.setCardNumber(newCardNumber);
                user.addTransaction(new Transaction(timestamp, "The card has been destroyed",
                        cardNumber, email, account.getIban()));
                user.addTransaction(new Transaction(timestamp, "New card created",
                        newCardNumber, email, account.getIban()));
            }

            return null;

        } catch (IllegalArgumentException e) {
            innerOutput.put("description", e.getMessage());
            output.set("output", innerOutput);
            output.put("timestamp", timestamp);
            return output;
        }
    }
}


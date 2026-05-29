package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.CommerciantService;
import org.poo.bank.User;
import org.poo.bank.UserService;
import org.poo.bank.Account;
import org.poo.bank.Transaction;
import org.poo.bank.Commerciant;
import org.poo.bank.commission.CommissionCalculator;
import org.poo.bank.commission.CommissionService;
import org.poo.bank.commission.CommissionStrategy;
import org.poo.utils.ExchangeRate;
//import org.poo.bank.commission.CommisionCalculator;
import org.poo.bank.CashbackCalculator;

import java.util.List;

import static org.poo.utils.Utils.isValidIBAN;

public final class SendMoney implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final List<ExchangeRate> exchangeRates;
    private final CommerciantService commerciantService;

    public SendMoney(final UserService userService, final ObjectMapper objectMapper,
                     final List<ExchangeRate> exchangeRates,
                     final CommerciantService commerciantService) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.exchangeRates = exchangeRates;
        this.commerciantService = commerciantService;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        String accountIBAN = input.get("account").asText();
        double amount = input.get("amount").asDouble();
        String receiverIBAN = input.get("receiver").asText();
        String email = input.get("email").asText();
        String description = input.get("description").asText();
        int timestamp = input.get("timestamp").asInt();

        try {
            List<ExchangeRate> bidirectionalRates = ExchangeRate.
                    generateBidirectionalRates(exchangeRates);

            Commerciant commerciant = commerciantService.findCommerciantByIBAN(receiverIBAN);
            if (commerciant != null) {
                User user = userService.getUserByEmail(email);

                Account account;

                if (isValidIBAN(accountIBAN)) {
                    account = user.findAccountByIBAN(accountIBAN);
                } else {
                    account = user.findAccountByAlias(accountIBAN);
                }

                if (account.getBalance() < amount) {
                    Transaction transaction = new Transaction(timestamp, "Insufficient funds");
                    transaction.setAccountIBAN(accountIBAN);
                    user.addTransaction(transaction);
                    return null;
                }

                String money = amount + " " + account.getCurrency();
                Transaction transaction = new Transaction(timestamp, description, accountIBAN,
                        receiverIBAN, money, "sent", commerciant.getName(), commerciant);
                transaction.setAccountIBAN(accountIBAN);
                user.addTransaction(transaction);

                double convertedToRON = ExchangeRate.convertCurrency(account.getCurrency(),
                        "RON", amount, bidirectionalRates);

                CommissionStrategy strategy = CommissionService.
                        getCommissionStrategy(account.getPlan());
                CommissionCalculator calculator = new CommissionCalculator(strategy);
                double commision = calculator.applyCommission(convertedToRON);

                double convertedCommision = ExchangeRate.convertCurrency("RON",
                        account.getCurrency(), commision, bidirectionalRates);

                double cashbackPercentage = CashbackCalculator.calculateCashbackPercentage(user,
                        account, commerciant, exchangeRates);

                double cashback = cashbackPercentage * amount;

                double totalAmount = amount + convertedCommision;

                account.setBalance(account.getBalance() - totalAmount + cashback);

                if (user.checkForAutomaticUpgrade(bidirectionalRates)) {
                    Transaction transactionUpgrade = new Transaction(timestamp,
                            "Upgrade plan", account.getIban(), "gold");
                    transactionUpgrade.setAccountIBAN(account.getIban());
                    user.addTransaction(transactionUpgrade);
                }
                return null;
            }


            User user = userService.getUserByEmail(email);
            Account account;

            if (isValidIBAN(accountIBAN)) {
                account = user.findAccountByIBAN(accountIBAN);
            } else {
                account = user.findAccountByAlias(accountIBAN);
            }

            Account receiverAccount;
            User receiver;
            if (isValidIBAN(receiverIBAN)) {
                receiver = userService.getUserByAccount(receiverIBAN);
                receiverAccount = receiver.findAccountByIBAN(receiverIBAN);
            } else {
                receiver = userService.findUserByAlias(receiverIBAN);
                receiverAccount = receiver.findAccountByAlias(receiverIBAN);
            }

            double convertedAmount = ExchangeRate.convertCurrency(account.getCurrency(),
                    receiverAccount.getCurrency(), amount, bidirectionalRates);

            double convertedToRON = ExchangeRate.convertCurrency(account.getCurrency(),
                    "RON", amount, bidirectionalRates);

            CommissionStrategy strategy = CommissionService.
                    getCommissionStrategy(account.getPlan());
            CommissionCalculator calculator = new CommissionCalculator(strategy);
            double commision = calculator.applyCommission(convertedToRON);

            double convertedCommision = ExchangeRate.convertCurrency("RON",
                                        account.getCurrency(), commision, bidirectionalRates);

            double totalAmount = amount + convertedCommision;

            if (account.getBalance() < totalAmount) {
                Transaction transaction = new Transaction(timestamp, "Insufficient funds");
                transaction.setAccountIBAN(accountIBAN);
                user.addTransaction(transaction);
                return null;
            }

            account.setBalance(account.getBalance() - totalAmount);

            receiverAccount.setBalance(receiverAccount.getBalance() + convertedAmount);

            String money = amount + " " + account.getCurrency();
            Transaction transaction = new Transaction(timestamp, description, accountIBAN,
                    receiverIBAN, money, "sent");
            transaction.setAccountIBAN(accountIBAN);
            user.addTransaction(transaction);
            String moneyReceived = convertedAmount + " " + receiverAccount.getCurrency();
            Transaction receiverTransaction = new Transaction(timestamp, description, accountIBAN,
                    receiverIBAN, moneyReceived, "received");
            receiverTransaction.setAccountIBAN(receiverIBAN);
            receiver.addTransaction(receiverTransaction);

        } catch (IllegalArgumentException e) {
            ObjectNode output = objectMapper.createObjectNode();
            output.put("command", "sendMoney");
            ObjectNode innerOutput = objectMapper.createObjectNode();
            innerOutput.put("timestamp", timestamp);
            innerOutput.put("description", e.getMessage());
            output.set("output", innerOutput);
            output.put("timestamp", timestamp);
            return output;
        }
        return null;
    }
}

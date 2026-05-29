package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.bank.CommerciantService;
import org.poo.utils.ExchangeRate;
import org.poo.bank.UserService;

import java.util.List;

import static org.poo.utils.Utils.resetRandom;

public final class CommandFactory {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final List<ExchangeRate> exchangeRates;
    private final CommerciantService commerciantService;

    public CommandFactory(final UserService userService, final ObjectMapper objectMapper,
                          final List<ExchangeRate> exchangeRates,
                          final CommerciantService commerciantService) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.exchangeRates = exchangeRates;
        this.commerciantService = commerciantService;
        resetRandom();
    }

    /**
     * Retrieves the appropriate command implementation based on the provided command name.
     * This method uses a factory-like approach to instantiate the specific command class
     * dynamically, enabling extensibility and reducing coupling between components.
     *
     * @param commandName the name of the command to retrieve
     * @return the command implementation corresponding to the given name
     * @throws IllegalArgumentException if the command name is not recognized
     */
    public Command getCommand(final String commandName) {
        return switch (commandName) {
            case "printUsers" -> new PrintUsers(userService, objectMapper);
            case "addAccount" -> new AddAccount(userService, exchangeRates);
            case "createCard" -> new CreateCard(userService);
            case "addFunds" -> new AddFunds(userService, exchangeRates);
            case "deleteAccount" -> new DeleteAccount(userService, objectMapper);
            case "createOneTimeCard" -> new CreateOneTimeCard(userService);
            case "deleteCard" -> new DeleteCard(userService);
            case "setMinimumBalance" -> new SetMinimumBalance(userService);
            case "payOnline" -> new PayOnline(userService, exchangeRates,
                    objectMapper, commerciantService);
            case "sendMoney" -> new SendMoney(userService, objectMapper,
                    exchangeRates, commerciantService);
            case "printTransactions" -> new PrintTransactions(userService, objectMapper);
            case "setAlias" -> new SetAlias(userService, objectMapper);
            case "checkCardStatus" -> new CheckCardStatus(userService, objectMapper);
            case "changeInterestRate" -> new ChangeInterestRate(userService, objectMapper);
            case "addInterest" -> new AddInterest(userService, objectMapper);
            case "splitPayment" -> new SplitPayment(userService, exchangeRates);
            case "report" -> new Report(userService, objectMapper);
            case "spendingsReport" -> new SpendingsReport(userService, objectMapper);
            case "withdrawSavings" -> new WithdrawSavings(userService, exchangeRates);
            case "upgradePlan" -> new UpgradePlan(userService, exchangeRates, objectMapper);
            case "cashWithdrawal" -> new CashWithdrawal(userService, objectMapper, exchangeRates);
            case "acceptSplitPayment" -> new AcceptSplitPayment(userService,
                    exchangeRates, objectMapper);
            case "rejectSplitPayment" -> new RejectSplitPayment(userService, objectMapper);
            case "addNewBusinessAssociate" -> new AddNewBusinessAssociate(userService);
            case "changeSpendingLimit" -> new ChangeSpendingLimit(userService, objectMapper);
            case "changeDepositLimit" -> new ChangeDepositLimit(userService, objectMapper);
            case "businessReport" -> new BusinessReport(userService, objectMapper);
            default -> throw new IllegalArgumentException("Unknown command: " + commandName);
        };
    }
}

package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Constants;
import org.poo.utils.ExchangeRate;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String birthDate;
    private final String occupation;
    private String plan;
    @Setter
    private boolean statusOfAcceptance;
    @Setter
    private boolean splitTransaction;
    @Setter
    private List<Account> accounts;
    @Setter
    private List<Transaction> transactions;
    @Setter
    private List<SavingsAccount> savingsAccounts;
    @Setter
    private List<Alias> aliases;

    public User(final String firstName, final String lastName, final String email,
                final String birthDate, final String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.occupation = occupation;
        this.plan = occupation.equalsIgnoreCase("student") ? "student" : "standard";
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.savingsAccounts = new ArrayList<>();
        this.aliases = new ArrayList<>();
        this.statusOfAcceptance = false;
        this.splitTransaction = false;
    }

    /**
     * Adds a new account to the user's list of accounts.
     *
     * @param account the account to be added.
     */
    public void addAccount(final Account account) {
        accounts.add(account);
    }

    /**
     * Deletes an account from the user's list of accounts and records the action as a transaction.
     *
     * @param account   the account to be deleted.
     * @param timestamp the timestamp of the deletion action.
     */
    public void deleteAccount(final Account account, final int timestamp) {
        accounts.remove(account);
        this.addTransaction(new Transaction(timestamp, "Account deleted"));
    }

    /**
     * Converts the user's information, accounts, and associated details into a JSON representation.
     *
     * @param objectMapper the {@link ObjectMapper} instance used to create JSON objects.
     * @return an {@link ObjectNode} representing the user, including their personal details
     *         and associated accounts.
     */
    public ObjectNode toJson(final ObjectMapper objectMapper) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("firstName", firstName);
        userNode.put("lastName", lastName);
        userNode.put("email", email);

        ArrayNode accountsArray = objectMapper.createArrayNode();
        for (Account account : accounts) {
            accountsArray.add(account.toJson(objectMapper));
        }

        userNode.set("accounts", accountsArray);
        return userNode;
    }

    /**
     * Finds an account by its IBAN.
     *
     * @param iban the IBAN of the account to be found.
     * @return the {@link Account} with the specified IBAN.
     * @throws IllegalArgumentException if no account with the given IBAN is found.
     */
    public Account findAccountByIBAN(final String iban) {
        for (Account account : accounts) {
            if (account.getIban().equals(iban)) {
                return account;
            }
        }
        throw new IllegalArgumentException("Account not found");
    }


    /**
     * Finds a card by its card number.
     *
     * @param cardNumber the card number of the card to be found.
     * @return the {@link Card} with the specified card number.
     * @throws IllegalArgumentException if no card with the given card number is found.
     */
    public Card findCardByCardNumber(final String cardNumber) {
        for (Account account: accounts) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    return card;
                }
            }
        }
        throw new IllegalArgumentException("Card not found");
    }

    /**
     * Finds an account associated with a specific card number.
     *
     * @param cardNumber the card number used to locate the account.
     * @return the {@link Account} associated with the given card number.
     * @throws IllegalArgumentException if no account is found for the specified card number.
     */
    public Account findAccountByCardNumber(final String cardNumber) {
        for (Account account : accounts) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    return account;
                }
            }
        }
        throw new IllegalArgumentException("Card not found");
    }

    /**
     * Adds a transaction to the user's transaction history.
     *
     * @param transaction the {@code Transaction} to be added
     */
    public void addTransaction(final Transaction transaction) {
        this.transactions.add(transaction);
    }

    /**
     * Adds a new alias to the list of aliases.
     *
     * @param alias the {@link Alias} object to be added.
     * @throws IllegalArgumentException if an alias with the same name already exists.
     */
    public void addAlias(final Alias alias) {
        if (hasAlias(alias.getAlias())) {
            throw new IllegalArgumentException("Alias already exists.");
        }
        aliases.add(alias);
    }

    /**
     * Checks if a given alias exists in the list of aliases.
     *
     * @param alias the alias to check for existence.
     * @return {@code true} if the alias exists, {@code false} otherwise.
     */
    public boolean hasAlias(final String alias) {
        for (Alias a : aliases) {
            if (a.getAlias().equals(alias)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the first classic account that matches the specified currency.
     *
     * @param currency The currency to search for.
     * @return The first classic account found with the specified currency.
     * @throws IllegalArgumentException If no classic account with the specified currency is found.
     */
    public Account findFirstClassicAccountByCurrency(final String currency) {
        for (Account account : accounts) {
            if (account.getCurrency().equalsIgnoreCase(currency)
                && account.getType().equalsIgnoreCase("classic")) {
                return account;
            }
        }
        throw new IllegalArgumentException("You do not have a classic account.");
    }

    /**
     * Upgrades the user's plan to a new specified plan.
     *
     * @param newPlan The new plan to be set.
     * @throws IllegalArgumentException If the user already has the specified plan
     *                                  or if the upgrade is considered a downgrade.
     */
    public void upgradePlan(final String newPlan) {
        if (newPlan.equals(plan)) {
            throw new IllegalArgumentException("The user already has the " + newPlan + " plan.");
        }
        if (isDowngrade(plan, newPlan)) {
            throw new IllegalArgumentException("You cannot downgrade your plan.");
        }
        this.plan = newPlan;
        for (Account account : accounts) {
            account.setPlan(newPlan);
        }
    }

    /**
     * Checks if the user is eligible for an automatic upgrade to the Gold plan
     * based on transaction history and specified exchange rates.
     *
     * @param exchangeRates The list of exchange rates used to convert currency.
     * @return {@code true} if the user qualifies for an upgrade, {@code false} otherwise.
     */
    public boolean checkForAutomaticUpgrade(final List<ExchangeRate> exchangeRates) {
        Constants constants = Constants.getInstance();
        if (this.plan.equalsIgnoreCase("silver")) {
            int qualifyingPayments = 0;

            for (Transaction t : transactions) {
                if (t.getCurrencyForPlan() != null) {
                    double amountInRON = ExchangeRate.convertCurrency(t.getCurrencyForPlan(),
                            "RON", t.getMoney(), exchangeRates);
                    if (amountInRON >= constants.getMinimumPayment()) {
                        qualifyingPayments++;
                    }
                }
            }

            if (qualifyingPayments >= constants.getNumberOfTransactions()) {
                this.plan = "gold";
                for (Account account : accounts) {
                    account.setPlan("gold");
                }
                return true;
            }
        }
        return false;
    }


    private boolean isDowngrade(final String currentPlan, final String newPlan) {
        List<String> plans = List.of("standard", "student", "silver", "gold");
        return plans.indexOf(newPlan) < plans.indexOf(currentPlan);
    }

    /**
     * Finds an account associated with a given alias.
     *
     * @param alias The alias to search for.
     * @return The account associated with the given alias.
     * @throws IllegalArgumentException If no account is found with the given alias.
     */
    public Account findAccountByAlias(final String alias) {
        for (Alias a : aliases) {
            if (a.getAlias().equalsIgnoreCase(alias)) {
                return findAccountByIBAN(a.getAccountIBAN());
            }
        }
        throw new IllegalArgumentException("Account not found");
    }
}

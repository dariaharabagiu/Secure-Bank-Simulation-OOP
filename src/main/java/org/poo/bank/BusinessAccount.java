package org.poo.bank;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Constants;
import org.poo.utils.ExchangeRate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BusinessAccount extends Account {
    private String ownerEmail;
    private Map<String, String> associates; //Email -> Role (owner, manager, employee)
    private double spendingLimit;
    private double depositLimit;

    public BusinessAccount(final String iban, final double balance, final String currency,
                           final String type, final String plan,
                           final List<ExchangeRate> exchangeRates, final String ownerEmail) {
        super(iban, balance, currency, type, plan, exchangeRates);
        Constants constants = Constants.getInstance();
        this.ownerEmail = ownerEmail;
        this.associates = new LinkedHashMap<>();
        this.associates.put(ownerEmail, "owner");
        this.spendingLimit = ExchangeRate.convertCurrency("RON", currency,
                                        constants.getInitialLimit(), exchangeRates);
        this.depositLimit = ExchangeRate.convertCurrency("RON", currency,
                                        constants.getInitialLimit(), exchangeRates);
    }

    /**
     * Adds a new associate to the business account.
     *
     * @param email The email of the associate to be added.
     * @param role  The role of the associate (e.g., "manager", "employee").
     */
    public void addAssociate(final String email, final String role) {
        associates.put(email, role);
    }


    /**
     * Sets a new deposit limit for the business account.
     *
     * @param email The email of the associate requesting the change.
     * @param limit The new deposit limit.
     */
    public void setDepositLimit(final String email, final double limit) {
        depositLimit = limit;
    }

    /**
     * Retrieves the role of an associate based on their email address.
     *
     * @param email The email of the associate.
     * @return The role of the associate (e.g., "owner", "manager", "employee")
     * or {@code null} if not found.
     */
    public String getRole(final String email) {
        return associates.get(email);
    }

    /**
     * Retrieves the role of an associate using their email address.
     * Returns "owner" if the email belongs to the account owner.
     *
     * @param email The email of the associate.
     * @return The role of the associate (e.g., "owner", "manager", "employee")
     * or {@code null} if not found.
     */
    public String getRoleByEmail(final String email) {
        if (email.equals(ownerEmail)) {
            return "owner";
        }
        for (Map.Entry<String, String> entry : associates.entrySet()) {
            if (entry.getKey().equals(email)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Checks if a given email belongs to an associate of the business account.
     *
     * @param email The email to check.
     * @return {@code true} if the email is associated with the account, {@code false} otherwise.
     */
    public boolean isAssociate(final String email) {
        return associates.containsKey(email);
    }
}

package org.poo.bank;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class CommerciantData {
    private final String commerciantName;
    private double totalSpent;
    private final List<String> managers;
    private final List<String> employees;

    public CommerciantData(final String commerciantName) {
        this.commerciantName = commerciantName;
        this.totalSpent = 0.0;
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
    }

    /**
     * Adds a specified amount to the total spending for the commerciant.
     *
     * @param amount The amount to be added to the total spent.
     */
    public void addAmountSpent(final double amount) {
        this.totalSpent += amount;
    }

    /**
     * Adds a user to the commerciant with a specified role.
     *
     * @param userName The name of the user to be added.
     * @param role     The role of the user (e.g., "manager" or "employee").
     */
    public void addUser(final String userName, final String role) {
        if ("manager".equalsIgnoreCase(role)) {
            managers.add(userName);
        } else if ("employee".equalsIgnoreCase(role)) {
            employees.add(userName);
        }
    }
}

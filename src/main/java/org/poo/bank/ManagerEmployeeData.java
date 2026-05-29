package org.poo.bank;

import lombok.Getter;

@Getter
public final class ManagerEmployeeData {
    private final String userName;
    private final String email;
    private double spent;
    private double deposited;
    private final String role;

    public ManagerEmployeeData(final String userName, final String email, final String role) {
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.spent = 0;
        this.deposited = 0;
    }

    /**
     * Adds an amount to the total spending of the user.
     *
     * @param amount The amount to add to the spending.
     */
    public void addSpent(final double amount) {
        this.spent += amount;
    }

    /**
     * Adds an amount to the total deposits made by the user.
     *
     * @param amount The amount to add to the deposits.
     */
    public void addDeposited(final double amount) {
        this.deposited += amount;
    }
}

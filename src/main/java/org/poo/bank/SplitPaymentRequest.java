package org.poo.bank;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


@Getter
public  final class SplitPaymentRequest {
    private final List<String> accounts;
    private final List<Double> amounts;
    private final String currency;
    private final int timestamp;
    private final String splitPaymentType;
    private final double totalAmount;
    private final Map<String, Map<String, Boolean>> userResponses;

    private final UserService userService;

    public SplitPaymentRequest(final List<String> accounts, final List<Double> amounts,
                               final String currency, final int timestamp,
                               final String splitPaymentType, final double totalAmount,
                               final UserService userService) {
        this.accounts = accounts;
        this.amounts = amounts;
        this.currency = currency;
        this.timestamp = timestamp;
        this.splitPaymentType = splitPaymentType;
        this.totalAmount = totalAmount;
        this.userResponses = new HashMap<>();
        this.userService = userService;

        for (String account : accounts) {
            User user = userService.getUserByAccount(account);
            this.userResponses.putIfAbsent(user.getEmail(), new HashMap<>());
            this.userResponses.get(user.getEmail()).put(splitPaymentType, null);
        }
    }

    /**
     * Updates the user's response for a specific split payment type.
     * This method checks if the given user is part of the specified split payment type and
     * updates their acceptance status. If the user is not found in the split payment records,
     * an exception is thrown.
     *
     * @param email The email of the user responding to the split payment.
     * @param type The type of split payment (e.g., equal, percentage-based).
     * @param accepted A boolean indicating whether the user accepts the payment.
     * @throws IllegalArgumentException If the user is not part of this split payment type.
     */
    public void acceptPayment(final String email, final String type,
                              final boolean accepted) {
        if (userResponses.containsKey(email) && userResponses.
                get(email).containsKey(type)) {
            userResponses.get(email).put(type, accepted);
        } else {
            throw new IllegalArgumentException("User not part of this split payment type.");
        }
    }

    /**
     * Checks whether all users have accepted the split payment.
     * This method iterates through all recorded responses for the split payment type and
     * determines if all users involved have accepted it. If any user has not responded or
     * rejected the payment, the method returns false.
     *
     * @return {@code true} if all users have accepted the split payment; {@code false} otherwise.
     */
    public boolean isFullyAccepted() {
        for (Map<String, Boolean> splitTypes : userResponses.values()) {
            Boolean response = splitTypes.get(splitPaymentType);
            if (response == null || !response) {
                return false;
            }
        }
        return true;
    }
}


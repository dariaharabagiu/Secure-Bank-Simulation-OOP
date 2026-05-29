package org.poo.bank;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;


public final class UserService {
    private static UserService instance;
    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, Map<String, Queue<SplitPaymentRequest>>>
            splitPaymentRequests = new HashMap<>();

    /**
     * Adds a new user to the system with the given details.
     *
     * @param firstName the first name of the user.
     * @param lastName the last name of the user.
     * @param email the email of the user. This must be unique.
     * @throws IllegalArgumentException if a user with the specified email already exists.
     */

    public void addUser(final String firstName, final String lastName,
                        final String email, final String birthdate, final String commerciant) {
        if (users.containsKey(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists.");
        }
        users.put(email, new User(firstName, lastName, email, birthdate, commerciant));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email of the user to retrieve.
     * @return the {@code User} object associated with the provided email.
     * @throws IllegalArgumentException if no user with the specified email exists.
     */
    public User getUserByEmail(final String email) {
        if (!users.containsKey(email)) {
            throw new IllegalArgumentException("User not found");
        }
        return users.get(email);
    }

    /**
     * Retrieves the user associated with a specific account IBAN.
     *
     * @param iban the IBAN of the account to search for.
     * @return the {@code User} object who owns the account with the specified IBAN.
     * @throws IllegalArgumentException if no user is found with the specified account IBAN.
     */
    public User getUserByAccount(final String iban) {
        for (var user : getAllUsers()) {
            for (var account : user.getAccounts()) {
                if (account.getIban().equals(iban)) {
                    return user;
                }
            }
        }
        throw new IllegalArgumentException("User not found");
    }

    /**
     * Retrieves the user associated with a specific card number.
     *
     * @param cardNumber the card number to search for.
     * @return the {@code User} object who owns the card with the specified number.
     * @throws IllegalArgumentException if no user is found with the specified card number.
     */
    public User getUserByCardNumber(final String cardNumber) {
        for (User user : getAllUsers()) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        return user;
                    }
                }
            }
        }
        throw new IllegalArgumentException("User not found");
    }

    /**
     * Finds a card based on the provided card number.
     *
     * @param cardNumber The card number to search for.
     * @return The {@link Card} object that matches the given card number.
     * @throws IllegalArgumentException If no card with the specified number is found.
     */
    public Card findCardByCardNumber(final String cardNumber) {
        for (User user : getAllUsers()) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        return card;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Card not found");
    }

    /**
     * Finds an account based on the given IBAN.
     *
     * @param iban the IBAN of the account to search for.
     * @return the {@code Account} object with the specified IBAN.
     * @throws IllegalArgumentException if no account is found with the specified IBAN.
     */
    public Account findAccountByIBAN(final String iban) {
        for (var user : getAllUsers()) {
            for (var account : user.getAccounts()) {
                if (account.getIban().equals(iban)) {
                    return account;
                }
            }
        }
        throw new IllegalArgumentException("Account not found");
    }

    /**
     * Checks if a user is eligible for withdrawal based on their age.
     *
     * @param user The user whose eligibility is being checked.
     * @param minimumAge The minimum required age for withdrawal eligibility.
     * @return {@code true} if the user's age is greater than or equal to the minimum age,
     * {@code false} otherwise.
     */
    public boolean isUserEligibleForWithdrawal(final User user, final int minimumAge) {
        String birthDateString = user.getBirthDate();

        LocalDate birthDate = LocalDate.parse(birthDateString);
        LocalDate today = LocalDate.now();

        int age = Period.between(birthDate, today).getYears();
        return age >= minimumAge;
    }

    /**
     * Saves a split payment request for a list of users, categorized by payment type.
     *
     * @param emails The list of user emails associated with the split payment request.
     * @param splitPaymentType The type of split payment (e.g., equal, custom).
     * @param request The split payment request to be saved.
     */
    public void saveSplitPaymentRequest(final List<String> emails, final String splitPaymentType,
                                        final SplitPaymentRequest request) {
        for (String email : emails) {
            // Get the map of requests for the user or create a new one if it doesn't exist
            Map<String, Queue<SplitPaymentRequest>> requestsByType = splitPaymentRequests
                    .computeIfAbsent(email, k -> new HashMap<>());

            // Get the queue of requests for the specific type or create a new one
            Queue<SplitPaymentRequest> requestQueue = requestsByType
                    .computeIfAbsent(splitPaymentType, k -> new LinkedList<>());

            // Add the request to the queue
            requestQueue.add(request);
        }
    }

    /**
     * Finds a pending split payment request for a user and payment type.
     *
     * @param email The email of the user for whom the request is being searched.
     * @param splitPaymentType The type of split payment being searched for.
     * @return The split payment request if found and pending.
     * @throws IllegalArgumentException If no pending split payment is found for the user.
     */
    public SplitPaymentRequest findSplitPaymentRequest(final String email,
                                                       final String splitPaymentType) {
        Map<String, Queue<SplitPaymentRequest>> requestsByType = splitPaymentRequests.get(email);
        if (requestsByType != null && requestsByType.containsKey(splitPaymentType)) {
            Queue<SplitPaymentRequest> requestQueue = requestsByType.get(splitPaymentType);

            for (SplitPaymentRequest request : requestQueue) {
                // Check if the user has not yet accepted this request
                Boolean userResponse = request.getUserResponses().get(email).get(splitPaymentType);
                if (userResponse == null || !userResponse) {
                    return request; // Found the request that has not yet been accepted
                }
            }
        }
        throw new IllegalArgumentException("No pending split payment found for user");
    }

    /**
     * Removes the first pending split payment request for a user and payment type.
     *
     * @param email The email of the user for whom the request should be removed.
     * @param splitPaymentType The type of split payment request to remove.
     * @throws IllegalArgumentException If no pending split payment request is found for the user.
     */
    public void removeSplitPaymentRequest(final String email, final String splitPaymentType) {
        // Checking for requests for this email and type
        Map<String, Queue<SplitPaymentRequest>> requestsByType = splitPaymentRequests.get(email);
        if (requestsByType != null && requestsByType.containsKey(splitPaymentType)) {
            Queue<SplitPaymentRequest> requestQueue = requestsByType.get(splitPaymentType);

            if (!requestQueue.isEmpty()) {
                requestQueue.poll();
            }

            if (requestQueue.isEmpty()) {
                requestsByType.remove(splitPaymentType);
            }

            if (requestsByType.isEmpty()) {
                splitPaymentRequests.remove(email);
            }
        } else {
            throw new IllegalArgumentException("No pending split payment found for user");
        }
    }

    /**
     * Finds a user based on an alias associated with one of their accounts.
     *
     * @param alias The alias to search for.
     * @return The user associated with the given alias.
     * @throws IllegalArgumentException If no user is found with the given alias.
     */
    public User findUserByAlias(final String alias) {
        for (User user : getAllUsers()) {
            for (Alias a : user.getAliases()) {
                if (a.getAlias().equalsIgnoreCase(alias)) {
                    return user;
                }
            }
        }
        throw new IllegalArgumentException("User not found");
    }
}

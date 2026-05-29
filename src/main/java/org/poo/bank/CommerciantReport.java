package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;


public final class CommerciantReport {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public CommerciantReport(final ObjectMapper objectMapper, final UserService userService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    /**
     * Generates a business report for a given business account within a specified time period.
     *
     * This method processes transactions related to the specified business account, filtering
     * them by the given timestamps and aggregating data for each involved commerciant.
     *
     * @param businessAccount The business account for which the report is generated.
     * @param startTimestamp  The starting timestamp for filtering transactions.
     * @param endTimestamp    The ending timestamp for filtering transactions.
     * @return An {@link ObjectNode} containing the generated report data, including IBAN,
     *         balance, currency, spending and deposit limits, and a list of commerciants
     *         with associated transaction details.
     */
    public ObjectNode generate(final BusinessAccount businessAccount,
                               final int startTimestamp, final int endTimestamp) {
        ObjectNode report = objectMapper.createObjectNode();
        Map<String, CommerciantData> commerciantReports = new TreeMap<>();

        User user = userService.getUserByAccount(businessAccount.getIban());
        String accountIBAN = businessAccount.getIban();

        // Process transactions for commerciants
        for (Transaction transaction : user.getTransactions()) {
            if (transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp
                    && accountIBAN.equalsIgnoreCase(transaction.getAccountIBAN())
                    && transaction.getCommerciant() != null
                    && !transaction.getResponsible().equals(user.getEmail())) {

                String commerciantName = transaction.getCommerciant();
                User responsible = userService.getUserByEmail(transaction.getResponsible());
                String userName = responsible.getLastName() + " " + responsible.getFirstName();
                String role = businessAccount.getRoleByEmail(transaction.getResponsible());

                commerciantReports.putIfAbsent(commerciantName,
                        new CommerciantData(commerciantName));

                CommerciantData commerciantData = commerciantReports.get(commerciantName);
                commerciantData.addAmountSpent(transaction.getMoney());

                commerciantData.addUser(userName, role);
            }
        }

        report.put("IBAN", businessAccount.getIban());
        report.put("balance", businessAccount.getBalance());
        report.put("currency", businessAccount.getCurrency());
        report.put("spending limit", businessAccount.getSpendingLimit());
        report.put("deposit limit", businessAccount.getDepositLimit());
        report.put("statistics type", "commerciant");

        ArrayNode commerciantArray = objectMapper.createArrayNode();

        for (CommerciantData data : commerciantReports.values()) {
            ObjectNode commerciantNode = objectMapper.createObjectNode();
            commerciantNode.put("commerciant", data.getCommerciantName());
            commerciantNode.put("total received", data.getTotalSpent());

            // Sort managers and employees
            ArrayNode managersArray = objectMapper.createArrayNode();
            ArrayNode employeesArray = objectMapper.createArrayNode();

            List<String> sortedManagers = new ArrayList<>(data.getManagers());
            Collections.sort(sortedManagers);
            for (String manager : sortedManagers) {
                managersArray.add(manager);
            }

            List<String> sortedEmployees = new ArrayList<>(data.getEmployees());
            Collections.sort(sortedEmployees);
            for (String employee : sortedEmployees) {
                employeesArray.add(employee);
            }

            commerciantNode.set("managers", managersArray);
            commerciantNode.set("employees", employeesArray);

            commerciantArray.add(commerciantNode);
        }

        report.set("commerciants", commerciantArray);
        return report;
    }
}

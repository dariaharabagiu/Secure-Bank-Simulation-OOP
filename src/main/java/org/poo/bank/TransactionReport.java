package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.LinkedHashMap;
import java.util.Map;

public final class TransactionReport {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public TransactionReport(final ObjectMapper objectMapper, final UserService userService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    /**
     * Generates a financial report for a given business account within a specified time range.
     *
     * @param businessAccount The business account for which the report is generated.
     * @param startTimestamp The start of the reporting period (inclusive).
     * @param endTimestamp The end of the reporting period (inclusive).
     * @return An {@code ObjectNode} containing the generated report with details such as IBAN,
     *         balance, currency, spending limit, deposit limit, total spent and deposited amounts,
     *         as well as categorized lists of managers and employees.
     */
    public ObjectNode generate(final BusinessAccount businessAccount,
                               final int startTimestamp, final int endTimestamp) {
        ObjectNode report = objectMapper.createObjectNode();
        Map<String, ManagerEmployeeData> userReports = new LinkedHashMap<>();
        double totalSpent = 0;
        double totalDeposited = 0;

        User user = userService.getUserByAccount(businessAccount.getIban());
        String accountIBAN = businessAccount.getIban();

        for (Map.Entry<String, String> associateEntry
                : businessAccount.getAssociates().entrySet()) {
            String email = associateEntry.getKey();
            User responsible = userService.getUserByEmail(email);
            String userName = responsible.getLastName() + " " + responsible.getFirstName();
            String role = associateEntry.getValue();
            userReports.put(userName, new ManagerEmployeeData(userName, email, role));
        }

        report.put("IBAN", businessAccount.getIban());
        report.put("balance", businessAccount.getBalance());
        report.put("currency", businessAccount.getCurrency());
        report.put("spending limit", businessAccount.getSpendingLimit());
        report.put("deposit limit", businessAccount.getDepositLimit());
        report.put("statistics type", "transaction");

        for (Transaction transaction: user.getTransactions()) {
            if (transaction.getResponsible() != null && transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp
                    && accountIBAN.equalsIgnoreCase(transaction.getAccountIBAN())
                    && !transaction.getResponsible().equals(user.getEmail())) {
                User responsible = userService.getUserByEmail(transaction.getResponsible());
                String userName = responsible.getLastName() + " " + responsible.getFirstName();
                String role = businessAccount.getRoleByEmail(transaction.getResponsible());
                userReports.putIfAbsent(userName, new ManagerEmployeeData(userName,
                        transaction.getResponsible(), role));

                ManagerEmployeeData userReport = userReports.get(userName);
                if (transaction.getType() != null && transaction.getType().
                        equalsIgnoreCase("deposit")) {
                    userReport.addDeposited(transaction.getMoney());
                    totalDeposited += transaction.getMoney();
                } else if (transaction.getType() != null && transaction.getType().
                        equalsIgnoreCase("spent")) {
                    userReport.addSpent(transaction.getMoney());
                    totalSpent += transaction.getMoney();
                }
            }
        }

        ArrayNode managersArray = objectMapper.createArrayNode();
        ArrayNode employeesArray = objectMapper.createArrayNode();

        for (ManagerEmployeeData data : userReports.values()) {
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("username", data.getUserName());
            userNode.put("spent", data.getSpent());
            userNode.put("deposited", data.getDeposited());

            if (data.getRole().equalsIgnoreCase("manager")) {
                managersArray.add(userNode);
            } else if (data.getRole().equalsIgnoreCase("employee")) {
                employeesArray.add(userNode);
            }
        }

        report.set("managers", managersArray);
        report.set("employees", employeesArray);
        report.put("total spent", totalSpent);
        report.put("total deposited", totalDeposited);

        return report;
    }
}

package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.bank.UserService;
import org.poo.bank.Account;
import org.poo.bank.BusinessAccount;
import org.poo.bank.TransactionReport;
import org.poo.bank.CommerciantReport;


public final class BusinessReport implements Command {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public BusinessReport(final UserService userService, final ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectNode execute(final JsonNode input) {
        int startTimestamp = input.get("startTimestamp").asInt();
        int endTimestamp = input.get("endTimestamp").asInt();
        String accountIBAN = input.get("account").asText();
        String type = input.get("type").asText();
        int timestamp = input.get("timestamp").asInt();

        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", "businessReport");

        try {
            Account account = userService.findAccountByIBAN(accountIBAN);

            if (!account.getType().equalsIgnoreCase("business")) {
                throw new IllegalArgumentException("Account is not of type business");
            }

            BusinessAccount businessAccount = (BusinessAccount) account;

            ObjectNode report;
            if (type.equalsIgnoreCase("transaction")) {
                TransactionReport transactionReport = new TransactionReport(objectMapper,
                                                                            userService);
                report = transactionReport.generate(businessAccount, startTimestamp, endTimestamp);
            } else if (type.equalsIgnoreCase("commerciant")) {
                CommerciantReport commerciantReport = new CommerciantReport(objectMapper,
                                                                            userService);
                report = commerciantReport.generate(businessAccount, startTimestamp, endTimestamp);
            } else {
                throw new IllegalArgumentException("Invalid report type");
            }

            output.set("output", report);
        } catch (IllegalArgumentException e) {
            output.put("error", e.getMessage());
        }

        output.put("timestamp", timestamp);
        return output;
    }
}

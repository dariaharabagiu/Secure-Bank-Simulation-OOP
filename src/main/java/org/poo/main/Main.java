package org.poo.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Commerciant;
import org.poo.bank.CommerciantService;
import org.poo.bank.UserService;
import org.poo.bank.commands.Command;
import org.poo.bank.commands.CommandFactory;
import org.poo.checker.Checker;
import org.poo.checker.CheckerConstants;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.utils.ExchangeRate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        var sortedFiles = Arrays.stream(Objects.requireNonNull(directory.listFiles())).
                sorted(Comparator.comparingInt(Main::fileConsumer))
                .toList();

        for (File file : sortedFiles) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(CheckerConstants.TESTS_PATH + filePath1);
        ObjectInput inputData = objectMapper.readValue(file, ObjectInput.class);

        ArrayNode output = objectMapper.createArrayNode();

        // Convert ExchangeInput to ExchangeRate
        List<ExchangeRate> exchangeRates = ExchangeRate.convert(
                List.of(inputData.getExchangeRates())
        );

        // Initialize UserService
        UserService userService = new UserService();

        // Populate users into UserService
        if (inputData.getUsers() != null) {
            for (UserInput userInput : inputData.getUsers()) {
                userService.addUser(userInput.getFirstName(),
                        userInput.getLastName(), userInput.getEmail(),
                        userInput.getBirthDate(), userInput.getOccupation());
            }
        }

        CommerciantService commerciantService = new CommerciantService();

        if (inputData.getCommerciants() != null) {
            for (CommerciantInput commerciantInput : inputData.getCommerciants()) {
                Commerciant commerciant = new Commerciant(
                        commerciantInput.getCommerciant(),
                        commerciantInput.getId(),
                        commerciantInput.getAccount(),
                        commerciantInput.getType(),
                        commerciantInput.getCashbackStrategy()
                );
                commerciantService.addCommerciants(List.of(commerciant));
            }
        }

        // Prepare CommandFactory for processing commands
        CommandFactory commandFactory = new CommandFactory(userService,
                objectMapper, exchangeRates, commerciantService);

        if (inputData.getCommands() != null) {
            for (CommandInput commandInput : inputData.getCommands()) {
                String commandName = commandInput.getCommand();

                try {
                    // Obtain and execute the command
                    Command command = commandFactory.getCommand(commandName);
                    JsonNode commandData = objectMapper.valueToTree(commandInput);
                    // Execute the command and add it to the output only if it is not null
                    ObjectNode result = command.execute(commandData);
                    if (result != null) {
                        output.add(result);
                    }
                } catch (IllegalArgumentException e) {
                    // Handle unknown commands
                    ObjectNode errorOutput = objectMapper.createObjectNode();
                    errorOutput.put("command", commandName);
                    errorOutput.put("status", "error");
                    errorOutput.put("message", e.getMessage());
                    output.add(errorOutput);
                }
            }
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }

    /**
     * Method used for extracting the test number from the file name.
     *
     * @param file the input file
     * @return the extracted numbers
     */
    public static int fileConsumer(final File file) {
        return Integer.parseInt(
                file.getName()
                        .replaceAll(CheckerConstants.DIGIT_REGEX, CheckerConstants.EMPTY_STR)
        );
    }
}

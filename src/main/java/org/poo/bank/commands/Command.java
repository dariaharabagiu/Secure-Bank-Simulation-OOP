package org.poo.bank.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents a command that can be executed in the system.
 * Each command processes a specific input and produces a corresponding output.
 */
public interface Command {

    /**
     * Executes the command based on the provided input.
     *
     * @param input the {@code JsonNode} containing the input data for the command
     * @return an {@code ObjectNode} containing the result of the command execution
     */
    ObjectNode execute(JsonNode input);
}

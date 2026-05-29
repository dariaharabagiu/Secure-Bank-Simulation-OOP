package org.poo.utils;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.ExchangeInput;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public final class ExchangeRate {
    private String from;
    private String to;
    private double rate;

    public ExchangeRate(final String from, final String to, final double rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    /**
     * Generates a list of bidirectional exchange rates based on the given rates.
     * For each provided exchange rate, a reverse rate is calculated and added
     * to the resulting list.
     *
     * @param rates the list of existing exchange rates.
     * @return a list containing both the original and bidirectional (reversed) exchange rates.
     */
    public static List<ExchangeRate> generateBidirectionalRates(final List<ExchangeRate> rates) {
        List<ExchangeRate> bidirectionalRates = new ArrayList<>(rates);

        for (ExchangeRate rate : rates) {
            bidirectionalRates.add(new ExchangeRate(rate.getTo(), rate.getFrom(),
                    1 / rate.getRate()));
        }

        return bidirectionalRates;
    }

    /**
     * Converts a specified amount from one currency to another using a list of exchange rates.
     *
     * @param from   the source currency
     * @param to     the target currency
     * @param amount the amount to be converted.
     * @param rates  a list of available exchange rates for conversions.
     * @return the converted amount in the target currency.
     * @throws IllegalArgumentException if no valid conversion path exists between
     * the source and target currencies.
     */
    public static double convertCurrency(final String from, final String to,
                                         final double amount, final List<ExchangeRate> rates) {
        // Direct conversion
        for (ExchangeRate rate : rates) {
            if (rate.getFrom().equals(from) && rate.getTo().equals(to)) {
                return amount * rate.getRate();
            }
        }

        // Conversion through an intermediate currency
        for (ExchangeRate rate1 : rates) {
            if (rate1.getFrom().equals(from)) {
                for (ExchangeRate rate2 : rates) {
                    if (rate1.getTo().equals(rate2.getFrom()) && rate2.getTo().equals(to)) {
                        return amount * rate1.getRate() * rate2.getRate();
                    }
                }
            }
        }

        throw new IllegalArgumentException("No conversion path available for "
                + from + " to " + to);
    }

    /**
     * Converts a list of {@code ExchangeInput} objects into a list of {@code ExchangeRate} objects.
     *
     * @param inputs the list of {@code ExchangeInput} objects to be converted
     * @return a list of {@code ExchangeRate} objects derived from the input data
     */
    public static List<ExchangeRate> convert(final List<ExchangeInput> inputs) {
        return inputs.stream()
                .map(input -> new ExchangeRate(input.getFrom(), input.getTo(), input.getRate()))
                .collect(Collectors.toList());
    }

}

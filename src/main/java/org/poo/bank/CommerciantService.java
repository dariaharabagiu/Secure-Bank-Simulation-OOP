package org.poo.bank;

import java.util.ArrayList;
import java.util.List;

public final class CommerciantService {
    private final List<Commerciant> commerciants = new ArrayList<>();

    /**
     * Adds a list of commerciants to the existing collection.
     *
     * @param commerciantsList The list of commerciants to be added.
     */
    public void addCommerciants(final List<Commerciant> commerciantsList) {
        commerciants.addAll(commerciantsList);
    }

    /**
     * Finds a commerciant by their name.
     *
     * @param name The name of the commerciant to search for.
     * @return The matching commerciant object.
     * @throws IllegalArgumentException If no commerciant with the given name is found.
     */
    public Commerciant findCommerciantByName(final String name) {
        for (Commerciant commerciant: commerciants) {
            if (commerciant.getName().equals(name)) {
                return commerciant;
            }
        }
        throw new IllegalArgumentException("Commerciant not found");
    }

    /**
     * Finds a commerciant by their IBAN.
     *
     * @param iban The IBAN of the commerciant to search for.
     * @return The matching commerciant object, or {@code null} if no match is found.
     */
    public Commerciant findCommerciantByIBAN(final String iban) {
        for (Commerciant commerciant : commerciants) {
            if (commerciant.getAccountIBAN().equals(iban)) {
                return commerciant;
            }
        }
        return  null;
    }
}

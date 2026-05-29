package org.poo.bank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Commerciant {
    private String name;
    private int id;
    private String accountIBAN;
    private String type;
    private String cashbackStrategy;

    public Commerciant(final String name, final int id, final String accountIBAN,
                       final String type, final String cashbackStrategy) {
        this.name = name;
        this.id = id;
        this.accountIBAN = accountIBAN;
        this.type = type;
        this.cashbackStrategy = cashbackStrategy;
    }
}

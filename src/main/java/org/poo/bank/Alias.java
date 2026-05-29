package org.poo.bank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Alias {
    private String email;
    private String alias;
    private String accountIBAN;

    public Alias(final String email, final String alias, final String accountIBAN) {
        this.email = email;
        this.alias = alias;
        this.accountIBAN = accountIBAN;
    }
}

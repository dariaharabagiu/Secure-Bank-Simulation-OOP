package org.poo.bank;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.ExchangeRate;

import java.util.List;

@Setter
@Getter
public final class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(final String iban, final double balance, final String currency,
                          final String type, final String plan,
                          final List<ExchangeRate> exchangeRateList, final double interestRate) {
        super(iban, balance, currency, type, plan, exchangeRateList);
        this.interestRate = interestRate;
    }
}

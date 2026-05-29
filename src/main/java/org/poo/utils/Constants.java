package org.poo.utils;

import lombok.Getter;

@Getter
public final class Constants {
    private final int silverFee = 100;
    private final int goldFee = 350;
    private final int silverToGoldFee = 250;
    private final int initialLimit = 500;
    private final int minimumAge = 21;
    private final int foodThreshold = 2;
    private final int clothesThreshold = 5;
    private final int techThreshold = 10;
    private final double foodDiscount = 0.02;
    private final double clothesDiscount = 0.05;
    private final double techDiscount = 0.1;
    private final double firstThreshold = 100;
    private final double secondThreshold = 300;
    private final double thirdThreshold = 500;
    private final double firstStudentDiscount = 0.001;
    private final double firstSilverDiscount = 0.003;
    private final double firstGoldDiscount = 0.005;
    private final double secondStudentDiscount = 0.002;
    private final double secondSilverDiscount = 0.004;
    private final double secondGoldDiscount = 0.0055;
    private final double thirdStudentDiscount = 0.0025;
    private final double thirdSilverDiscount = 0.005;
    private final double thirdGoldDiscount = 0.007;
    private final double minimumPayment = 300;
    private final int numberOfTransactions = 5;
    private final double maximumPayment = 500;
    private final double silverCommision = 0.001;
    private final double standardCommision = 0.002;


    private static Constants instance;

    private Constants() {
    }

    /**
     * Returns the unique instance of the Constants class.
     *
     * @return the singleton instance of the Constants class
     */
    public static Constants getInstance() {
        if (instance == null) {
            instance = new Constants();
        }
        return instance;
    }
}

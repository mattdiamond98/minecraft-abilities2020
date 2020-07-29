package de.gesundkrank.jskills.numerics;

import java.util.Collection;

/**
 * For all the functions that aren't in java.lang.Math
 */
public final class MathUtils {

    /**
     * Don't allow instantiation
     **/
    private MathUtils() {
    }

    /**
     * Square a number
     *
     * @param x number to square
     * @return squared number
     **/
    public static double square(double x) {
        return x * x;
    }


    public static double mean(Collection<Double> collection) {
        double ret = 0;
        for (double d : collection) {
            ret += d;
        }
        return ret / collection.size();
    }
}

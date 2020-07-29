package de.gesundkrank.jskills.elo;

import de.gesundkrank.jskills.Rating;

/**
 * An Elo data represented by a single number (mean).
 */
public class EloRating extends Rating {
    public EloRating(double rating) { super(rating, 0); }
}
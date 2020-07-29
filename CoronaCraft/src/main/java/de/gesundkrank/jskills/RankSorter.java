package de.gesundkrank.jskills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to sort ranks in non-decreasing order.
 */
public class RankSorter {

    /**
     * Returns a list of all the elements in items, sorted in non-descending order, according to
     * itemRanks. Uses a stable sort, and also sorts itemRanks (in place)
     *
     * @param items     The items to sort according to the order specified by ranks.
     * @param itemRanks The ranks for each item where 1 is first place.
     * @param <T>       Type of the items to sort.
     * @return the items sorted according to their ranks
     */
    public static <T> List<T> sort(final Collection<T> items, final int[] itemRanks) {
        Guard.argumentNotNull(items, "items");
        Guard.argumentNotNull(itemRanks, "itemRanks");

        int lastObservedRank = 0;
        boolean needToSort = false;

        for (int currentRank : itemRanks) {
            // We're expecting ranks to go up (e.g. 1, 2, 2, 3, ...)
            // If it goes down, then we've got to sort it.
            if (currentRank < lastObservedRank) {
                needToSort = true;
                break;
            }

            lastObservedRank = currentRank;
        }

        // Get the existing items as an indexable list.
        final List<T> itemsInList = new ArrayList<>(items);

        // Don't bother doing more work, it's already in a good order
        if (!needToSort) {
            return itemsInList;
        }

        // item -> rank
        final Map<T, Integer> itemToRank = new HashMap<>();
        for (int i = 0; i < itemsInList.size(); i++) {
            itemToRank.put(itemsInList.get(i), itemRanks[i]);
        }

        itemsInList.sort(Comparator.comparing(itemToRank::get));

        Arrays.sort(itemRanks);
        return itemsInList;
    }
}
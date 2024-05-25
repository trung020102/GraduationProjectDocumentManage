package com.doc.mamagement.utility.comparator;

import java.text.Collator;
import java.util.Locale;

public class StringComparator {
    public static int compareVietnameseString(String firstString, String secondString) {
        Collator viCollator = Collator.getInstance(new Locale("vi", "VN"));
        viCollator.setStrength(Collator.PRIMARY);

        return  viCollator.compare(firstString, secondString);
    }
}

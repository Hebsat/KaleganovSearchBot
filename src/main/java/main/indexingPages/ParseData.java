package main.indexingPages;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ParseData {

    private static final Set<String> foundLinks = Collections.synchronizedSet(new HashSet<>());
    private static boolean isInterrupted;
    private static boolean isSearching = false;

    public static Set<String> getFoundLinks() {
        return foundLinks;
    }

    public static void addFoundLink(String foundLink) {
        foundLinks.add(foundLink);
    }

    public static void clearFoundLinks() {
        foundLinks.clear();
    }

    public static boolean isInterrupted() {
        return isInterrupted;
    }

    public static void setInterrupted(boolean interrupted) {
        isInterrupted = interrupted;
    }

    public static boolean isSearching() {
        return isSearching;
    }

    public static void setSearching(boolean isSearching) {
        ParseData.isSearching = isSearching;
    }
}
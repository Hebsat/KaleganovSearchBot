package main;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FoundLinks {

    private static Set<String> foundLinks = Collections.synchronizedSet(new HashSet<>());

    public static void setFoundLinks(Set<String> foundLinks) {
        FoundLinks.foundLinks = foundLinks;
    }

    public static Set<String> getFoundLinks() {
        return foundLinks;
    }

    public static void addFoundLink(String foundLink) {
        foundLinks.add(foundLink);
    }
}
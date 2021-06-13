package algorithms;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

class Follow extends TreeSet<String> {
    public Follow(String... symbols) {
        super();
        this.addAll(Arrays.asList(symbols));
    }
}

class FollowMap extends TreeMap<String, Follow> {
    FollowMap() {
        super();
    }

    FollowMap deepClone() {
        FollowMap mapClone = new FollowMap();
        for (String symbol : this.keySet()) {
            Follow old = this.get(symbol);
            Follow clone = new Follow();
            clone.addAll(old);
            mapClone.put(symbol, clone);
        }
        return mapClone;
    }

    void initializeFollowSetOfNonTerminal(String symbol) {
        Follow set = new Follow();
        this.put(symbol, set);
    }
}

package me.lynxplay.idonis.core.dialect.promise.parser;

/**
 * The {@link CachedStringIndexer} is an index tool that caches the last result until it is invalid
 */
public class CachedStringIndexer {

    private int lastIndex;

    private final StringBuilder source;
    private final String target;

    /**
     * Creates a new {@link CachedStringIndexer} looking for the given target string in the source string.
     *
     * @param source the source string it will search in
     * @param target the target string it will search for
     */
    public CachedStringIndexer(StringBuilder source, String target) {
        this.source = source;
        this.target = target;
        this.lastIndex = -1;
    }

    /**
     * Searches for the next found index of the target string
     *
     * @param startIndex the start index from which to start the search in the source string
     *
     * @return the index of the found sub string or -1 if none was found
     */
    public int find(int startIndex) {
        if (startIndex <= lastIndex) {
            return lastIndex;
        }
        return (this.lastIndex = this.source.indexOf(target, startIndex));
    }

    /**
     * Returns the target string of the indexer
     *
     * @return the target string
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * Adjusts the cache of the indexer by the given value
     *
     * @param i the value to adjust by
     */
    public void adjustCache(int i) {
        this.lastIndex += i;
    }
}

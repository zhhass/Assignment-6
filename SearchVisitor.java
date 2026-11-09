package kz.aitu.filesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


public final class SearchVisitor extends TraversingVisitor {

    private final Predicate<String> nameCriterion;
    private final long minimumSize;
    private final List<String> foundPaths = new ArrayList<>();

    public SearchVisitor(Predicate<String> nameCriterion, long minimumSize) {
        this.nameCriterion = Objects.requireNonNull(nameCriterion, "nameCriterion");
        if (minimumSize < 0) {
            throw new IllegalArgumentException("Minimum size must not be negative: " + minimumSize);
        }
        this.minimumSize = minimumSize;
    }

    public static SearchVisitor withExtension(String extension, long minimumSize) {
        return new SearchVisitor(name -> name.endsWith(extension), minimumSize);
    }

    public static SearchVisitor nameContains(String fragment, long minimumSize) {
        return new SearchVisitor(name -> name.contains(fragment), minimumSize);
    }

    /** Sorted, unmodifiable snapshot of the matches of the latest run. */
    public List<String> matches() {
        List<String> sorted = new ArrayList<>(foundPaths);
        Collections.sort(sorted);
        return Collections.unmodifiableList(sorted);
    }

    @Override
    protected void resetState() {
        foundPaths.clear();
    }

    @Override
    protected void onFile(FileNode file, int depth, String relativePath) {
        if (file.sizeInBytes() >= minimumSize && nameCriterion.test(file.name())) {
            foundPaths.add(relativePath);
        }
    }

    @Override
    protected void onDirectory(DirectoryNode directory, int depth) {
        // only files can match
    }
}

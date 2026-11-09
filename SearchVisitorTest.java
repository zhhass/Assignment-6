package kz.aitu.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SearchVisitorTest {

    @Test
    @DisplayName("T3: name criterion and size threshold are combined with AND")
    void combinesBothConstraints() {
        SearchVisitor visitor = SearchVisitor.withExtension(".java", 1000);
        Fixtures.project().accept(visitor);
        // Config.java (300) and User.java (800) match the name but are too small
        assertEquals(List.of("app/App.java", "app/model/Order.java"), visitor.matches());
    }

    @Test
    @DisplayName("T3: the size threshold is inclusive (size >= threshold)")
    void thresholdIsInclusive() {
        SearchVisitor visitor = SearchVisitor.withExtension(".java", 800);
        Fixtures.project().accept(visitor);
        assertTrue(visitor.matches().contains("app/model/User.java"));
        assertEquals(3, visitor.matches().size());
    }

    @Test
    @DisplayName("T3: a custom predicate can be supplied as the name criterion")
    void customNameCriterion() {
        SearchVisitor visitor = new SearchVisitor(name -> name.startsWith("i") || name.startsWith("g"), 0);
        Fixtures.project().accept(visitor);
        assertEquals(List.of("docs/api/index.md", "docs/guide.md"), visitor.matches());
    }

    @Test
    @DisplayName("T4: paths are root-relative, '/'-separated and sorted lexicographically")
    void pathsAreRelativeAndSorted() {
        SearchVisitor visitor = SearchVisitor.nameContains(".", 0);
        Fixtures.project().accept(visitor);
        assertEquals(List.of(
                "app/App.java", "app/Config.java", "app/model/Order.java", "app/model/User.java",
                "build.gradle", "docs/api/index.md", "docs/guide.md", "notes.txt"), visitor.matches());
    }

    @Test
    @DisplayName("T4: paths are relative to the chosen root, not to the whole tree")
    void pathsDependOnChosenRoot() {
        SearchVisitor visitor = SearchVisitor.withExtension(".java", 0);
        Fixtures.appDirectory().accept(visitor);
        assertEquals(List.of("App.java", "Config.java", "model/Order.java", "model/User.java"),
                visitor.matches());
    }

    @Test
    @DisplayName("T4: when the root is a file the path is just its name")
    void fileRootPath() {
        SearchVisitor visitor = SearchVisitor.withExtension(".txt", 5);
        new FileNode("solo.txt", 10).accept(visitor);
        assertEquals(List.of("solo.txt"), visitor.matches());
    }

    @Test
    @DisplayName("T6: no match and empty directory give an empty list")
    void noMatchAndEmptyDirectory() {
        SearchVisitor visitor = SearchVisitor.withExtension(".java", 1_000_000);
        Fixtures.project().accept(visitor);
        assertTrue(visitor.matches().isEmpty());
        visitor.visit(new DirectoryNode("void"));
        assertTrue(visitor.matches().isEmpty());
    }

    @Test
    @DisplayName("R4: invalid search configuration is rejected")
    void invalidConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> SearchVisitor.withExtension(".java", -1));
        assertThrows(NullPointerException.class, () -> new SearchVisitor(null, 0));
    }
}

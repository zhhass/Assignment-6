package kz.aitu.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatisticsVisitorTest {

    @Test
    @DisplayName("T5: counts files, directories (root included) and max depth over nested levels")
    void nestedStatistics() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        Fixtures.project().accept(visitor);
        assertEquals(8, visitor.fileCount());
        assertEquals(6, visitor.directoryCount());
        assertEquals(3, visitor.maxDepth());
    }

    @Test
    @DisplayName("T5: statistics of a subtree restart the depth at the chosen root")
    void subtreeStatistics() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        Fixtures.appDirectory().accept(visitor);
        assertEquals(4, visitor.fileCount());
        assertEquals(3, visitor.directoryCount());
        assertEquals(2, visitor.maxDepth());
    }

    @Test
    @DisplayName("T6: an empty root directory counts itself, depth 0")
    void emptyRoot() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        new DirectoryNode("void").accept(visitor);
        assertEquals(0, visitor.fileCount());
        assertEquals(1, visitor.directoryCount());
        assertEquals(0, visitor.maxDepth());
    }

    @Test
    @DisplayName("T6: a file as root: one file, no directories, depth 0")
    void fileRoot() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        new FileNode("a", 1).accept(visitor);
        assertEquals(1, visitor.fileCount());
        assertEquals(0, visitor.directoryCount());
        assertEquals(0, visitor.maxDepth());
    }

    @Test
    @DisplayName("R5: a deep empty directory chain still determines the max depth")
    void emptyLeafDirectoryDeterminesDepth() {
        DirectoryNode root = new DirectoryNode("r");
        DirectoryNode current = root;
        for (int level = 1; level <= 5; level++) {
            DirectoryNode next = new DirectoryNode("d" + level);
            current.add(next);
            current = next;
        }
        StatisticsVisitor visitor = new StatisticsVisitor();
        root.accept(visitor);
        assertEquals(5, visitor.maxDepth());
        assertEquals(6, visitor.directoryCount());
    }
}

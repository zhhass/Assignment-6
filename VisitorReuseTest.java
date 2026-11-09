package kz.aitu.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VisitorReuseTest {

    @Test
    @DisplayName("T7: running TotalSizeVisitor twice on the same root gives the same result")
    void sizeVisitorDoesNotAccumulate() {
        DirectoryNode root = Fixtures.project();
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        root.accept(visitor);
        assertEquals(4780, visitor.totalSize());
    }

    @Test
    @DisplayName("T7: running SearchVisitor twice does not duplicate paths")
    void searchVisitorDoesNotDuplicate() {
        DirectoryNode root = Fixtures.project();
        SearchVisitor visitor = SearchVisitor.withExtension(".md", 0);
        root.accept(visitor);
        root.accept(visitor);
        assertEquals(List.of("docs/api/index.md", "docs/guide.md"), visitor.matches());
    }

    @Test
    @DisplayName("T7: StatisticsVisitor does not leak counts or depth into the next run")
    void statisticsVisitorDoesNotLeak() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        Fixtures.project().accept(visitor);
        Fixtures.appDirectory().accept(visitor);
        assertEquals(4, visitor.fileCount());
        assertEquals(3, visitor.directoryCount());
        assertEquals(2, visitor.maxDepth());
    }

    @Test
    @DisplayName("T7: a smaller tree after a bigger one is not polluted (order independence)")
    void runOrderDoesNotMatter() {
        TotalSizeVisitor reused = new TotalSizeVisitor();
        Fixtures.project().accept(reused);
        new FileNode("tiny", 5).accept(reused);
        TotalSizeVisitor fresh = new TotalSizeVisitor();
        new FileNode("tiny", 5).accept(fresh);
        assertEquals(fresh.totalSize(), reused.totalSize());
    }

    @Test
    @DisplayName("T7: results follow tree changes between runs")
    void changedTreeIsReflectedOnNextRun() {
        DirectoryNode root = Fixtures.project();
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        root.add(new FileNode("extra.bin", 20));
        root.accept(visitor);
        assertEquals(4800, visitor.totalSize());
    }
}

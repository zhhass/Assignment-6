package kz.aitu.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
class TotalSizeVisitorTest {

    @Test
    @DisplayName("T2: sizes accumulate across several nesting levels")
    void sumsAllNestedFiles() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        Fixtures.project().accept(visitor);
        assertEquals(4780, visitor.totalSize());
    }

    @Test
    @DisplayName("T2: a subtree counts only its own files")
    void subtreeTotalIgnoresSiblings() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        Fixtures.appDirectory().accept(visitor);
        assertEquals(3800, visitor.totalSize());
    }

    @Test
    @DisplayName("T6: empty directory and zero-byte file give 0")
    void emptyStructuresHaveZeroSize() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        new DirectoryNode("void").add(new DirectoryNode("inner")).accept(visitor);
        assertEquals(0, visitor.totalSize());
        new FileNode("zero.bin", 0).accept(visitor);
        assertEquals(0, visitor.totalSize());
    }

    @Test
    @DisplayName("R3: a single file as root contributes exactly its own size")
    void fileAsRoot() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        new FileNode("x.dat", 77).accept(visitor);
        assertEquals(77, visitor.totalSize());
    }
}

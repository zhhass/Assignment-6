package kz.aitu.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TreeIntegrityTest {

    @Test
    @DisplayName("T8: negative file size is rejected")
    void negativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad", -1));
    }

    @Test
    @DisplayName("T8: illegal names are rejected")
    void illegalNames() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("", 1));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode("a/b"));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(".."));
    }

    @Test
    @DisplayName("T8: duplicate sibling names are rejected and the tree stays unchanged")
    void duplicateSiblingName() {
        DirectoryNode dir = new DirectoryNode("d").add(new FileNode("same", 1));
        assertThrows(IllegalArgumentException.class, () -> dir.add(new DirectoryNode("same")));
        assertEquals(1, dir.children().size());
        assertTrue(dir.find("same").isPresent());
    }

    @Test
    @DisplayName("T8: a node cannot have two parents")
    void secondParentRejected() {
        FileNode file = new FileNode("f", 1);
        DirectoryNode first = new DirectoryNode("first").add(file);
        DirectoryNode second = new DirectoryNode("second");
        assertThrows(IllegalStateException.class, () -> second.add(file));
        assertTrue(second.children().isEmpty());
        assertSame(first, file.parent().orElseThrow());
    }

    @Test
    @DisplayName("T8: adding a directory to itself or to its own descendant is rejected")
    void cyclesRejected() {
        DirectoryNode top = new DirectoryNode("top");
        DirectoryNode middle = new DirectoryNode("middle");
        DirectoryNode bottom = new DirectoryNode("bottom");
        top.add(middle);
        middle.add(bottom);
        assertThrows(IllegalArgumentException.class, () -> top.add(top));
        assertThrows(IllegalArgumentException.class, () -> bottom.add(top));
        assertThrows(IllegalArgumentException.class, () -> bottom.add(middle));
        assertTrue(top.parent().isEmpty());
    }

    @Test
    @DisplayName("T8: null child is rejected")
    void nullChild() {
        assertThrows(NullPointerException.class, () -> new DirectoryNode("d").add(null));
    }

    @Test
    @DisplayName("T8: the children view cannot be used to modify the directory")
    void childrenViewIsImmutable() {
        DirectoryNode dir = new DirectoryNode("d").add(new FileNode("f", 1));
        assertThrows(UnsupportedOperationException.class, () -> dir.children().add(new FileNode("g", 2)));
        assertThrows(UnsupportedOperationException.class, () -> dir.children().clear());
    }

    @Test
    @DisplayName("T8: valid deep nesting works, and a removed node can be moved elsewhere")
    void validNestingAndMove() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode cursor = root;
        for (int level = 0; level < 50; level++) {
            DirectoryNode next = new DirectoryNode("level" + level);
            cursor.add(next);
            cursor = next;
        }
        cursor.add(new FileNode("deep.txt", 3));
        StatisticsVisitor stats = new StatisticsVisitor();
        root.accept(stats);
        assertEquals(51, stats.maxDepth());

        DirectoryNode a = new DirectoryNode("a").add(new FileNode("moved.txt", 9));
        DirectoryNode b = new DirectoryNode("b");
        Node moved = a.remove("moved.txt").orElseThrow();
        assertTrue(moved.parent().isEmpty());
        b.add(moved);
        assertSame(b, moved.parent().orElseThrow());
        assertTrue(a.children().isEmpty());
    }
}

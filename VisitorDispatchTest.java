package kz.aitu.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VisitorDispatchTest {

    private static final class RecordingVisitor implements Visitor {
        final List<String> calls = new ArrayList<>();

        @Override
        public void visit(FileNode file) {
            calls.add("file:" + file.name());
        }

        @Override
        public void visit(DirectoryNode directory) {
            calls.add("directory:" + directory.name());
        }
    }

    @Test
    @DisplayName("T1: a FileNode dispatches to visit(FileNode)")
    void fileReachesFileOverload() {
        RecordingVisitor recorder = new RecordingVisitor();
        new FileNode("a.txt", 1).accept(recorder);
        assertEquals(List.of("file:a.txt"), recorder.calls);
    }

    @Test
    @DisplayName("T1: a DirectoryNode dispatches to visit(DirectoryNode)")
    void directoryReachesDirectoryOverload() {
        RecordingVisitor recorder = new RecordingVisitor();
        new DirectoryNode("d").accept(recorder);
        assertEquals(List.of("directory:d"), recorder.calls);
    }

    @Test
    @DisplayName("T1: static type Element still dispatches by runtime type")
    void dispatchWorksThroughTheCommonInterface() {
        RecordingVisitor recorder = new RecordingVisitor();
        List<Element> elements = List.of(new DirectoryNode("d"), new FileNode("f", 0));
        elements.forEach(element -> element.accept(recorder));
        assertEquals(List.of("directory:d", "file:f"), recorder.calls);
    }

    @Test
    @DisplayName("R7: a traversing visitor reaches every node exactly once, in name order")
    void traversalVisitsEachNodeOnce() {
        List<String> visited = new ArrayList<>();
        TraversingVisitor collector = new TraversingVisitor() {
            @Override
            protected void resetState() {
                visited.clear();
            }

            @Override
            protected void onFile(FileNode file, int depth, String relativePath) {
                visited.add("F " + relativePath);
            }

            @Override
            protected void onDirectory(DirectoryNode directory, int depth) {
                visited.add("D " + directory.name());
            }
        };
        Fixtures.appDirectory().accept(collector);
        // children are ordered by plain String order (upper-case before lower-case): Order.java, User.java, empty
        assertEquals(List.of("D app", "F App.java", "F Config.java", "D model",
                "F model/Order.java", "F model/User.java", "D empty"), visited);
    }
}

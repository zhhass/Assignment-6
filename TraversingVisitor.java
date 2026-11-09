package kz.aitu.filesystem;

import java.util.ArrayDeque;
import java.util.Deque;


public abstract class TraversingVisitor implements Visitor {

    private static final int NOT_TRAVERSING = -1;

    private int depth = NOT_TRAVERSING;
    private final Deque<String> directoryNamesBelowRoot = new ArrayDeque<>();

    @Override
    public final void visit(FileNode file) {
        startRunIfOutermost();
        depth++;
        try {
            onFile(file, depth, relativePathOf(file));
        } finally {
            depth--;
        }
    }

    @Override
    public final void visit(DirectoryNode directory) {
        startRunIfOutermost();
        depth++;
        boolean belowRoot = depth > 0;
        if (belowRoot) {
            directoryNamesBelowRoot.addLast(directory.name());
        }
        try {
            onDirectory(directory, depth);
            for (Node child : directory.children()) {
                child.accept(this);
            }
        } finally {
            if (belowRoot) {
                directoryNamesBelowRoot.removeLast();
            }
            depth--;
        }
    }

    /** Clears results of the previous run. Called once at the start of each top-level traversal. */
    protected abstract void resetState();

    protected abstract void onFile(FileNode file, int depth, String relativePath);

    protected abstract void onDirectory(DirectoryNode directory, int depth);

    private void startRunIfOutermost() {
        if (depth == NOT_TRAVERSING) {
            directoryNamesBelowRoot.clear();
            resetState();
        }
    }

    private String relativePathOf(FileNode file) {
        if (directoryNamesBelowRoot.isEmpty()) {
            return file.name();
        }
        return String.join("/", directoryNamesBelowRoot) + "/" + file.name();
    }
}

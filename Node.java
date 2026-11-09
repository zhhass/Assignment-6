package kz.aitu.filesystem;

import java.util.Optional;


public abstract class Node implements Element {

    private final String name;
    private DirectoryNode parent;

    protected Node(String name) {
        this.name = requireValidName(name);
    }

    @Override
    public String name() {
        return name;
    }

    public Optional<DirectoryNode> parent() {
        return Optional.ofNullable(parent);
    }

    DirectoryNode parentOrNull() {
        return parent;
    }

    void attachTo(DirectoryNode newParent) {
        this.parent = newParent;
    }

    void detach() {
        this.parent = null;
    }

    private static String requireValidName(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        if (candidate.contains("/") || candidate.equals(".") || candidate.equals("..")) {
            throw new IllegalArgumentException("Illegal node name: '" + candidate + "'");
        }
        return candidate;
    }
}

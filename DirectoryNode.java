package kz.aitu.filesystem;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

public final class DirectoryNode extends Node {

    private final Map<String, Node> childrenByName = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }


    public DirectoryNode add(Node child) {
        Objects.requireNonNull(child, "child");
        rejectCycle(child);
        rejectSecondParent(child);
        rejectDuplicateName(child);
        childrenByName.put(child.name(), child);
        child.attachTo(this);
        return this;
    }

    public Optional<Node> remove(String childName) {
        Node removed = childrenByName.remove(childName);
        if (removed != null) {
            removed.detach();
        }
        return Optional.ofNullable(removed);
    }

    public Optional<Node> find(String childName) {
        return Optional.ofNullable(childrenByName.get(childName));
    }

    public List<Node> children() {
        return List.copyOf(childrenByName.values());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    private void rejectCycle(Node child) {
        for (DirectoryNode cursor = this; cursor != null; cursor = cursor.parentOrNull()) {
            if (cursor == child) {
                throw new IllegalArgumentException(
                        "Adding '" + child.name() + "' to '" + name() + "' would create a cycle");
            }
        }
    }

    private void rejectSecondParent(Node child) {
        if (child.parentOrNull() != null) {
            throw new IllegalStateException(
                    "'" + child.name() + "' already belongs to '" + child.parentOrNull().name() + "'");
        }
    }

    private void rejectDuplicateName(Node child) {
        if (childrenByName.containsKey(child.name())) {
            throw new IllegalArgumentException(
                    "Directory '" + name() + "' already contains '" + child.name() + "'");
        }
    }
}

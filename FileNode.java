package kz.aitu.filesystem;

public final class FileNode extends Node {

    private final long sizeInBytes;

    public FileNode(String name, long sizeInBytes) {
        super(name);
        if (sizeInBytes < 0) {
            throw new IllegalArgumentException("File size must not be negative: " + sizeInBytes);
        }
        this.sizeInBytes = sizeInBytes;
    }

    public long sizeInBytes() {
        return sizeInBytes;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

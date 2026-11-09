package kz.aitu.filesystem;


public final class TotalSizeVisitor extends TraversingVisitor {

    private long totalBytes;

    public long totalSize() {
        return totalBytes;
    }

    @Override
    protected void resetState() {
        totalBytes = 0;
    }

    @Override
    protected void onFile(FileNode file, int depth, String relativePath) {
        totalBytes = Math.addExact(totalBytes, file.sizeInBytes());
    }

    @Override
    protected void onDirectory(DirectoryNode directory, int depth) {
        // directories have no byte size of their own
    }
}

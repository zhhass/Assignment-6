package kz.aitu.filesystem;


public final class StatisticsVisitor extends TraversingVisitor {

    private int fileCount;
    private int directoryCount;
    private int maxDepth;

    public int fileCount() {
        return fileCount;
    }

    public int directoryCount() {
        return directoryCount;
    }

    public int maxDepth() {
        return maxDepth;
    }

    @Override
    protected void resetState() {
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
    }

    @Override
    protected void onFile(FileNode file, int depth, String relativePath) {
        fileCount++;
        maxDepth = Math.max(maxDepth, depth);
    }

    @Override
    protected void onDirectory(DirectoryNode directory, int depth) {
        directoryCount++;
        maxDepth = Math.max(maxDepth, depth);
    }
}

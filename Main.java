package kz.aitu.filesystem;

public final class Main {

    public static void main(String[] args) {
        DirectoryNode backend = buildBackend();
        DirectoryNode root = new DirectoryNode("shop")
                .add(new FileNode("pom.xml", 1800))
                .add(new FileNode("README.md", 700))
                .add(backend)
                .add(new DirectoryNode("assets")
                        .add(new FileNode("logo.png", 15000))
                        .add(new FileNode("banner.png", 48000)));

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.println("Total size of '" + root.name() + "': " + sizeVisitor.totalSize() + " bytes");

        SearchVisitor search = SearchVisitor.withExtension(".java", 1000);
        root.accept(search);
        System.out.println("Search *.java with size >= 1000:");
        search.matches().forEach(path -> System.out.println("  " + path));

        StatisticsVisitor statistics = new StatisticsVisitor();
        root.accept(statistics);
        System.out.println("Statistics: files=" + statistics.fileCount()
                + ", directories=" + statistics.directoryCount()
                + ", maxDepth=" + statistics.maxDepth());

        backend.accept(sizeVisitor);
        System.out.println("Reused size visitor on subtree 'backend': " + sizeVisitor.totalSize() + " bytes");
    }

    private static DirectoryNode buildBackend() {
        DirectoryNode controllers = new DirectoryNode("controllers")
                .add(new FileNode("OrderController.java", 2100))
                .add(new FileNode("UserController.java", 950));
        return new DirectoryNode("backend")
                .add(new FileNode("Server.java", 3400))
                .add(controllers)
                .add(new DirectoryNode("tmp"));
    }
}

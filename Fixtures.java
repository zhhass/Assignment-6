package kz.aitu.filesystem;
final class Fixtures {

    private Fixtures() {
    }

    static DirectoryNode project() {
        return new DirectoryNode("project")
                .add(new FileNode("build.gradle", 40))
                .add(new FileNode("notes.txt", 90))
                .add(appDirectory())
                .add(new DirectoryNode("docs")
                        .add(new FileNode("guide.md", 600))
                        .add(new DirectoryNode("api").add(new FileNode("index.md", 250))));
    }

    static DirectoryNode appDirectory() {
        return new DirectoryNode("app")
                .add(new FileNode("App.java", 1200))
                .add(new FileNode("Config.java", 300))
                .add(new DirectoryNode("model")
                        .add(new FileNode("Order.java", 1500))
                        .add(new FileNode("User.java", 800))
                        .add(new DirectoryNode("empty")));
    }
}

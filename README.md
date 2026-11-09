# Visitor Pattern - Advanced File System

ShP-2216 Software Design Patterns, Assignment 06 (Java 17, Maven, JUnit 5).

An in-memory hierarchical file system (`FileNode`, `DirectoryNode`) with three independent
operations implemented as visitors: `TotalSizeVisitor`, `SearchVisitor`, `StatisticsVisitor`.

## Commands

Run all tests (from the project root, no IDE or manual input):

```
mvn test
```

Run the deterministic demonstration:

```
mvn -q compile exec:java
```

(or, after `mvn compile`: `java -cp target/classes kz.aitu.filesystem.Main`)

Expected demo output:

```
Total size of 'shop': 71950 bytes
Search *.java with size >= 1000:
  backend/Server.java
  backend/controllers/OrderController.java
Statistics: files=7, directories=5, maxDepth=3
Reused size visitor on subtree 'backend': 6450 bytes
```

## Design decisions

* **Double dispatch.** `accept(Visitor)` in each element calls the overload `visit(this)` that
  matches its own type; the visitor then runs the operation for that type. No `instanceof`,
  `getClass()`, reflection or casts anywhere in `src/main`.
* **Traversal owner: the visitors.** `TraversingVisitor` is the single base class that recurses
  (`DirectoryNode` visit -> `child.accept(this)` for each child, sorted by name). Elements only
  dispatch. Each reachable node is visited exactly once per run. It also tracks depth
  (root = 0) and the root-relative path (`/` separated; a file used as root has just its name).
* **Controlled state / reuse.** Visitor instances are reusable: `resetState()` is called
  automatically at the start of every top-level traversal, so results never accumulate and
  never depend on run order. Results always describe the latest run.
* **Tree integrity (R6).** `DirectoryNode.add` rejects, leaving the tree unchanged:
  cycles (`IllegalArgumentException`), a node that already has a parent
  (`IllegalStateException`), duplicate sibling names (`IllegalArgumentException`).
  `FileNode` rejects negative sizes; names must be non-blank and contain no `/`.
  `children()` returns an immutable snapshot. `remove(name)` detaches a node so it can be moved.
* **Extension boundary (R8).** New *operations* = new visitor class (`StatisticsVisitor` was
  added without touching `FileNode`, `DirectoryNode` or their `accept` methods; see git history).
  A new *element type* would require a new `visit` overload in `Visitor` and in every visitor.

## Tests (JUnit 5, 34 tests)

`VisitorDispatchTest` (T1, R7), `TotalSizeVisitorTest` (T2, T6), `SearchVisitorTest` (T3, T4, T6),
`StatisticsVisitorTest` (T5, T6), `VisitorReuseTest` (T7), `TreeIntegrityTest` (T8).

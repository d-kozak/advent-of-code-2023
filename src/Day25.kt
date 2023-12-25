private data class Node(val name: String) {
    val nei = mutableSetOf<Node>()
}

private fun readGraph(input: List<String>): Map<String, Node> {
    val nodes = mutableMapOf<String, Node>()
    for (line in input) {
        val (left, right) = line.split(": ")
        val leftNode = nodes.computeIfAbsent(left) { Node(left) }
        for (next in right.split(" ").map { it.trim() }) {
            val nextNode = nodes.computeIfAbsent(next) { Node(next) }
            leftNode.nei.add(nextNode)
            nextNode.nei.add(leftNode)
        }
    }
    return nodes
}

private fun dfs(startNode: Node, counter: MutableMap<Set<Node>, Int>?): Int {
    val queue = ArrayDeque<Node>()
    val seen = mutableSetOf<Node>()
    queue.add(startNode)
    seen.add(startNode)
    while (queue.isNotEmpty()) {
        val curr = queue.removeFirst()
        for (next in curr.nei) {
            if (seen.add(next)) {
                if (counter != null) {
                    val key = setOf(curr, next)
                    counter[key] = counter.getOrDefault(key, 0) + 1
                }
                queue.add(next)
            }
        }
    }
    return seen.size
}


private fun removeEdge(edge: Set<Node>, graph: Map<String, Node>) {
    val it = edge.iterator()
    val left = it.next()
    val right = it.next()
    graph.getValue(left.name).nei.remove(right)
    graph.getValue(right.name).nei.remove(left)
}


private fun addEdge(edge: Set<Node>, graph: Map<String, Node>) {
    val it = edge.iterator()
    val left = it.next()
    val right = it.next()
    graph.getValue(left.name).nei.add(right)
    graph.getValue(right.name).nei.add(left)
}

fun main() {
    val graph = readGraph(readInput("Day25"))
    val n = graph.size
    val fstNode = graph.values.iterator().next()
//    for (node in graph.values) {
//        println("${node.name} -> ${node.nei.map { it.name }}")
//    }
    val counter = mutableMapOf<Set<Node>, Int>()
    for (node in graph.values) {
        check(dfs(node, counter) == n) { "${dfs(node, counter)} $n" }
    }
    val sortedEdges = counter.entries.sortedByDescending { it.value }.map { it.key }
    val m = sortedEdges.size
    for (i in 0 until m) {
        removeEdge(sortedEdges[i], graph)
        for (j in i + 1 until m) {
            removeEdge(sortedEdges[j], graph)
            for (k in j + 1 until m) {
                removeEdge(sortedEdges[k], graph)
                val x = dfs(fstNode, null)
                if (x != n) {
                    println(x * (n - x))
                    return
                }
                addEdge(sortedEdges[k], graph)
            }
            addEdge(sortedEdges[j], graph)
        }
        addEdge(sortedEdges[i], graph)
    }
}
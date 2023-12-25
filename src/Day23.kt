import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.max

data class EdgeOld(val cost: Int, val target: NodeOld)
data class NodeOld(val r: Int, val c: Int) : Comparable<NodeOld> {
    val edges = mutableSetOf<EdgeOld>()
    fun check() {
        for (edge in edges) {
            if (edge.target == this) {
                error("Self loop")
            }
        }
    }

    override fun compareTo(other: NodeOld): Int {
        val cmp = this.r.compareTo(other.r)
        if (cmp != 0) return cmp
        return this.c.compareTo(other.c)
    }
}

fun main() {
    val cells = readInput("Day23")
    val n = cells.size
    val m = cells[0].length
    val startCol = cells[0].indexOf('.')
    val endCol = cells[n - 1].indexOf('.')
    var res = 0

    val visited = Array(n) { BooleanArray(m) }
    val nei = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
    val left = listOf(0 to -1)
    val right = listOf(0 to 1)
    val top = listOf(-1 to 0)
    val bottom = listOf(1 to 0)

    fun dumpPath() {
        for (r in 0 until n) {
            for (c in 0 until m) {
                val cell = if (visited[r][c]) 'O' else cells[r][c]
                print(cell)
            }
            println()
        }
        println()
    }

    var part2 = false

    fun goRecursive(r: Int, c: Int, dist: Int) {
        if (r == n - 1 && c == endCol) {
            if (dist > res) {
//                dumpPath()
//                println(dist)
            }
            res = max(res, dist)
            return
        }
        if (visited[r][c]) return
        visited[r][c] = true
        val next = if (part2) nei else when (cells[r][c]) {
            '>' -> right
            '<' -> left
            'v' -> bottom
            '^' -> top
            '.' -> nei
            else -> error("Bad char ${cells[r][c]}")
        }
        for ((dr, dc) in next) {
            val nr = r + dr
            val nc = c + dc
            if (nr in 0 until n && nc in 0 until m && cells[nr][nc] != '#') {
                goRecursive(nr, nc, dist + 1)
            }
        }
        visited[r][c] = false
    }

    fun go(sr: Int, sc: Int) {
        val stack = mutableListOf<Triple<Int, Int, Int>>()
        stack.add(Triple(sr, sc, 0))
        while (stack.isNotEmpty()) {
            val (r, c, dist) = stack.removeAt(stack.size - 1)
//            println("$r $c $dist")
            if (dist == -1) {
                visited[r][c] = false
                continue
            }
            if (r == n - 1 && c == endCol) {
                if (dist > res) {
                    dumpPath()
                    println(dist)
                }
                res = max(res, dist)
                continue
            }
            if (visited[r][c]) continue
            visited[r][c] = true
            val next = if (part2) nei else when (cells[r][c]) {
                '>' -> right
                '<' -> left
                'v' -> bottom
                '^' -> top
                '.' -> nei
                else -> error("Bad char ${cells[r][c]}")
            }
            stack.add(Triple(r, c, -1))
            for ((dr, dc) in next) {
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until n && nc in 0 until m && cells[nr][nc] != '#') {
                    stack.add(Triple(nr, nc, dist + 1))
                }
            }
        }
    }

    fun createGraph(sr: Int, sc: Int): MutableMap<Pair<Int, Int>, NodeOld> {
        val nodes = mutableMapOf<Pair<Int, Int>, NodeOld>()
        for (r in 0 until n) {
            for (c in 0 until m) {
                if (cells[r][c] == '#') continue
                val from = nodes.computeIfAbsent(r to c) { NodeOld(r, c) }
                for ((dr, dc) in nei) {
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until n && nc in 0 until m && cells[nr][nc] != '#') {
                        val to = nodes.computeIfAbsent(nr to nc) { NodeOld(nr, nc) }
                        from.edges.add(EdgeOld(1, to))
                        to.edges.add(EdgeOld(1, from))
                    }
                }
            }
        }
        nodes.values.forEach { it.check() }
        return nodes
    }

    fun simplify(nodes: MutableMap<Pair<Int, Int>, NodeOld>): MutableSet<NodeOld> {
        val liveNodes = TreeSet(nodes.values)
        var cnt = 0
        val worklist = ArrayDeque(nodes.values)
        while (worklist.isNotEmpty()) {
            val node = worklist.removeFirst()
            if (node !in liveNodes) continue
            if (node.edges.size == 2) {
                println("$node can be simplified")
                cnt++
                val it = node.edges.iterator()
                val left = it.next()
                val right = it.next()
                left.target.edges.removeIf { it.target == node }
                right.target.edges.removeIf { it.target == node }
                val total = left.cost + right.cost
                left.target.edges.add(EdgeOld(total, right.target))
                right.target.edges.add(EdgeOld(total, left.target))
                worklist.add(left.target)
                worklist.add(right.target)
                left.target.check()
                right.target.check()
                liveNodes.remove(node)
            }
        }
        println("Removed $cnt nodes")
        return liveNodes
    }



    go(0, startCol)
    println(res)
    res = 0
    part2 = true
//    go(0, startCol)
    // 6602 - too low
//    println(res)
    val nodes = createGraph(0, startCol)
    val liveNodes = simplify(nodes)
    val startNode = nodes.getValue(0 to startCol)
    check(startNode in liveNodes)
    val endNode = nodes.getValue(n - 1 to endCol)
    check(endNode in liveNodes)

    for (node in liveNodes) {
        println(node)
        for (edge in node.edges) {
            println("\t $edge")
        }
    }
    println("Nodes ${liveNodes.size}")

    val seen = mutableSetOf<NodeOld>()
    res = 0

    fun go2(node: NodeOld, dist: Int) {
        if (node == endNode) {
            res = max(res, dist)
            return
        }
        if (!seen.add(node)) return
        for (edge in node.edges) {
            go2(edge.target, dist + edge.cost)
        }
        seen.remove(node)
    }

    go2(startNode, 0)
    println(res)
}
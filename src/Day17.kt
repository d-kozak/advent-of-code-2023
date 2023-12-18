import java.util.*

enum class Dir(val dr: Int, val dc: Int) {
    LEFT(0, -1), RIGHT(0, 1), UP(-1, 0), DOWN(1, 0);

    val left: Dir
        get() = when (this) {
            LEFT -> DOWN
            RIGHT -> UP
            UP -> LEFT
            DOWN -> RIGHT
        }

    val right: Dir
        get() = when (this) {
            LEFT -> UP
            RIGHT -> DOWN
            UP -> RIGHT
            DOWN -> LEFT
        }

    val viz: Char
        get() = when (this) {
            LEFT -> '<'
            RIGHT -> '>'
            UP -> '^'
            DOWN -> 'v'
        }
}

fun main() {
    data class State(var r: Int, var c: Int, var dir: Dir, var cnt: Int, var score: Int, var parent: State? = null) :
        Comparable<State> {
        override fun compareTo(other: State) = this.score.compareTo(other.score)
    }

    val input = readInput("Day17").map { it.toCharArray() }.toTypedArray()
    val n = input.size
    val m = input[0].size
    val queue = PriorityQueue<State>()
    val seen = mutableSetOf<List<Any>>()

    fun maybeAdd(curr: State, dir: Dir, cnt: Int) {
        val nr = curr.r + dir.dr
        val nc = curr.c + dir.dc
        if (nr in 0 until n && nc in 0 until m) {
            queue.add(State(nr, nc, dir, cnt, curr.score + (input[nr][nc] - '0'), curr))
        }
    }

    fun printPath(state: State) {
        var score = 0
        var curr = state as State?
        while (curr != null) {
            if (!(curr.r == 0 && curr.c == 0 && curr.score == 0))
                score += input[curr.r][curr.c] - '0'
            input[curr.r][curr.c] = curr.dir.viz
            curr = curr.parent
        }
        for (line in input)
            println(line.joinToString(""))
        println(score)
    }

    queue.add(State(0, 0, Dir.RIGHT, 0, 0))
    queue.add(State(0, 0, Dir.DOWN, 0, 0))
    while (queue.isNotEmpty()) {
        val curr = queue.remove()
        if (!seen.add(listOf(curr.r, curr.c, curr.dir, curr.cnt)))
            continue
//        println(curr)
        if (curr.r == n - 1 && curr.c == m - 1) {
//            printPath(curr)
            println(curr.score)
            break
        }
        if (curr.cnt < 3) {
            maybeAdd(curr, curr.dir, curr.cnt + 1)
        }
        maybeAdd(curr, curr.dir.left, 1)
        maybeAdd(curr, curr.dir.right, 1)
    }

    queue.clear()
    seen.clear()

    queue.add(State(0, 0, Dir.RIGHT, 0, 0))
    queue.add(State(0, 0, Dir.DOWN, 0, 0))
    while (queue.isNotEmpty()) {
        val curr = queue.remove()
        if (!seen.add(listOf(curr.r, curr.c, curr.dir, curr.cnt)))
            continue
        if (curr.r == n - 1 && curr.c == m - 1 && curr.cnt >= 4) {
            printPath(curr)
            println(curr.score)
            break
        }
        if (curr.cnt < 10) {
            maybeAdd(curr, curr.dir, curr.cnt + 1)
        }
        if (curr.cnt >= 4) {
            maybeAdd(curr, curr.dir.left, 1)
            maybeAdd(curr, curr.dir.right, 1)
        }
    }
}

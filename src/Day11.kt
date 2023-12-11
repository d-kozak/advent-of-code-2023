import java.util.*

fun main() {
    val input = readInput("Day11")
    val n = input.size
    val m = input[0].length
    val expandedCols = (0 until m).filter { col -> (0 until n).none { row -> input[row][col] == '#' } }.toSet()
    val expandedRows = (0 until n).filter { row -> (0 until m).none { col -> input[row][col] == '#' } }.toSet()
    val galaxies = mutableListOf<Pair<Int, Int>>()
    for (i in 0 until n) {
        for (j in 0 until m) {
            if (input[i][j] == '#')
                galaxies.add(i to j)
        }
    }
    data class State(val pos: Pair<Int, Int>, val dist: Long) : Comparable<State> {
        override fun compareTo(other: State): Int = dist.compareTo(other.dist)
    }

    var expansion = 2L

    val nei = listOf(+1 to 0, -1 to 0, 0 to +1, 0 to -1)
    fun dist(from: Int, to: Int): Long {
        val start = galaxies[from]
        val end = galaxies[to]
        val queue = PriorityQueue<State>()
        queue.add(State(start, 0))
        val seen = Array(n) { BooleanArray(m) }
        while (queue.isNotEmpty()) {
            val (pos, d) = queue.remove()
            val (x, y) = pos
            if (seen[x][y]) continue
            seen[x][y] = true
            if (pos == end) return d
            for ((dx, dy) in nei) {
                val nx = x + dx
                val ny = y + dy
                if (nx in 0 until n && ny in 0 until m) {
                    val nextDist = if (nx in expandedRows || ny in expandedCols) d + expansion else d + 1
                    queue.add(State(nx to ny, nextDist))
                }
            }
        }
        error("Should not reach here")
    }

    fun solve(): Long {
        var res = 0L
        for (from in galaxies.indices) {
            for (to in from + 1 until galaxies.size) {
                val dist = dist(from, to)
                println("${from + 1} ${to + 1}: $dist")
                res += dist
            }
        }
        return res
    }

    fun solveFaster(): Long {
        val dist = mutableMapOf<Pair<Pair<Int, Int>, Pair<Int, Int>>, Long>()
        for (sx in 0 until n) {
            for (sy in 0 until m) {
                for (nx in 0 until n) {
                    for (ny in 0 until m) {
                        dist[(sx to sy) to (nx to ny)] = if (sx == nx && sy == ny) 0 else 10_000_000L
                    }
                }
            }
        }
        for (x in 0 until n) {
            for (y in 0 until m) {
                for ((dx, dy) in nei) {
                    val nx = x + dx
                    val ny = y + dy
                    if (nx in 0 until n && ny in 0 until m) {
                        val d = if (nx in expandedRows || ny in expandedCols) expansion else 1
                        dist[(x to y) to (nx to ny)] = d
                    }
                }
            }
        }
        for (mx in 0 until n) {
            for (my in 0 until m) {
                for (sx in 0 until n) {
                    for (sy in 0 until m) {
                        for (ex in 0 until n) {
                            for (ey in 0 until m) {
                                val start = sx to sy
                                val end = ex to ey
                                val mid = mx to my
                                val direct = dist.getValue(start to end)
                                val indirect = dist.getValue(start to mid) + dist.getValue(mid to end)
                                if (indirect < direct) {
                                    dist[start to end] = indirect
                                }
                            }
                        }
                    }
                }
            }
        }

        var res = 0L
        for (from in galaxies.indices) {
            for (to in from + 1 until galaxies.size) {
                val x = dist.getValue(galaxies[from] to galaxies[to])
//                println("${from + 1} ${to + 1}: $x")
                res += x
            }
        }
        return res
    }
    solve().println()
//    solveFaster().println()
    expansion = 1_000_000
    solve().println()
//    solveFaster().println()
}
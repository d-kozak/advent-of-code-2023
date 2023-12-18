import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.max

enum class DirOld(val dr: Int, val dc: Int) {
    UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1);
}

fun main() {
    data class State(val r: Int, val c: Int, val dir: DirOld)

    val maze = readInput("Day16")
    val n = maze.size
    val m = maze[0].length
    fun solve(startR: Int, startC: Int, startDir: DirOld): Int {
        val seen = Array(n) { Array(n) { EnumSet.noneOf(DirOld::class.java) } }

        val worklist = ArrayDeque<State>()
        worklist.add(State(startR, startC, startDir))
        while (worklist.isNotEmpty()) {
            val curr = worklist.removeFirst()
//        println(curr)
            val (r, c, dir) = curr
            if (r !in 0 until n || c !in 0 until m) continue
            if (!seen[r][c].add(dir)) continue
            val nr = r + dir.dr
            val nc = c + dir.dc
            if (maze[r][c] == '.' || (maze[r][c] == '|' && (dir == DirOld.UP || dir == DirOld.DOWN)) || (maze[r][c] == '-' && (dir == DirOld.LEFT || dir == DirOld.RIGHT))) {
                worklist.add(State(nr, nc, dir))
                continue
            }
            when (dir) {
                DirOld.UP -> when (maze[r][c]) {
                    '-' -> {
                        worklist.add(State(r, c - 1, DirOld.LEFT))
                        worklist.add(State(r, c + 1, DirOld.RIGHT))
                    }

                    '/' -> {
                        worklist.add(State(r, c + 1, DirOld.RIGHT))
                    }

                    '\\' -> {
                        worklist.add(State(r, c - 1, DirOld.LEFT))
                    }

                    else -> error("Unknown ${maze[r][c]}")
                }

                DirOld.DOWN -> when (maze[r][c]) {
                    '-' -> {
                        worklist.add(State(r, c - 1, DirOld.LEFT))
                        worklist.add(State(r, c + 1, DirOld.RIGHT))
                    }

                    '/' -> {
                        worklist.add(State(r, c - 1, DirOld.LEFT))
                    }

                    '\\' -> {
                        worklist.add(State(r, c + 1, DirOld.RIGHT))
                    }

                    else -> error("Unknown ${maze[r][c]}")
                }

                DirOld.LEFT -> when (maze[r][c]) {
                    '|' -> {
                        worklist.add(State(r - 1, c, DirOld.UP))
                        worklist.add(State(r + 1, c, DirOld.DOWN))
                    }


                    '/' -> {
                        worklist.add(State(r + 1, c, DirOld.DOWN))
                    }

                    '\\' -> {
                        worklist.add(State(r - 1, c, DirOld.UP))
                    }
                }

                DirOld.RIGHT -> when (maze[r][c]) {
                    '|' -> {
                        worklist.add(State(r - 1, c, DirOld.UP))
                        worklist.add(State(r + 1, c, DirOld.DOWN))
                    }

                    '/' -> {
                        worklist.add(State(r - 1, c, DirOld.UP))
                    }

                    '\\' -> {
                        worklist.add(State(r + 1, c, DirOld.DOWN))
                    }

                    else -> error("Unknown ${maze[r][c]}")
                }
            }
        }

        val energized = seen.sumOf { it.count { it.isNotEmpty() } }
        return energized
    }

    solve(0, 0, DirOld.RIGHT).println()

    var best = -1
    for (c in 0 until m) {
        best = max(best, solve(0, c, DirOld.DOWN))
        best = max(best, solve(n - 1, c, DirOld.UP))
    }
    for (r in 0 until n) {
        best = max(best, solve(n, 0, DirOld.RIGHT))
        best = max(best, solve(n, m - 1, DirOld.LEFT))
    }
    println(best)
}
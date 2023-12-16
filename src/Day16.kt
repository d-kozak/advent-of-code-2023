import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.count
import kotlin.collections.isNotEmpty
import kotlin.collections.sumOf
import kotlin.math.max

enum class Dir(val dr: Int, val dc: Int) {
    UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1);
}

fun main() {
    data class State(val r: Int, val c: Int, val dir: Dir)

    val maze = readInput("Day16")
    val n = maze.size
    val m = maze[0].length
    fun solve(startR: Int, startC: Int, startDir: Dir): Int {
        val seen = Array(n) { Array(n) { EnumSet.noneOf(Dir::class.java) } }

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
            if (maze[r][c] == '.' || (maze[r][c] == '|' && (dir == Dir.UP || dir == Dir.DOWN)) || (maze[r][c] == '-' && (dir == Dir.LEFT || dir == Dir.RIGHT))) {
                worklist.add(State(nr, nc, dir))
                continue
            }
            when (dir) {
                Dir.UP -> when (maze[r][c]) {
                    '-' -> {
                        worklist.add(State(r, c - 1, Dir.LEFT))
                        worklist.add(State(r, c + 1, Dir.RIGHT))
                    }

                    '/' -> {
                        worklist.add(State(r, c + 1, Dir.RIGHT))
                    }

                    '\\' -> {
                        worklist.add(State(r, c - 1, Dir.LEFT))
                    }

                    else -> error("Unknown ${maze[r][c]}")
                }

                Dir.DOWN -> when (maze[r][c]) {
                    '-' -> {
                        worklist.add(State(r, c - 1, Dir.LEFT))
                        worklist.add(State(r, c + 1, Dir.RIGHT))
                    }

                    '/' -> {
                        worklist.add(State(r, c - 1, Dir.LEFT))
                    }

                    '\\' -> {
                        worklist.add(State(r, c + 1, Dir.RIGHT))
                    }

                    else -> error("Unknown ${maze[r][c]}")
                }

                Dir.LEFT -> when (maze[r][c]) {
                    '|' -> {
                        worklist.add(State(r - 1, c, Dir.UP))
                        worklist.add(State(r + 1, c, Dir.DOWN))
                    }


                    '/' -> {
                        worklist.add(State(r + 1, c, Dir.DOWN))
                    }

                    '\\' -> {
                        worklist.add(State(r - 1, c, Dir.UP))
                    }
                }

                Dir.RIGHT -> when (maze[r][c]) {
                    '|' -> {
                        worklist.add(State(r - 1, c, Dir.UP))
                        worklist.add(State(r + 1, c, Dir.DOWN))
                    }

                    '/' -> {
                        worklist.add(State(r - 1, c, Dir.UP))
                    }

                    '\\' -> {
                        worklist.add(State(r + 1, c, Dir.DOWN))
                    }

                    else -> error("Unknown ${maze[r][c]}")
                }
            }
        }

        val energized = seen.sumOf { it.count { it.isNotEmpty() } }
        return energized
    }

    solve(0, 0, Dir.RIGHT).println()

    var best = -1
    for (c in 0 until m) {
        best = max(best, solve(0, c, Dir.DOWN))
        best = max(best, solve(n - 1, c, Dir.UP))
    }
    for (r in 0 until n) {
        best = max(best, solve(n, 0, Dir.RIGHT))
        best = max(best, solve(n, m - 1, Dir.LEFT))
    }
    println(best)
}
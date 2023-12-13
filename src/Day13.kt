import kotlin.math.min

fun main() {
    data class Pattern(val lines: List<CharArray>) {
        val n = lines.size
        val m = lines[0].size
        val rows = LongArray(n)
        val cols = LongArray(m)
        var lastMatchRow = -1
        var lastMatchCol = -1

        init {
            recompute()
        }

        fun recompute() {
            for (r in 0 until n) rows[r] = 0L
            for (c in 0 until m) cols[c] = 0L

            for (r in 0 until n) {
                for (c in 0 until m) {
                    rows[r] *= 2L
                    if (lines[r][c] == '#')
                        rows[r]++
                }
            }
            for (c in 0 until m) {
                for (r in 0 until n) {
                    cols[c] *= 2L
                    if (lines[r][c] == '#')
                        cols[c]++
                }
            }
        }
    }

    fun parsePatterns(input: List<String>): List<Pattern> {
        val curr = mutableListOf<CharArray>()
        val res = mutableListOf<Pattern>()
        for (line in input) {
            if (line.isEmpty()) {
                res.add(Pattern(curr.toMutableList()))
                curr.clear()
            } else {
                curr.add(line.toCharArray())
            }
        }
        if (curr.isNotEmpty()) {
            res.add(Pattern(curr))
        }
        return res
    }

    fun allMatches(i: Int, arr: LongArray): Boolean {
        val maxOffset = min(i + 1, arr.size - i - 1)
        for (o in 0 until maxOffset) {
            check(i - o >= 0)
            check(i + o + 1 < arr.size)
            if (arr[i - o] != arr[i + o + 1])
                return false

        }
        return true
    }

    fun processPattern(pattern: Pattern): Long {
        var res = 0L
        for (c in 0 until pattern.m - 1) {
            if (allMatches(c, pattern.cols) && pattern.lastMatchCol != c) {
                if (pattern.lastMatchCol == -1)
                    pattern.lastMatchCol = c
                res += (c + 1)
            }
        }
        for (r in 0 until pattern.n - 1) {
            if (allMatches(r, pattern.rows) && pattern.lastMatchRow != r) {
                if (pattern.lastMatchRow == -1)
                    pattern.lastMatchRow = r
                res += (r + 1) * 100
            }
        }
//        println(res)
        return res
    }

    fun part1(patterns: List<Pattern>): Long {
        var res = 0L
        for (pattern in patterns) {
            res += processPattern(pattern)
        }
        return res
    }

    fun flip(c: Char) = when (c) {
        '.' -> '#'
        '#' -> '.'
        else -> error("invalid $c")
    }

    fun part2(patterns: List<Pattern>): Long {
        var res = 0L
        l@ for (pattern in patterns) {
            for (r in 0 until pattern.n) {
                for (c in 0 until pattern.m) {
                    pattern.lines[r][c] = flip(pattern.lines[r][c])
                    pattern.recompute()
                    val x = processPattern(pattern)
                    if (x != 0L) {
                        res += x
//                        println(x)
                        continue@l
                    }
                    pattern.lines[r][c] = flip(pattern.lines[r][c])
                }
            }
        }
        return res
    }

    val input = readInput("Day13")
    val patterns = parsePatterns(input)
    part1(patterns).println()
    part2(patterns).println()
}
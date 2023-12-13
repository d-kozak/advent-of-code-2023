fun main() {
    fun parseLine(line: String): Pair<String, List<Int>> {
        val (left, right) = line.split(' ')
        return left to right.split(',').map { it.toInt() }
    }

    fun solve(str: String, sizes: List<Int>): Long {
        val n = str.length
        val m = sizes.size
        val dp = Array(n) { Array(m + 1) { LongArray(n) { -1 } } }

        fun go(i: Int, j: Int, leftInvalid: Int): Long {
            if (i == n) return if (j == m || (j == m - 1 && leftInvalid == sizes[j])) 1 else 0
            if (dp[i][j][leftInvalid] != -1L) return dp[i][j][leftInvalid]
            val res = when (str[i]) {
                '.' -> {
                    if (leftInvalid != 0) {
                        if (j == m || leftInvalid != sizes[j]) 0
                        else go(i + 1, j + 1, 0)
                    } else go(i + 1, j, 0)
                }

                '#' -> {
                    if (j >= m || leftInvalid + 1 > sizes[j]) 0
                    else go(i + 1, j, leftInvalid + 1)
                }

                '?' -> {
                    val res = if (leftInvalid != 0) {
                        if (j == m || leftInvalid != sizes[j]) 0
                        else go(i + 1, j + 1, 0)
                    } else go(i + 1, j, 0)

                    res + if (j >= m || leftInvalid + 1 > sizes[j]) 0
                    else go(i + 1, j, leftInvalid + 1)
                }

                else -> error("invalid")
            }
            dp[i][j][leftInvalid] = res
            return res
        }
        return go(0, 0, 0)
    }

    fun maybeExpand(str: String, sizes: List<Int>): Pair<String, List<Int>> {
        val newStr = StringBuilder()
        val newList = mutableListOf<Int>()
        repeat(5) {
            newStr.append(str)
            newList.addAll(sizes)
            if (it != 4) {
                newStr.append('?')
            }
        }
        return newStr.toString() to newList
    }

    fun processLines(lines: List<Pair<String, List<Int>>>, partTwo: Boolean = false): Long {
        var res = 0L
        for ((str, sizes) in lines) {
            val (str, sizes) = if (partTwo) maybeExpand(str, sizes) else str to sizes
            val solve = solve(str, sizes)
            println(solve)
            res += solve
        }
        return res
    }

    val input = readInput("Day12").map { parseLine(it) }
    processLines(input).println()
    processLines(input, partTwo = true).println()
}
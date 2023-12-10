fun main() {
    val input = readInput("Day10").map { it.toCharArray() }.toTypedArray()
    val n = input.size
    val m = input[0].size

    fun inside(point: Pair<Int, Int>): Boolean {
        val (x, y) = point
        return x in 0 until n && y in 0 until m && input[x][y] != '.'
    }

    fun findStart(input: Array<CharArray>): Pair<Int, Int> {
        for (i in input.indices) {
            for (j in input[0].indices) {
                if (input[i][j] == 'S') return i to j
            }
        }
        error("no start pos")
    }

    val closedFromAbove = setOf('F', '7', '-')
    val closedFromDown = setOf('L', 'J', '-')
    val closedFromLeft = setOf('L', 'F', '|')
    val closedFromRight = setOf('J', '7', '|')

    fun findCycle(input: Array<CharArray>, start: Pair<Int, Int>): Set<Pair<Int, Int>>? {
        val seen = mutableSetOf<Pair<Int, Int>>()
        val worklist = ArrayDeque<Triple<Pair<Int, Int>, Pair<Int, Int>, Int>>()
        worklist.add(Triple(start, -1 to -1, 0))



        while (worklist.isNotEmpty()) {
            val (curr, prev, d) = worklist.removeFirst()
            seen.add(curr)
            if (curr == start && d != 0) {
                check(seen.size % 2 == 0) { d }
                return seen
            }
            // L J F 7 | -
            val (x, y) = curr
            when (input[x][y]) {
                'F' -> {
                    var next = x + 1 to y
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromAbove) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                    next = x to y + 1
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromLeft) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                }

                '7' -> {
                    var next = x + 1 to y
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromAbove) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                    next = x to y - 1
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromRight) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                }

                'L' -> {
                    var next = x - 1 to y
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromDown) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                    next = x to y + 1
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromLeft) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                }

                'J' -> {
                    var next = x - 1 to y
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromDown) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                    next = x to y - 1
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromRight) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                }

                '-' -> {
                    var next = x to y + 1
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromLeft) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                    next = x to y - 1
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromRight) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                }

                '|' -> {
                    var next = x + 1 to y
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromAbove) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                    next = x - 1 to y
                    if (inside(next) && next != prev && input[next.first][next.second] !in closedFromDown) {
                        worklist.add(Triple(next, curr, d + 1))
                    }
                }
            }
        }
        return null
    }

    val dirs = listOf('-', '|', '7', 'F', 'L', 'J')
    val start = findStart(input)
    var cycle = null as Set<Pair<Int, Int>>?
    for (opt in dirs) {
        input[start.first][start.second] = opt
        cycle = findCycle(input, start)
        if (cycle != null) {
            break
        }
    }
    check(cycle != null) { "No path found" }
    (cycle.size / 2).println()

    for (i in 0 until n) {
        for (j in 0 until m) {
            if (input[i][j] != '.' && i to j !in cycle)
                input[i][j] = '.'
        }
    }
    for (line in input) {
        println(line.joinToString(""))
    }
    println("===")



    fun moveVertical(left: Char, right: Char): Boolean {
        return left in setOf('|', '7', 'J', '.', ' ') && right in setOf('|', 'F', 'L', '.', ' ')
    }

    fun moveHorizontal(top: Char, bottom: Char): Boolean {
        return top in setOf('-', 'J', 'L', '.', ' ') && bottom in setOf('-', 'F', '7', '.', ' ')
    }

    val visitedFields = Array(n) { BooleanArray(m) }
    val visitedBorders = Array(n + 1) { BooleanArray(m + 1) }
    val worklist = ArrayDeque<Pair<Int, Int>>()
    for (col in 0..m) {
        worklist.add(0 to col)
        worklist.add(n to col)
    }
    for (row in 0..n) {
        worklist.add(row to 0)
        worklist.add(row to m)
    }
    while (worklist.isNotEmpty()) {
        val (x, y) = worklist.removeFirst()
        if (visitedBorders[x][y]) continue
        visitedBorders[x][y] = true
        val topLeft = if (x - 1 >= 0 && y - 1 >= 0) input[x - 1][y - 1] else ' '
        val topRight = if (x - 1 >= 0 && y < m) input[x - 1][y] else ' '
        val bottomLeft = if (x < n && y - 1 >= 0) input[x][y - 1] else ' '
        val bottomRight = if (x < n && y < m) input[x][y] else ' '
        if (topLeft == '.') visitedFields[x - 1][y - 1] = true
        if (topRight == '.') visitedFields[x - 1][y] = true
        if (bottomLeft == '.') visitedFields[x][y - 1] = true
        if (bottomRight == '.') visitedFields[x][y] = true

        if (x - 1 >= 0 && moveVertical(topLeft, topRight)) worklist.add(x - 1 to y)
        if (x + 1 <= n && moveVertical(bottomLeft, bottomRight)) worklist.add(x + 1 to y)
        if (y - 1 >= 0 && moveHorizontal(topLeft, bottomLeft)) worklist.add(x to y - 1)
        if (y + 1 <= m && moveHorizontal(topRight, bottomRight)) worklist.add(x to y + 1)
    }


    var res = 0
    for (i in 0 until n) {
        for (j in 0 until m) {
            if (!visitedFields[i][j] && i to j !in cycle) {
                input[i][j] = '1'
                res++
            }
        }
    }
    for (line in input) {
        println(line.joinToString(""))
    }
    println(res)
}
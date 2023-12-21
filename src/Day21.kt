fun main() {
    fun Long.pow() = this * this

    val cells = readInput("Day21")
    val n = cells.size
    val m = cells[0].length
    var sr = -1
    var sc = -1
    for (i in 0 until n) {
        for (j in 0 until m) {
            if (cells[i][j] == 'S') {
                sr = i
                sc = j
            }
        }
    }

    check(n == m)
    check(sr == n / 2)
    check(sc == n / 2)

    fun fill(sr: Int, sc: Int, steps: Int): Long {
        val res = mutableSetOf<Pair<Int, Int>>()
        val seen = mutableSetOf(sr to sc)
        val queue = ArrayDeque<Triple<Int, Int, Int>>()
        queue.add(Triple(sr, sc, steps))
        while (queue.isNotEmpty()) {
            val (r, c, s) = queue.removeFirst()

            if (s % 2 == 0)
                res.add(r to c)
            if (s == 0)
                continue

            for ((nr, nc) in listOf(r - 1 to c, r + 1 to c, r to c - 1, r to c + 1)) {
                if (nr !in 0 until n || nc !in 0 until m || cells[nr][nc] == '#' || nr to nc in seen)
                    continue
                seen.add(nr to nc)
                queue.add(Triple(nr, nc, s - 1))
            }
        }
        return res.size.toLong()
    }

    println(fill(sr, sc, 64))

    val steps = 26_501_365L
    val gridWidth = steps / n - 1

    val odd = (gridWidth / 2 * 2 + 1).pow()
    val even = ((gridWidth + 1) / 2 * 2).pow()

    val oddPoints = fill(sr, sc, n * 2 + 1)
    val evenPoints = fill(sr, sc, n * 2)

    val cornerT = fill(n - 1, sc, n - 1)
    val cornerR = fill(sr, 0, n - 1)
    val cornerB = fill(0, sc, n - 1)
    val cornerL = fill(sr, n - 1, n - 1)


    val smallTR = fill(n - 1, 0, n / 2 - 1)
    val smallTL = fill(n - 1, n - 1, n / 2 - 1)
    val smallBR = fill(0, 0, n / 2 - 1)
    val smallBL = fill(0, n - 1, n / 2 - 1)


    val largeTR = fill(n - 1, 0, n * 3 / 2 - 1)
    val largeTL = fill(n - 1, n - 1, n * 3 / 2 - 1)
    val largeBR = fill(0, 0, n * 3 / 2 - 1)
    val largeBL = fill(0, n - 1, n * 3 / 2 - 1)

    println(
        odd * oddPoints +
                even * evenPoints +
                cornerT + cornerR + cornerB + cornerL +
                (gridWidth + 1) * (smallTR + smallTL + smallBR + smallBL) +
                (gridWidth) * (largeTR + largeTL + largeBR + largeBL)

    )
}
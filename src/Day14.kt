sealed class Direction() {
    abstract val mainLoop: IntProgression
    abstract val secondLoop: IntProgression
    abstract val dx: Int
    abstract val dy: Int

    abstract fun get(x: Int, y: Int, input: List<CharArray>): Char
    abstract fun set(x: Int, y: Int, input: List<CharArray>, value: Char)
}

class North(n: Int, m: Int) : Direction() {
    override val mainLoop = 0 until n
    override val secondLoop = 0 until m
    override val dx = -1
    override val dy = 0
    override fun get(x: Int, y: Int, input: List<CharArray>) = input[x][y]
    override fun set(x: Int, y: Int, input: List<CharArray>, value: Char) {
        input[x][y] = value
    }
}

class South(n: Int, m: Int) : Direction() {
    override val mainLoop = n - 1 downTo 0
    override val secondLoop = 0 until m
    override val dx: Int = +1
    override val dy: Int = 0

    override fun get(x: Int, y: Int, input: List<CharArray>) = input[x][y]
    override fun set(x: Int, y: Int, input: List<CharArray>, value: Char) {
        input[x][y] = value
    }
}

class West(n: Int, m: Int) : Direction() {
    override val mainLoop = 0 until m
    override val secondLoop = 0 until n
    override val dx = -1
    override val dy = 0
    override fun get(x: Int, y: Int, input: List<CharArray>) = input[y][x]
    override fun set(x: Int, y: Int, input: List<CharArray>, value: Char) {
        input[y][x] = value
    }
}

class East(n: Int, m: Int) : Direction() {
    override val mainLoop = m - 1 downTo 0
    override val secondLoop = 0 until n
    override val dx = +1
    override val dy = 0
    override fun get(x: Int, y: Int, input: List<CharArray>) = input[y][x]
    override fun set(x: Int, y: Int, input: List<CharArray>, value: Char) {
        input[y][x] = value
    }
}


fun main() {
    val input = readInput("Day14").map { it.toCharArray() }
    val n = input.size
    val m = input[0].size
    val north = North(n, m)
    val south = South(n, m)
    val west = West(n, m)
    val east = East(n, m)

    fun move(input: List<CharArray>, dir: Direction) {
        for (x in dir.mainLoop) {
            for (y in dir.secondLoop) {
                if (dir.get(x, y, input) == 'O') {
                    var xx = x + dir.dx
                    var yy = y + dir.dy
                    while (xx in dir.mainLoop && yy in dir.secondLoop && dir.get(xx, yy, input) == '.') {
                        dir.set(xx, yy, input, 'O')
                        dir.set(xx - dir.dx, yy - dir.dy, input, '.')
                        xx += dir.dx
                        yy += dir.dy
                    }
                }
            }
        }
    }

    fun dump(input: List<CharArray>) {
        for (line in input) {
            println(line.joinToString(""))
        }
    }

    fun northLoad(input: List<CharArray>): Int {
        var res = 0
        for (r in 0 until n) {
            for (c in 0 until m) {
                if (input[r][c] == 'O') {
                    val score = n - r
                    res += score
                }
            }
        }
        return res
    }

    fun str(input: List<CharArray>): String = input.joinToString("\n") { it.joinToString("") }

    move(input, north)
//    dump(input)
    northLoad(input).println()

    val iters = 1_000_000_000
    val configs = mutableMapOf<String, Int>()
    var it = 1
    var cycleDetected = false
    while (it <= iters) {
        move(input, north)
        move(input, west)
        move(input, south)
        move(input, east)

        val prev = configs.put(str(input), it)
        if (!cycleDetected && prev != null && it != iters) {
            cycleDetected = true
            val cycleLen = it - prev
            val remIterations = iters - it
            val fullCycles = remIterations / cycleLen
            // fast-forward to one step after the last cycle
            it += fullCycles * cycleLen + 1
        } else {
            it++
        }
    }
    val load = northLoad(input)
    println(load)
}
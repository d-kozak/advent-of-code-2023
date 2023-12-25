fun main() {
    data class Line(val sx: Double, val sy: Double, val sz: Double, val dx: Double, val dy: Double, val dz: Double) {
        val a = dy
        val b = -dx
        val c = dy * sx - dx * sy
        fun coord() = listOf(a, b, c)
    }

    fun parseLine(input: String): Line {
        val (left, right) = input.split(" @ ")
        val (x, y, z) = left.split(", ").map { it.trim().toDouble() }
        val (dx, dy, dz) = right.split(", ").map { it.trim().toDouble() }
        return Line(x, y, z, dx, dy, dz)
    }

    val lo = 200000000000000.0
    val hi = 400000000000000.0

    fun lineIntercept(left: Line, right: Line): Boolean {
        val (a1, b1, c1) = left.coord()
        val (a2, b2, c2) = right.coord()
        val d = a1 * b2 - a2 * b1
        if (d == 0.0)
            return false
        val x = (c1 * b2 - c2 * b1) / d
        val y = (c2 * a1 - c1 * a2) / d
        if (x in lo..hi && y in lo..hi) {
            if (listOf(left, right).all { (x - it.sx) * it.dx >= 0 && (y - it.sy) * it.dy >= 0 })
                return true
        }
        return false
    }

    val lines = readInput("Day24").map { parseLine(it) }
    val n = lines.size
    var res = 0
    for (i in 0 until n) {
        for (j in i + 1 until n) {
            if (lineIntercept(lines[i], lines[j]))
                res++
        }
    }
    println(res)
}
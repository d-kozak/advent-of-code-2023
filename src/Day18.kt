fun main() {
    val part2 = true

    data class Instruction(val dir: Dir, val len: Int, val color: String)

    fun String.toDir() = when (this[0]) {
        'R' -> Dir.RIGHT
        'L' -> Dir.LEFT
        'D' -> Dir.DOWN
        'U' -> Dir.UP
        else -> error("bad char ${this[0]}")
    }

    fun parseInstruction(line: String): Instruction {
        val (dir, len, color) = line.split(' ')
        if (part2) {
            var len = 0
            for (i in 2..6) {
                val x = when {
                    color[i] in '0'..'9' -> color[i] - '0'
                    else -> color[i] - 'a' + 10
                }
                len *= 16
                len += x
            }
            val dir = when (color[7]) {
                '0' -> Dir.RIGHT
                '1' -> Dir.DOWN
                '2' -> Dir.LEFT
                '3' -> Dir.UP
                else -> error("${color[7]}")
            }
            return Instruction(dir, len, color)
        }
        return Instruction(dir.toDir(), len.toInt(), color)
    }

    val instructions = readInput("Day18").map { parseInstruction(it) }
    var r = 0L
    var c = 0L
    val points = mutableListOf<Pair<Long, Long>>()
    var b = 0
    for ((dir, len) in instructions) {
        points.add(r to c)
        b += len
        r += dir.dr * len
        c += dir.dc * len
    }
    val n = points.size
    var A = 0L
    var res2 = 0L
    for (i in 0 until n) {
        println(points[i])
        val j = (i + 1) % n
        val x1 = points[i].second
        val x2 = points[j].second
        val y1 = points[i].first
        val y2 = points[j].first
        A += (y1 + y2) * (x1 - x2)
    }
    A /= 2
    println(A)
    val i = A - b / 2 + 1
    println(i + b)


//    var r = 0
//    var c = 0
//    val cells = mutableMapOf<Pair<Int, Int>, Char>()
//    for ((dir, len) in instructions) {
//        repeat(len) {
//            val nr = r + dir.dr
//            val nc = c + dir.dc
//            cells[nr to nc] = '#'
//            r = nr
//            c = nc
//        }
//    }
//
//    var left = 0
//    var right = 0
//    var up = 0
//    var down = 0
//    for ((r, c) in cells.keys) {
//        left = min(left, c)
//        right = max(right, c)
//        up = min(up, r)
//        down = max(down, r)
//    }
////    for (r in up..down) {
////        for (c in left..right) {
////            print(if (r to c in cells) '#' else '.')
////        }
////        println()
////    }
//
//    val total = (down - up + 1L) * (right - left + 1L)
//    val worklist = ArrayDeque<Pair<Int, Int>>()
//    val visited = mutableSetOf<Pair<Int, Int>>()
//
//    for (c in left..right) {
//        if (up to c !in cells) worklist.add(up to c)
//        if (down to c !in cells) worklist.add(down to c)
//    }
//    for (r in up..down) {
//        if (r to left !in cells) worklist.add(r to left)
//        if (r to right !in cells) worklist.add(r to right)
//    }
//    var cnt = 0
//    while (worklist.isNotEmpty()) {
//        cnt++
//        val curr = worklist.removeFirst()
//        check(curr !in cells)
//        if (!visited.add(curr)) continue
//        if (cnt % 1_000_000 == 0) {
//            val covered = visited.size / total.toDouble()
//            println("$cnt: ${covered * 100}%")
//        }
//        for ((dr, dc) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) {
//            val nr = curr.first + dr
//            val nc = curr.second + dc
//            if (nr in up..down && nc in left..right && nr to nc !in cells) {
//                worklist.add(nr to nc)
//            }
//        }
//    }
//
////    println()
////    for (r in up..down) {
////        for (c in left..right) {
////            val c = when {
////                r to c in cells -> '#'
////                r to c in visited -> 'v'
////                else -> '.'
////            }
////            print(c)
////        }
////        println()
////    }
//
//    println(total - visited.size)

}
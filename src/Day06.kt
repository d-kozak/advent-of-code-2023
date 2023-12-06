fun main() {
    fun parseNums(line: String): List<Int> {
        val j = line.indexOf(':')
        return line.substring(j + 1).split(' ').mapNotNull { it.toIntOrNull() }
    }

    fun parseInput(input: List<String>): List<List<Int>> {
        return input.map { parseNums(it) }
    }

    fun part1(time: List<Int>, dist: List<Int>): Int {
        val n = time.size
        var res = 1
        for (race in 0 until n) {
            var cnt = 0
            for (t in 0..time[race]) {
                val speed = t
                val rem = time[race] - t
                val maxDist = rem * speed
                if (maxDist > dist[race]) cnt++
            }
            res *= cnt
        }
        return res
    }

    fun parseNum(line: String): Long {
        val j = line.indexOf(':')
        return line.substring(j + 1).replace(" ", "").toLong()
    }

    fun part2(input: List<String>): Long {
        val time = parseNum(input[0])
        val dist = parseNum(input[1])
        var res = 0L
        for (t in 0..time) {
            val speed = t
            val rem = time - t
            val maxDist = speed * rem
            if (maxDist > dist) res++
        }
        return res
    }

    val input = readInput("Day06")
    val (time, dist) = parseInput(input)
    part1(time, dist).println()
    part2(input).println()
}
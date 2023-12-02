import kotlin.math.max

fun main() {
    data class Grab(val r: Int, val g: Int, val b: Int)

    fun parseGrab(input: String): Grab {
        var r = 0
        var g = 0
        var b = 0

        for (part in input.split(',')) {
            var i = 0
            while (part[i].isWhitespace()) i++
            val start = i
            while (part[i] in '0'..'9') i++
            val x = part.substring(start, i).toInt()
            when (val w = part.substring(i + 1)) {
                "red" -> r = x
                "green" -> g = x
                "blue" -> b = x
                else -> error(w)
            }
        }
        return Grab(r, g, b)
    }

    fun parseGame(input: String): List<Grab> {
        val i = input.indexOf(':')
        val input = input.substring(i + 1)
        return input.split(';').map { parseGrab(it) }
    }

    fun part1(input: List<String>): Int {
        val games = input.map { parseGame(it) }
        val redLim = 12
        val greenLim = 13
        val blueLim = 14
        var res = 0
        for ((i, game) in games.withIndex()) {
            if (game.all { it.r <= redLim && it.g <= greenLim && it.b <= blueLim })
                res += i + 1
        }
        return res
    }

    fun part2(input: List<String>): Int {
        val games = input.map { parseGame(it) }
        var res = 0
        for (game in games) {
            var mr = game[0].r
            var mg = game[0].g
            var mb = game[0].b
            for (i in 1 until game.size) {
                mr = max(mr, game[i].r)
                mg = max(mg, game[i].g)
                mb = max(mb, game[i].b)
            }
            val power = mr * mg * mb
            res += power
        }
        return res
    }

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
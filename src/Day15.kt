fun main() {
    fun hash(input: String): Int {
        var res = 0
        for (c in input) {
            res += c.code
            res *= 17
            res %= 256
        }
        check(res <= 255) { res }
        return res
    }

    fun part1(input: List<String>): Int {
        var res = 0
        for (cmd in input) {
            var x = hash(cmd)
            res += x
        }
        return res
    }

    fun part2(input: List<String>): Int {
        val boxes = Array(256) { mutableMapOf<String, Int>() }
        for (cmd in input) {
            var i = 0
            while (cmd[i].isLetter()) i++
            val label = cmd.substring(0, i)
            val box = boxes[hash(label)]
            when (cmd[i]) {
                '=' -> {
                    val focus = cmd.substring(i + 1).toInt()
                    box[label] = focus
                }

                '-' -> {
                    box.remove(label)
                }

                else -> error("bad symbol ${cmd[i]}")
            }
            println("after $cmd")
            for ((i, box) in boxes.withIndex()) {
                if (box.isNotEmpty()) {
                    println("$i : $box")
                }
            }
        }
        var res = 0
        for (i in boxes.indices) {
            for ((j, entry) in boxes[i].entries.withIndex()) {
                val x = (i + 1) * (j + 1) * entry.value
                res += x
            }
        }
        return res
    }

    val input = readInput("Day15").joinToString("").split(",")
    part1(input).println()
    part2(input).println()
}
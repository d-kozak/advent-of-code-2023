val digits = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

fun main() {
    fun extractNum(str: String, part2: Boolean = false): Int {
        var fst = -1
        var last = -1
        for (i in str.indices) {
            val c = str[i]
            if (c in '0'..'9') {
                if (fst == -1) fst = c - '0'
                last = c - '0'
            } else if (part2) {
                l@ for ((x, digit) in digits.withIndex()) {
                    for (j in digit.indices) {
                        if (i + j >= str.length || str[i + j] != digit[j]) continue@l
                    }
                    if (fst == -1) fst = x + 1
                    last = x + 1
                }
            }
        }
        return fst * 10 + last
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { extractNum(it) }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { extractNum(it, true) }
    }

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

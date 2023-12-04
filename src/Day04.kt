fun main() {

    fun extractNumbers(str: String) =
        str.split(' ').mapNotNull { it.toIntOrNull() }

    fun processCard(line: String): Int {
        val i = line.indexOf(':')
        val line = line.substring(i + 1)
        val split = line.split('|')
        val winning = extractNumbers(split[0]).toSet()
        val myNums = extractNumbers(split[1])
        var res = 0
        for (x in myNums) {
            if (x in winning) {
                if (res == 0) res = 1
                else res *= 2
            }
        }
        return res
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { processCard(it) }
    }

    data class Card(val idx: Int, val winning: Set<Int>, val myNums: List<Int>, var score: Int = 0) {
        init {
            for (x in myNums) {
                if (x in winning)
                    score++
            }
        }
    }

    fun parseCard(idx: Int, line: String): Card {
        val i = line.indexOf(':')
        val line = line.substring(i + 1)
        val split = line.split('|')
        val winning = extractNumbers(split[0]).toSet()
        val myNums = extractNumbers(split[1])
        return Card(idx, winning, myNums)
    }

    fun part2(input: List<String>): Int {
        val cards = input.mapIndexed { i, line -> parseCard(i, line) }
        val worklist = ArrayDeque<Card>()
        val processed = mutableListOf<Card>()
        for (card in cards)
            worklist.add(card)
        while (worklist.isNotEmpty()) {
            val curr = worklist.removeFirst()
            processed.add(curr)
            if (curr.score > 0) {
                for (i in curr.idx + 1..curr.idx + curr.score)
                    worklist.add(cards[i])
            }
        }
        return processed.size
    }

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
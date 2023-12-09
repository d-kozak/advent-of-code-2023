fun main() {
    val input = readInput("Day09").map { it.split(' ').map { it.toInt() } }

    fun extrapolate(nums: List<Int>): Pair<Int, Int> {
        val lines = mutableListOf<MutableList<Int>>()
        lines.add(nums.toMutableList())
        while (lines.last().any { it != 0 }) {
            val last = lines.last()
            val next = MutableList(last.size - 1) { 0 }
            for (i in next.indices) {
                next[i] = last[i + 1] - last[i]
            }
            lines.add(next)
        }
//        println(lines)
        lines.last().add(0)
        var prevFirst = 0
        for (i in lines.size - 2 downTo 0) {
            lines[i].add(lines[i].last() + lines[i + 1].last())
            prevFirst = lines[i].first() - prevFirst
        }
//        println(lines)
        return prevFirst to lines[0].last()
    }

    fun solve(input: List<List<Int>>): Pair<Int, Int> {
        return input.fold(0 to 0) { acc, l ->
            val (fst, snd) = extrapolate(l)
            acc.first + fst to acc.second + snd
        }
    }
    solve(input).println()
}
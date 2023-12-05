import kotlin.math.min

fun main() {
    data class Range(val from: Long, val len: Long)

    data class MappingRange(val dst: Long, val src: Long, val len: Long) {
        val srcRange = Range(src, len)
        fun map(x: Long): Long = if (src <= x && x < src + len) dst + x - src else -1

        infix fun intersect(range: Range): Range? =
            if (range.from <= srcRange.from) intersectHelper(range, srcRange)
            else intersectHelper(srcRange, range)


        private fun intersectHelper(left: Range, right: Range): Range? {
            if (left.from + left.len - 1 < right.from) return null
            return Range(
                right.from,
                min(left.from + left.len - 1, right.from + right.len - 1) - right.from + 1
            )
        }
    }

    data class Mapping(val ranges: List<MappingRange>) {
        fun map(x: Long): Long {
            for (range in ranges) {
                val res = range.map(x)
                if (res != -1L) return res
            }
            return x
        }
    }

    fun parseInput(input: List<String>): Pair<List<Long>, List<Mapping>> {
        val seeds = input[0].split(' ').mapNotNull { it.toLongOrNull() }
        val n = input.size
        var i = 2
        val ranges = mutableListOf<MappingRange>()
        val maps = mutableListOf<Mapping>()
        while (i < n) {
            if (input[i].indexOf(':') != -1) {
                if (ranges.isNotEmpty()) {
                    maps.add(Mapping(ranges.toList()))
                    ranges.clear()
                }
            } else if (input[i].isNotEmpty()) {
                val (dst, src, len) = input[i].split(' ').map { it.toLong() }
                ranges.add(MappingRange(dst, src, len))
            }
            i++
        }
        if (ranges.isNotEmpty()) {
            maps.add(Mapping(ranges.toList()))
        }
        return seeds to maps
    }

    fun part1(seeds: List<Long>, maps: List<Mapping>): Long {
        var source = LongArray(seeds.size)
        var target = LongArray(seeds.size)
        for (i in source.indices)
            source[i] = seeds[i]
        for (map in maps) {
            for (i in source.indices)
                target[i] = map.map(source[i])

            val tmp = source
            source = target
            target = tmp
        }

        return source.min()
    }

    fun toRanges(seeds: List<Long>): List<Range> {
        val n = seeds.size
        val res = mutableListOf<Range>()
        for (i in 0 until n step 2) {
            res.add(Range(seeds[i], seeds[i + 1]))
        }
        return res
    }

    fun part2(seeds: List<Long>, maps: List<Mapping>): Long {
        val worklist = ArrayDeque<Range>()
        val next = mutableListOf<Range>()
        worklist.addAll(toRanges(seeds))
        for (map in maps) {
            l@ while (worklist.isNotEmpty()) {
                val curr = worklist.removeFirst()
                for (range in map.ranges) {
                    val x = range intersect curr
                    if (x != null) {
                        if (curr.from < x.from) {
                            worklist.add(Range(curr.from, x.from - curr.from))
                        }
                        val xRight = x.from + x.len - 1
                        val currRight = curr.from + curr.len - 1
                        if (xRight < currRight) {
                            worklist.add(Range(xRight + 1, currRight - xRight))
                        }
                        val offset = x.from - range.src
                        next.add(Range(range.dst + offset, x.len))
                        continue@l
                    }
                }
                next.add(curr)
            }
            worklist.addAll(next)
            next.clear()
        }
        return worklist.minOf { it.from }
    }

    val input = readInput("Day05")
    val (seeds, maps) = parseInput(input)
    part1(seeds, maps).println()
    part2(seeds, maps).println()
}
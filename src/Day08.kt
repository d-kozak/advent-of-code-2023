fun main() {
    data class Node(val id: String) {
        val nei = mutableListOf<Node>()

        override fun toString(): String = "($id => ${nei.joinToString(",") { it.id }})"
    }

    val input = readInput("Day08")
    val dirs = input[0]
    val nodes = mutableMapOf<String, Node>()
    for (i in 2 until input.size) {
        val (left, right) = input[i].split('=')
        val id = left.substring(0, left.length - 1)
        val node = nodes.getOrPut(id) { Node(id) }
        for (nei in right.substring(2, right.length - 1).split(',')) {
            val neiId = nei.replace(" ", "")
            node.nei.add(nodes.getOrPut(neiId) { Node(neiId) })
        }
    }

    fun dirIndex(c: Char) = when (c) {
        'L' -> 0
        'R' -> 1
        else -> error("Invalid dir: $c")
    }

    fun part1(dirs: String, nodes: MutableMap<String, Node>): Int {
        var curr = nodes.getValue("AAA")
        var i = 0
        var steps = 0
        while (curr.id != "ZZZ") {
            curr = curr.nei[dirIndex(dirs[i])]
            i = (i + 1) % dirs.length
            steps++
        }
        return steps
    }


    fun part2_brute_force(dirs: String, nodes: MutableMap<String, Node>): Long {
        val curr = mutableListOf<Node>()
        for (node in nodes.values)
            if (node.id.last() == 'A')
                curr.add(node)
        var steps = 0L
        var dirI = 0
        while (true) {
            if (steps % 1_000_000L == 0L)
                println("$steps " + curr.map { it.id })
            steps++
            var cnt = 0
            for (i in curr.indices) {
                curr[i] = curr[i].nei[dirIndex(dirs[dirI])]
                if (curr[i].id.last() == 'Z')
                    cnt++

            }
            if (cnt == curr.size)
                break
            dirI = (dirI + 1) % dirs.length
        }
        return steps
    }

    fun findDists(start: Node, dirs: String): List<Long> {
        val dist = mutableMapOf<Node, MutableList<Long>>()
        val seen = mutableSetOf<Pair<Node, Int>>()
        var curr = start
        var d = 0L
        var i = 0
        while (true) {
            dist.getOrPut(curr) { mutableListOf() }.add(d)
            curr = curr.nei[dirIndex(dirs[i])]
            d++
            i = (i + 1) % dirs.length
            if (!seen.add(curr to i)) break
        }
        return dist.filter { it.key.id.last() == 'Z' }.flatMap { it.value }
    }

    fun lcm(nums: MutableList<Long>): Long {
        var res = 1L
        var div = 2L
        while (true) {
            var cnt = 0
            var divApplied = false
            for (i in nums.indices) {
                when {
                    nums[i] == 1L -> cnt++
                    nums[i] % div == 0L -> {
                        divApplied = true
                        nums[i] /= div
                    }
                }
            }
            if (divApplied)
                res *= div
            div++
            if (cnt == nums.size) break
        }
        return res
    }

    fun part2(dirs: String, nodes: MutableMap<String, Node>): Long {
        val starts = nodes.values.filter { it.id.last() == 'A' }
        val dists = starts.map { findDists(it, dirs) }
        check(dists.all { it.size == 1 }) { dists } // this simplifies things
        return lcm(dists.map { it[0] }.toMutableList())
    }
    part1(dirs, nodes).println()
//    part2_brute_force(dirs, nodes).println()
    part2(dirs, nodes).println()
}


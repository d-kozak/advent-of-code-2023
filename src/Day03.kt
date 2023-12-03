fun main() {

    val nei = listOf(-1, 0, +1)

    fun part1(input: List<String>): Int {
        val n = input.size
        val m = input[0].length
        var res = 0
        for ((r, line) in input.withIndex()) {
            var numStart = -1
            var isPart = false
            for ((c, char) in line.withIndex()) {
                if (char.isDigit()) {
                    if (numStart == -1)
                        numStart = c
                    if (!isPart) {
                        for (dr in nei) {
                            for (dc in nei) {
                                if (r + dr in 0 until n && c + dc in 0 until m) {
                                    val neiChar = input[r + dr][c + dc]
                                    if (neiChar != '.' && !neiChar.isDigit())
                                        isPart = true
                                }
                            }
                        }
                    }
                } else if (numStart != -1) {
                    if (isPart) {
                        val x = line.substring(numStart, c).toInt()
                        res += x
                        isPart = false
                    }
                    numStart = -1
                }
            }
            if (numStart != -1 && isPart) {
                val x = line.substring(numStart).toInt()
                res += x
            }
        }
        return res
    }

    fun extractNum(sc: Int, line: String, seen: BooleanArray): Int {
        var left = sc
        while (left - 1 >= 0 && line[left - 1].isDigit()) {
            left--
            seen[left] = true
        }
        var right = sc
        while (right + 1 < line.length && line[right + 1].isDigit()) {
            right++
            seen[right] = true
        }
        return line.substring(left, right + 1).toInt()
    }

    fun part2(input: List<String>): Int {
        val n = input.size
        val m = input[0].length
        val seen = Array(n) { BooleanArray(m) }
        var res = 0
        for ((r, line) in input.withIndex()) {
            l@ for ((c, char) in line.withIndex()) {
                if (char == '*') {
                    var x = -1
                    var y = -1
                    for (dr in nei) {
                        for (dc in nei) {
                            val nr = r + dr
                            val nc = c + dc
                            if (nr in 0 until n && nc in 0 until m) {
                                if (!seen[nr][nc] && input[nr][nc].isDigit()) {
                                    seen[nr][nc] = true
                                    if (x == -1 || y == -1) {
                                        val num = extractNum(nc, input[nr], seen[nr])
                                        if (x == -1) x = num
                                        else y = num
                                    } else {
                                        for (dr in nei) {
                                            for (i in 0 until m) {
                                                seen[r + dr][i] = false
                                            }
                                        }
                                        continue@l
                                    }
                                }
                            }
                        }
                    }
                    if (x != -1 && y != -1) res += x * y
                    for (dr in nei) {
                        for (i in 0 until m) {
                            seen[r + dr][i] = false
                        }
                    }
                }
            }
        }
        return res
    }

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
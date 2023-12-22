import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.MutableSet
import kotlin.collections.all
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.contains
import kotlin.collections.getValue
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.collections.minus
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.collections.set
import kotlin.collections.sortedBy

data class Coord(val x: Int, val y: Int, val z: Int) {
    fun above(): Coord = Coord(x, y, z + 1)
    fun below(): Coord = Coord(x, y, z - 1)


}

data class Brick(
    val id: Int,
    val xLen: Int,
    val yLen: Int,
    val zLen: Int,
    var x1: Int,
    var y1: Int,
    var z1: Int
) :
    Comparable<Brick> {

    val horizontal: Boolean = zLen == 1


    override fun compareTo(other: Brick): Int = z1.compareTo(other.z1)

    fun cubes(limitZ: Boolean = true): Sequence<Coord> = sequence {
        if (xLen == 1 && yLen == 1 && zLen == 1) {
            yield(Coord(x1, y1, z1))
            return@sequence
        }
        if (xLen > 1) {
            for (x in x1 until x1 + xLen) {
                yield(Coord(x, y1, z1))
            }
        } else if (yLen > 1) {
            for (y in y1 until y1 + yLen) {
                yield(Coord(x1, y, z1))
            }
        } else if (zLen > 1) {
            for (z in z1 until z1 + zLen) {
                if (limitZ && z > z1) break
                yield(Coord(x1, y1, z))
            }
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Brick) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}

var id = 1
fun parseBrick(line: String): Brick {
    val (left, right) = line.split('~')
    val (x1, y1, z1) = left.split(',').map { it.toInt() }
    val (x2, y2, z2) = right.split(',').map { it.toInt() }
    check(x1 <= x2)
    check(y1 <= y2)
    check(z1 <= z2)
    return Brick(id++, x2 - x1 + 1, y2 - y1 + 1, z2 - z1 + 1, x1, y1, z1)
}


fun main() {
    val bricks = readInput("Day22").map { parseBrick(it) }
    val queue = PriorityQueue(bricks)
    val occupied = mutableMapOf<Coord, Brick>()
    for (brick in bricks) {
        for (cube in brick.cubes(limitZ = false)) {
            occupied[cube] = brick
        }
    }
    while (queue.isNotEmpty()) {
        val curr = queue.remove()
        if (curr.z1 == 1) continue
        curr.z1--
        if (curr.cubes().none { it in occupied }) {
            if (curr.horizontal) {
                for (cube in curr.cubes()) {
                    occupied[cube] = curr
                    check(occupied.remove(cube.above()) == curr) { "$curr $cube" }
                }
            } else {
                // yLen >= 2
                occupied[Coord(curr.x1, curr.y1, curr.z1)] = curr
                check(occupied.remove(Coord(curr.x1, curr.y1, curr.z1 + curr.zLen)) == curr) { "$curr" }
            }
            queue.add(curr)
        } else {
            curr.z1++
        }
    }

    val keys = mutableSetOf<Coord>()
    for (brick in bricks) {
        for (cube in brick.cubes(limitZ = false)) {
            keys.add(cube)
            check(occupied[cube] == brick) { "$cube $brick" }
        }
    }
    check(occupied.keys == keys) { occupied.keys - keys }

    val supportedBy = mutableMapOf<Brick, MutableSet<Brick>>()
    val supporting = mutableMapOf<Brick, MutableSet<Brick>>()
    for (brick in bricks) {
        if (brick.horizontal) {
            for (cube in brick.cubes()) {
                var target = occupied[cube.below()]
                if (target != null && target != brick) {
                    supportedBy.computeIfAbsent(brick) { mutableSetOf() }.add(target)
                }
                target = occupied[cube.above()]
                if (target != null && target != brick) {
                    supporting.computeIfAbsent(brick) { mutableSetOf() }.add(target)
                }
            }
        } else {
            val below = Coord(brick.x1, brick.y1, brick.z1 - 1)
            var target = occupied[below]
            if (target != null && target != brick) {
                supportedBy.computeIfAbsent(brick) { mutableSetOf() }.add(target)
            }
            val above = Coord(brick.x1, brick.y1, brick.z1 + brick.zLen)
            target = occupied[above]
            if (target != null && target != brick) {
                supporting.computeIfAbsent(brick) { mutableSetOf() }.add(target)
            }
        }
    }

    var res = 0
    for (brick in bricks) {
        val above = supporting[brick]
        if (above == null || above.all { supportedBy.getValue(it).size > 1 }) {
//            println(brick.id)
//            println("\t" + above?.map { it.id + " -> " + supportedBy.getValue(it).map { it.id } })
            res++
        }
    }
    println(res)

//    val damage = mutableMapOf<Brick, Int>()
//    for (brick in bricks.sortedByDescending { it.z1 }) {
//        val above = supporting[brick]
//        if (above == null) {
//            damage[brick] = 0
//        } else {
//            var res = 0
//            for (x in above) {
//                if (supportedBy.getValue(x).size == 1) {
//                    res += 1 + damage.getValue(x)
//                }
//            }
//            damage[brick] = res
//        }
//    }
//
//    println(damage)
//    println(damage.values.sum())

//    fun go(brick: Brick, fallen: MutableSet<Brick>): Int {
//        val below = supportedBy[brick]
//        if (below == null || fallen.containsAll(below)) {
//            var res = 1
//            val above = supporting[brick]
//            if (above != null) {
//                fallen.add(brick)
//                for (x in above) {
//                    res += go(x, fallen)
//                }
//                fallen.remove(brick)
//            }
//            return res
//        }
//        return 0
//    }

    res = 0
    val fallen = mutableSetOf<Brick>()
    val seen = mutableSetOf<Brick>()
    val worklist = ArrayDeque<Brick>()
    for (brick in bricks.sortedBy { it.z1 }) {
//        val x = go(brick, fallen)
//        if (x > 1) {
//            println("${brick.id} ${x - 1}")
//            res += x - 1
//        }
        fallen.clear()
        seen.clear()
        worklist.add(brick)
        var cnt = 0
        while (worklist.isNotEmpty()) {
            val curr = worklist.removeFirst()
            val below = supportedBy[curr]
            if (curr == brick || below == null || fallen.containsAll(below)) {
                if (!seen.add(curr)) continue
                cnt++
                fallen.add(curr)
                for (above in supporting[curr] ?: continue) {
                    worklist.add(above)
                }
            }
        }
        if (cnt > 1) {
            println("${brick.id} ${cnt - 1}")
            res += cnt - 1
        }
    }
    // 30766 - too low
    println(res)
}
sealed class Module {
    abstract val name: String
    val next = mutableListOf<Module>()
}

data class FlipFlop(override var name: String, var on: Boolean = false) : Module()
data class Conjunction(override val name: String, val inputs: MutableMap<String, Pulse> = mutableMapOf()) : Module()
data object Broadcaster : Module() {
    override val name: String = "broadcaster"
}

data class Untyped(override var name: String) : Module()

enum class Pulse {
    LOW, HIGH
}


fun main() {
    fun parseInput(input: List<String>): Map<String, Module> {
        val modules = mutableMapOf<String, Module>()
        for (line in input) {
            val (left) = line.split(" -> ")
            val module = when {
                left[0] == '%' -> {
                    val name = left.substring(1)
                    FlipFlop(name)
                }

                left[0] == '&' -> {
                    val name = left.substring(1)
                    Conjunction(name)
                }

                left == "broadcaster" -> {
                    Broadcaster
                }

                else -> error("Unknown module type $left")
            }
            modules[module.name] = module
        }
        for (line in input) {
            val (left, right) = line.split(" -> ")
            val name = if (left[0] == '%' || left[0] == '&') left.substring(1) else left
            val from = modules.getValue(name)
            for (nei in right.split(", ")) {
                val to = modules.computeIfAbsent(nei) { Untyped(nei) }
                from.next.add(to)
                if (to is Conjunction) {
                    to.inputs[from.name] = Pulse.LOW
                }
            }
        }
        return modules
    }

    var low = 0
    var high = 0
    val input = readInput("Day20")
    val modules = parseInput(input)

    val feed = modules.values.find { it.next.any { it.name == "rx" } }!!
    val seen = mutableMapOf<String, Int>()
    for (module in modules.values) {
        if (module.next.any { it.name == feed.name }) {
            seen[module.name] = 0
        }
    }
    val cycleLens = mutableMapOf<String, Int>()
    val part2 = true

    fun pushButton(modules: Map<String, Module>, it: Int): Boolean {
        val worklist = ArrayDeque<Triple<Module, Pulse, String>>()
        worklist.add(Triple(Broadcaster, Pulse.LOW, "button"))
        while (worklist.isNotEmpty()) {
            val (module, pulse, from) = worklist.removeFirst()
            println("$from -${pulse}-> ${module.name}")
            if (part2 && module.name == feed.name && pulse == Pulse.HIGH) {
                seen[from] = seen.getValue(from) + 1
                if (from !in cycleLens) {
                    cycleLens[from] = it
                } else {
                    check(it == seen.getValue(from) * cycleLens.getValue(from)) {
                        "$it ${
                            seen.getValue(from) * cycleLens.getValue(from)
                        }}"
                    }
                }
                if (seen.values.all { it >= 1 }) {
                    return true
                }
            }
            when (pulse) {
                Pulse.LOW -> low++
                Pulse.HIGH -> high++
            }
            when (module) {
                Broadcaster -> {
                    for (next in module.next) {
                        worklist.add(Triple(next, pulse, module.name))
                    }
                }

                is Conjunction -> {
                    module.inputs[from] = pulse
                    val nextPulse = if (module.inputs.values.all { it == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH
                    for (next in module.next) {
                        worklist.add(Triple(next, nextPulse, module.name))
                    }
                }

                is FlipFlop -> {
                    if (pulse == Pulse.LOW) {
                        val nextPulse = if (module.on) Pulse.LOW else Pulse.HIGH
                        module.on = !module.on
                        for (next in module.next) {
                            worklist.add(Triple(next, nextPulse, module.name))
                        }
                    }
                }

                is Untyped -> {}
            }
        }
        return false
    }

    fun lcm(nums: Collection<Int>): Long {
        var res = 1L
        for (num in nums) {
            var x = num
            for (d in 2..x) {
                if (x % d == 0) {
                    x /= d
                    res *= d
                    if (x == 1) break
                }
            }
        }
        return res
    }


    if (part2) {
        var it = 0
        while (true) {
            it++
            if (pushButton(modules, it)) {
                println(lcm(cycleLens.values))
                return
            }
            println()
        }
    } else {
        repeat(1000) {
            pushButton(modules, it + 1)
        }
        println("$low $high ${low * high}")
    }
}
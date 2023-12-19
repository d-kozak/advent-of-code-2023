sealed class Value
data class Variable(val name: Char) : Value()
data class Literal(val value: Int) : Value()

sealed class Action
data object Accept : Action()
data object Reject : Action()
data class Move(val target: String) : Action()

sealed class Condition {
    abstract val action: Action
}

data class Gt(val left: Value, val right: Value, override val action: Action) : Condition()
data class Lt(val left: Value, val right: Value, override val action: Action) : Condition()
data class Unconditional(override val action: Action) : Condition()

fun main() {
    data class Part(val vars: Map<Char, Int>)
    data class Workflow(val id: String, val conditions: List<Condition>)

    fun Value.eval(part: Part): Int = when (this) {
        is Variable -> part.vars.getValue(this.name)
        is Literal -> this.value
    }

    fun Condition.eval(part: Part): Boolean = when (this) {
        is Lt -> this.left.eval(part) < this.right.eval(part)
        is Gt -> this.left.eval(part) > this.right.eval(part)
        is Unconditional -> true
    }

    fun parseAction(input: String): Action = when (input) {
        "A" -> Accept
        "R" -> Reject
        else -> Move(input)
    }

    fun parseConditions(input: String): List<Condition> {
        val conditions = mutableListOf<Condition>()
        for (part in input.split(',')) {
            val i = part.indexOf(':')
            if (i != -1) {
                var j = 0
                val left: Value = if (part[j].isLetter()) {
                    Variable(part[j++])
                } else {
                    while (part[j].isDigit()) j++
                    Literal(part.substring(0, j).toInt())
                }
                val lt = part[j++] == '<'
                var k = j
                val right: Value = if (part[k].isLetter()) {
                    Variable(part[k++])
                } else {
                    while (part[k].isDigit()) k++
                    Literal(part.substring(j, k).toInt())
                }
                val action = parseAction(part.substring(i + 1))
                check(left is Variable)
                check(right is Literal)
                conditions.add(if (lt) Lt(left, right, action) else Gt(left, right, action))
            } else {
                val action = parseAction(part)
                conditions.add(Unconditional(action))
            }
        }
        return conditions
    }

    fun parseInput(input: List<String>): Pair<Map<String, Workflow>, List<Part>> {
        var parsingWorkflows = true
        val workflows = mutableMapOf<String, Workflow>()
        val parts = mutableListOf<Part>()
        for (line in input) {
            if (line.isEmpty()) {
                parsingWorkflows = false
                continue
            }
            if (parsingWorkflows) {
                val i = line.indexOf('{')
                val id = line.substring(0, i)
                val conditions = parseConditions(line.substring(i + 1, line.length - 1))
                val workflow = Workflow(id, conditions)
                workflows[id] = workflow
            } else {
                val vars = mutableMapOf<Char, Int>()
                for (part in line.substring(1, line.length - 1).split(',')) {
                    val name = part[0]
                    val value = part.substring(2).toInt()
                    vars[name] = value
                }
                parts.add(Part(vars))
            }
        }
        return workflows to parts
    }

    val input = readInput("Day19")
    val (workflows, parts) = parseInput(input)
//    for (workflow in workflows.values) {
//        println(workflow.id)
//        for (cond in workflow.conditions) {
//            println("\t$cond")
//        }
//    }
//    println()
//    for (part in parts) {
//        println(part)
//    }

    var score = 0
    partLoop@ for (part in parts) {
        var curr = workflows.getValue("in")
        whileLoop@ while (true) {
            for (cond in curr.conditions) {
                if (cond.eval(part)) {
                    when (val action = cond.action) {
                        is Accept -> {
                            score += part.vars.values.sum()
                            continue@partLoop
                        }

                        is Reject -> {
                            continue@partLoop
                        }

                        is Move -> {
                            curr = workflows.getValue(action.target)
                            continue@whileLoop
                        }
                    }
                }
            }
        }
    }

    println(score)
    data class Interval(val from: Int, val to: Int) {
        val len = to - from + 1

        fun isEmpty(): Boolean = from > to

        fun isNotEmpty(): Boolean = !isEmpty()
    }

    val startInterval = Interval(1, 4000)

    data class State(
        val intervals: Map<Char, Interval> = mutableMapOf(
            'x' to startInterval,
            'm' to startInterval,
            'a' to startInterval,
            's' to startInterval
        )
    )

    var res = 0L
    val worklist = ArrayDeque<Pair<State, Workflow>>()
    worklist.add(State() to workflows.getValue("in"))

    fun processAction(state: State, action: Action) {
        when (action) {
            Accept -> {
                res += state.intervals.values.fold(1L) { acc, curr -> acc * curr.len }
                check(res >= 0)
            }

            Reject -> return
            is Move -> worklist.add(state to workflows.getValue(action.target))
        }
    }

    loop@ while (worklist.isNotEmpty()) {
        val (state, workflow) = worklist.removeFirst()
        val bounds = state.intervals.toMutableMap()
        for (cond in workflow.conditions) {
            when (cond) {
                is Gt -> {
                    val variable = cond.left as Variable
                    val value = cond.right as Literal
                    val nextMap = bounds.toMutableMap()
                    val prev = nextMap.getValue(variable.name)
                    val nextInterval = Interval(value.value + 1, prev.to)
                    if (nextInterval.isNotEmpty()) {
                        nextMap[variable.name] = nextInterval
                        processAction(State(nextMap.toMap()), cond.action)
                    }
                    val continueInterval = Interval(prev.from, value.value)
                    if (continueInterval.isEmpty()) {
                        continue@loop
                    }
                    bounds[variable.name] = continueInterval
                }

                is Lt -> {
                    val variable = cond.left as Variable
                    val value = cond.right as Literal
                    val nextMap = bounds.toMutableMap()
                    val prev = nextMap.getValue(variable.name)
                    val nextInterval = Interval(prev.from, value.value - 1)
                    if (nextInterval.isNotEmpty()) {
                        nextMap[variable.name] = nextInterval
                        processAction(State(nextMap.toMap()), cond.action)
                    }
                    val continueInterval = Interval(value.value, prev.to)
                    if (continueInterval.isEmpty()) {
                        continue@loop
                    }
                    bounds[variable.name] = continueInterval
                }

                is Unconditional -> {
                    processAction(State(bounds), cond.action)
                }
            }
        }
    }
    println(res)
}
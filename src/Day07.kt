enum class Kind {
    HIGH,
    ONE_PAIR,
    TWO_PAIR,
    THREE,
    FULL_HOUSE,
    FOUR,
    FIVE,
}

fun main() {
    var part2 = false
    fun determineKind(cards: String): Kind {
        val freq = mutableMapOf<Char, Int>()
        for (c in cards)
            freq[c] = freq.getOrDefault(c, 0) + 1
        var jokers = 0
        if (part2 && freq.size > 1) {
            jokers = cards.count { it == 'J' }
            freq.remove('J')
        }
        if (freq.values.any { it + jokers == 5 }) return Kind.FIVE
        if (freq.values.any { it + jokers == 4 }) return Kind.FOUR
        for ((k1, cnt1) in freq) {
            for ((k2, cnt2) in freq) if (k1 != k2) {
                if (cnt1 + cnt2 + jokers == 5) return Kind.FULL_HOUSE
            }
        }
        if (freq.values.any { it + jokers == 3 }) return Kind.THREE
        for ((k1, cnt1) in freq) {
            for ((k2, cnt2) in freq) if (k1 != k2) {
                if (cnt1 + cnt2 + jokers == 4) return Kind.TWO_PAIR
            }
        }
        if (freq.values.any { it + jokers == 2 }) return Kind.ONE_PAIR
        return Kind.HIGH
//        return when {
//            5 in freq.values -> Kind.FIVE
//            4 in freq.values -> Kind.FOUR
//            2 in freq.values && 3 in freq.values -> Kind.FULL_HOUSE
//            3 in freq.values -> Kind.THREE
//            freq.values.toList().count { it == 2 } == 2 -> Kind.TWO_PAIR
//            2 in freq.values -> Kind.ONE_PAIR
//            else -> {
//                check(freq.size == 5)
//                Kind.HIGH
//            }
//        }
    }

    fun ord(x: Char): Int = when (x) {
        'T' -> 10
        'J' -> if (part2) 1 else 11
        'Q' -> 12
        'K' -> 13
        'A' -> 14
        else -> {
            check(x.isDigit())
            x - '0'
        }
    }


    fun compareCards(left: String, right: String): Int {
        // A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or 2.
        check(left.length == right.length)
        val n = left.length
        for (i in 0 until n) {
            if (left[i] != right[i]) {
                return ord(left[i]).compareTo(ord(right[i]))
            }
        }
        return 0
    }

    data class Hand(val cards: String, val bid: Int, var kind: Kind = determineKind(cards)) : Comparable<Hand> {

        override fun compareTo(other: Hand): Int {
            val cmp = kind.compareTo(other.kind)
            return if (cmp != 0) cmp else compareCards(this.cards, other.cards)
        }
    }

    fun parseHand(line: String): Hand {
        val split = line.split(' ')
        return Hand(split[0], split[1].toInt())
    }

    fun countScore(hands: List<Hand>): Int {
        var res = 0
        for (i in hands.indices)
            res += (i + 1) * hands[i].bid
        return res
    }

    val input = readInput("Day07")
    var hands = input.map { parseHand(it) }.sorted()
    countScore(hands).println()
    part2 = true
    for (hand in hands)
        hand.kind = determineKind(hand.cards)
    hands = hands.sorted()
    println(hands)
    countScore(hands).println()
}
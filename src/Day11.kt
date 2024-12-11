fun main() {
	fun blink(n: Long): List<Long> {
		val ns = n.toString()
		return if (n == 0L)
			listOf(1)
		else if (ns.length % 2 == 0)
			listOf(ns.substring(0, ns.length / 2).toLong(), ns.substring(ns.length / 2).toLong())
		else
			listOf(n * 2024)
	}

	val cache = mutableMapOf<Pair<Long, Int>, Long>()

	fun blinkRecurs(n: Long, level: Int = 0): Long {
		if (level >= 75) return 1
		var count = 0L
		for (x in blink(n)) {
			count += cache.getOrPut(x to 75 - level - 1) { blinkRecurs(x, level + 1) }
		}
		return count
	}

	fun part1(input: List<String>): Int {
		var stones = input[0].split(' ').map { it.toLong() }
		for (i in 0 until 25) {
			val newStones = mutableListOf<Long>()
			for (n in stones) newStones.addAll(blink(n))
			stones = newStones
		}
		return stones.size
	}

	fun part2(input: List<String>): Long {
		val stones = input[0].split(' ').map { it.toLong() }
		return stones.sumOf { blinkRecurs(it) }
	}

	val testInput = readInput("Day11_test")
	check(part1(testInput) == 55312)

	val input = readInput("Day11")
	part1(input).println() // 187738
	part2(input).println() // 223767210249237
}

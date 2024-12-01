import kotlin.math.abs

fun main() {
	fun part1(input: List<String>): Int {
		val a = input.map { it.substringBefore("   ").toInt() }.sorted()
		val b = input.map { it.substringAfter("   ").toInt() }.sorted()
		return a.indices.sumOf { abs(a[it] - b[it]) }
	}

	fun part2(input: List<String>): Int {
		val a = input.map { it.substringBefore("   ").toInt() }.sorted()
		val b = input.map { it.substringAfter("   ").toInt() }.sorted()
		val bCount = mutableMapOf<Int, Int>()
		for (n in b) bCount[n] = (bCount[n] ?: 0) + 1
		return a.sumOf { it * (bCount[it] ?: 0) }
	}

	val testInput = readInput("Day01_test")
	check(part1(testInput) == 11)
	check(part2(testInput) == 31)

	val input = readInput("Day01")
	part1(input).println() // 3569916
	part2(input).println() // 26407426
}

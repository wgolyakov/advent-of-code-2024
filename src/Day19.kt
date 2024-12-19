fun main() {
	fun possibleRecurs(available: List<String>, desired: String, cache: MutableMap<String, Boolean>): Boolean {
		val possible = cache[desired]
		if (possible != null) return possible
		if (desired.isEmpty()) return true
		for (pattern in available) {
			if (desired.startsWith(pattern)) {
				val res = possibleRecurs(available, desired.substring(pattern.length), cache)
				if (res) {
					cache[desired] = true
					return true
				}
			}
		}
		cache[desired] = false
		return false
	}

	fun countPossibleRecurs(available: List<String>, desired: String, cache: MutableMap<String, Long>): Long {
		val differentWays = cache[desired]
		if (differentWays != null) return differentWays
		if (desired.isEmpty()) return 1
		var count = 0L
		for (pattern in available) {
			if (desired.startsWith(pattern)) {
				count += countPossibleRecurs(available, desired.substring(pattern.length), cache)
			}
		}
		cache[desired] = count
		return count
	}

	fun part1(input: List<String>): Int {
		val available = input[0].split(", ")
		val desired = input.drop(2)
		return desired.count { possibleRecurs(available, it, mutableMapOf()) }
	}

	fun part2(input: List<String>): Long {
		val available = input[0].split(", ")
		val desired = input.drop(2)
		return desired.sumOf { countPossibleRecurs(available, it, mutableMapOf()) }
	}

	val testInput = readInput("Day19_test")
	check(part1(testInput) == 6)
	check(part2(testInput) == 16L)

	val input = readInput("Day19")
	part1(input).println() // 365
	part2(input).println() // 730121486795169
}

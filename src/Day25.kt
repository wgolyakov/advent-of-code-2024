fun main() {
	fun parse(input: List<String>): Pair<List<IntArray>, List<IntArray>> {
		val keys = mutableListOf<IntArray>()
		val locks = mutableListOf<IntArray>()
		for (grid in input.chunked(8)) {
			val item = IntArray(5)
			for (x in 0 until 5) item[x] = (1..5).count { grid[it][x] == '#' }
			if (grid[0] == "#####") locks.add(item) else keys.add(item)
		}
		return keys to locks
	}

	fun fit(key: IntArray, lock: IntArray): Boolean {
		return key.zip(lock) { k, l -> k + l }.all { it <= 5 }
	}

	fun part1(input: List<String>): Int {
		val (keys, locks) = parse(input)
		var fitCount = 0
		for (key in keys) {
			for (lock in locks) {
				if (fit(key, lock)) fitCount++
			}
		}
		return fitCount
	}

	val testInput = readInput("Day25_test")
	check(part1(testInput) == 3)

	val input = readInput("Day25")
	part1(input).println() // 3451
}

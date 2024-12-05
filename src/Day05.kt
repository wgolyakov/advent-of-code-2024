fun main() {
	fun parse(input: List<String>): Pair<Map<Int, Set<Int>>, List<List<Int>>> {
		val rules = mutableMapOf<Int, MutableSet<Int>>()
		val updates = mutableListOf<List<Int>>()
		var firstBlock = true
		for (line in input) {
			if (line.isEmpty()) {
				firstBlock = false
				continue
			}
			if (firstBlock) {
				val (x, y) = line.split('|').map { it.toInt() }
				rules.getOrPut(x) { mutableSetOf() }.add(y)
			} else {
				val update = line.split(',').map { it.toInt() }
				updates.add(update)
			}
		}
		return rules to updates
	}

	fun isCorrect(update: List<Int>, rules: Map<Int, Set<Int>>): Boolean {
		val indexes = mutableMapOf<Int, Int>()
		for ((i, page) in update.withIndex()) indexes[page] = i
		for (x in update) {
			val rule = rules[x] ?: continue
			val xi = indexes[x]!!
			for (y in rule) {
				val yi = indexes[y] ?: continue
				if (xi >= yi) return false
			}
		}
		return true
	}

	fun fix(update: List<Int>, rules: Map<Int, Set<Int>>): List<Int> {
		val fixed = update.toMutableList()
		for (x in update) {
			val rule = rules[x] ?: continue
			var xi = fixed.indexOf(x)
			for (y in rule) {
				val yi = fixed.indexOf(y)
				if (yi != -1 && xi >= yi) {
					fixed.removeAt(xi)
					fixed.add(yi, x)
					xi = yi
				}
			}
		}
		return fixed
	}

	fun part1(input: List<String>): Int {
		val (rules, updates) = parse(input)
		return updates.filter { isCorrect(it, rules) }.sumOf { it[it.size / 2] }
	}

	fun part2(input: List<String>): Int {
		val (rules, updates) = parse(input)
		return updates.filter { !isCorrect(it, rules) }.map { fix(it, rules) }.sumOf { it[it.size / 2] }
	}

	val testInput = readInput("Day05_test")
	check(part1(testInput) == 143)
	check(part2(testInput) == 123)

	val input = readInput("Day05")
	part1(input).println() // 4924
	part2(input).println() // 6085
}

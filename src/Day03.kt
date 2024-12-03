fun main() {
	fun part1(input: List<String>): Int {
		val text = input.joinToString("")
		val matchResults = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)").findAll(text)
		return matchResults.sumOf { it.groupValues[1].toInt() * it.groupValues[2].toInt() }
	}

	fun part2(input: List<String>): Int {
		val text = input.joinToString("")
		val matchResults = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)").findAll(text)
		var sum = 0
		var enable = true
		for (mr in matchResults) {
			if (mr.groupValues[0] == "do()") {
				enable = true
			} else if (mr.groupValues[0] == "don't()") {
				enable = false
			} else {
				if (enable) sum += mr.groupValues[1].toInt() * mr.groupValues[2].toInt()
			}
		}
		return sum
	}

	val testInput = readInput("Day03_test")
	val testInput2 = readInput("Day03_test2")
	check(part1(testInput) == 161)
	check(part2(testInput2) == 48)

	val input = readInput("Day03")
	part1(input).println() // 166357705
	part2(input).println() // 88811886
}

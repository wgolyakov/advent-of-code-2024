fun main() {
	fun parse(line: String): Pair<Long, List<Int>> {
		val (strValue, strNumbers) = line.split(": ")
		val testValue = strValue.toLong()
		val numbers = strNumbers.split(' ').map { it.toInt() }
		return testValue to numbers
	}

	fun test(testValue: Long, numbers: List<Int>, value: Long = numbers[0].toLong(), i: Int = 1): Boolean {
		if (i >= numbers.size) return value == testValue
		if (value > testValue) return false
		return test(testValue, numbers, value + numbers[i], i + 1) ||
				test(testValue, numbers, value * numbers[i], i + 1)
	}

	fun concat(x: Long, y: Int) = "$x$y".toLong()

	fun test2(testValue: Long, numbers: List<Int>, value: Long = numbers[0].toLong(), i: Int = 1): Boolean {
		if (i >= numbers.size) return value == testValue
		if (value > testValue) return false
		return test2(testValue, numbers, value + numbers[i], i + 1) ||
				test2(testValue, numbers, value * numbers[i], i + 1) ||
				test2(testValue, numbers, concat(value, numbers[i]), i + 1)
	}

	fun part1(input: List<String>): Long {
		return input.map { parse(it) }.filter { (value, numbers) -> test(value, numbers) }.sumOf { it.first }
	}

	fun part2(input: List<String>): Long {
		return input.map { parse(it) }.filter { (value, numbers) -> test2(value, numbers) }.sumOf { it.first }
	}

	val testInput = readInput("Day07_test")
	check(part1(testInput) == 3749L)
	check(part2(testInput) == 11387L)

	val input = readInput("Day07")
	part1(input).println() // 66343330034722
	part2(input).println() // 637696070419031
}

fun main() {
	fun safe(report: List<Int>): Boolean {
		val increasing = report[0] < report[1]
		for (i in 1 until report.size) {
			val d = if (increasing) report[i] - report[i - 1] else report[i - 1] - report[i]
			if (d < 1 || d > 3) return false
		}
		return true
	}

	fun safe2(report: List<Int>): Boolean {
		for (i in report.indices) {
			val subReport = report.subList(0, i) + report.subList(i + 1, report.size)
			if (safe(subReport)) return true
		}
		return false
	}

	fun part1(input: List<String>): Int {
		return input.count { line -> safe(line.split(' ').map { it.toInt() }) }
	}

	fun part2(input: List<String>): Int {
		return input.count { line -> safe2(line.split(' ').map { it.toInt() }) }
	}

	val testInput = readInput("Day02_test")
	check(part1(testInput) == 2)
	check(part2(testInput) == 4)

	val input = readInput("Day02")
	part1(input).println() // 356
	part2(input).println() // 413
}

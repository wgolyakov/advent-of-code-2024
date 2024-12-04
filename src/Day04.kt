fun main() {
	val word = "XMAS"

	fun countWordsInOnePoint(input: List<String>, x0: Int, y0: Int): Int {
		if (input[y0][x0] != word[0]) return 0
		var wordCount = 0
		for (dx in -1..1) {
			for (dy in -1..1) {
				if (dx == 0 && dy == 0) continue
				var match = true
				for (i in 1 .. word.lastIndex) {
					val x = x0 + dx * i
					val y = y0 + dy * i
					if (y < 0 || y >= input.size || x < 0 || x >= input[y].length || input[y][x] != word[i]) {
						match = false
						break
					}
				}
				if (match) wordCount++
			}
		}
		return wordCount
	}

	// M S  M M  S S  S M
	//  A    A    A    A
	// M S  S S  M M  S M
	fun isXmas(input: List<String>, x0: Int, y0: Int): Boolean {
		if (input[y0][x0] != 'A') return false
		if (y0 <= 0 || y0 >= input.lastIndex) return false
		if (x0 <= 0 || x0 >= input[y0].lastIndex) return false
		val word1 = "${input[y0 - 1][x0 - 1]}${input[y0][x0]}${input[y0 + 1][x0 + 1]}"
		val word2 = "${input[y0 + 1][x0 - 1]}${input[y0][x0]}${input[y0 - 1][x0 + 1]}"
		return (word1 == "MAS" || word1 == "SAM") && (word2 == "MAS" || word2 == "SAM")
	}

	fun part1(input: List<String>): Int {
		var wordCount = 0
		for (y in input.indices) {
			for (x in input[y].indices) {
				wordCount += countWordsInOnePoint(input, x, y)
			}
		}
		return wordCount
	}

	fun part2(input: List<String>): Int {
		var xmasCount = 0
		for (y in input.indices) {
			for (x in input[y].indices) {
				if (isXmas(input, x, y)) xmasCount++
			}
		}
		return xmasCount
	}

	val testInput = readInput("Day04_test")
	check(part1(testInput) == 18)
	check(part2(testInput) == 9)

	val input = readInput("Day04")
	part1(input).println() // 2591
	part2(input).println() // 1880
}

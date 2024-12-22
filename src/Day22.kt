fun main() {
	fun mix(x: Long, y: Int) = x.xor(y.toLong())
	fun prune(x: Long) = (x % 16777216).toInt()

	fun generateSecretNumber(n: Int): Int {
		val a = prune(mix(n.toLong() * 64, n))
		val b = prune(mix(a.toLong() / 32, a))
		val c = prune(mix(b.toLong() * 2048, b))
		return c
	}

	fun part1(input: List<String>): Long {
		var result = 0L
		for (line in input) {
			var n = line.toInt()
			for (i in 1..2000) n = generateSecretNumber(n)
			result += n
		}
		return result
	}

	fun part2(input: List<String>): Int {
		val numbers = Array(input.size) { MutableList(2001) { 0 } }
		val prices = Array(input.size) { MutableList(2001) { 0 } }
		val changes = Array(input.size) { MutableList(2000) { 0 } }
		for ((i, line) in input.withIndex()) {
			numbers[i][0] = line.toInt()
			prices[i][0] = numbers[i][0] % 10
			for (j in 1..2000) {
				numbers[i][j] = generateSecretNumber(numbers[i][j - 1])
				prices[i][j] = numbers[i][j] % 10
				changes[i][j - 1] = prices[i][j] - prices[i][j - 1]
			}
		}

		val bananas = mutableMapOf<List<Int>, Int>()
		for ((i, buyerChanges) in changes.withIndex()) {
			val usedFours = mutableSetOf<List<Int>>()
			for ((j, four) in buyerChanges.windowed(4).withIndex()) {
				if (four in usedFours) continue
				bananas[four] = (bananas[four] ?: 0) + prices[i][j + 4]
				usedFours.add(four)
			}
		}
		return bananas.values.max()
	}

	check(generateSecretNumber(123) == 15887950)
	check(generateSecretNumber(15887950) == 16495136)
	check(generateSecretNumber(16495136) == 527345)
	check(generateSecretNumber(527345) == 704524)
	check(generateSecretNumber(704524) == 1553684)
	check(generateSecretNumber(1553684) == 12683156)
	check(generateSecretNumber(12683156) == 11100544)
	check(generateSecretNumber(11100544) == 12249484)
	check(generateSecretNumber(12249484) == 7753432)
	check(generateSecretNumber(7753432) == 5908254)

	val testInput = readInput("Day22_test")
	val testInput2 = readInput("Day22_test2")
	check(part1(testInput) == 37327623L)
	check(part2(testInput2) == 23)

	val input = readInput("Day22")
	part1(input).println() // 15613157363
	part2(input).println() // 1784
}

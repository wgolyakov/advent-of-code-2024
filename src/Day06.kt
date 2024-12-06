private enum class Direction(val dx: Int, val dy: Int) {
	Up(0, -1),
	Down(0, 1),
	Left(-1, 0),
	Right(1, 0);

	fun right(): Direction {
		return when (this) {
			Up -> Right
			Down -> Left
			Left -> Up
			Right -> Down
		}
	}
}

fun main() {
	fun findStart(input: List<String>): Pair<Int, Int> {
		for ((y, row) in input.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c == '^') return x to y
			}
		}
		error("Start not found")
	}

	fun fillPath(map: List<StringBuilder>, x0: Int, y0: Int) {
		var direction = Direction.Up
		var x = x0
		var y = y0
		while (true) {
			map[y][x] = 'X'
			val x2 = x + direction.dx
			val y2 = y + direction.dy
			if (y2 !in map.indices || x2 !in map[y2].indices) break
			if (map[y2][x2] == '#') {
				direction = direction.right()
			} else {
				x = x2
				y = y2
			}
		}
	}

	fun isGuardInLoop(map: List<String>, x0: Int, y0: Int, xo: Int, yo: Int): Boolean {
		var direction = Direction.Up
		var x = x0
		var y = y0
		val path = mutableSetOf<Triple<Int, Int, Direction>>()
		while (true) {
			path.add(Triple(x, y, direction))
			val x2 = x + direction.dx
			val y2 = y + direction.dy
			if (y2 !in map.indices || x2 !in map[y2].indices) break
			if (map[y2][x2] == '#' || (x2 == xo && y2 == yo)) {
				direction = direction.right()
			} else {
				x = x2
				y = y2
			}
			if (Triple(x, y, direction) in path) return true
		}
		return false
	}

	fun part1(input: List<String>): Int {
		val (x0, y0) = findStart(input)
		val map = input.map { StringBuilder(it) }
		fillPath(map, x0, y0)
		return map.sumOf { row -> row.count { it == 'X' } }
	}

	fun part2(input: List<String>): Int {
		val (x0, y0) = findStart(input)
		var positionsCount = 0
		for ((y, row) in input.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c != '.') continue
				if (isGuardInLoop(input, x0, y0, x, y)) positionsCount++
			}
		}
		return positionsCount
	}

	val testInput = readInput("Day06_test")
	check(part1(testInput) == 41)
	check(part2(testInput) == 6)

	val input = readInput("Day06")
	part1(input).println() // 4758
	part2(input).println() // 1670
}

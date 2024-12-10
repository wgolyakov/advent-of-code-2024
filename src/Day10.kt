fun main() {
	data class Point(val x: Int, val y: Int)

	fun bfsScore(map: List<String>, start: Point): Int {
		var result = 0
		val visited = Array(map.size) { BooleanArray(map[it].length) }
		val queue = mutableListOf<Point>()
		visited[start.y][start.x] = true
		queue.add(start)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			val height = map[curr.y][curr.x].digitToInt()
			if (height == 9) {
				result++
				continue
			}
			val neighbors = listOf(
				Point(curr.x - 1, curr.y),
				Point(curr.x + 1, curr.y),
				Point(curr.x, curr.y - 1),
				Point(curr.x, curr.y + 1)
			).filter { it.y in map.indices && it.x in map[it.y].indices &&
					!visited[it.y][it.x] && map[it.y][it.x].digitToInt() == height + 1 }
			for (next in neighbors) {
				visited[next.y][next.x] = true
				queue.add(next)
			}
		}
		return result
	}

	fun bfsRating(map: List<String>, start: Point): Int {
		var result = 0
		val queue = mutableListOf<Point>()
		queue.add(start)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			val height = map[curr.y][curr.x].digitToInt()
			if (height == 9) {
				result++
				continue
			}
			val neighbors = listOf(
				Point(curr.x - 1, curr.y),
				Point(curr.x + 1, curr.y),
				Point(curr.x, curr.y - 1),
				Point(curr.x, curr.y + 1)
			).filter { it.y in map.indices && it.x in map[it.y].indices &&
					map[it.y][it.x].digitToInt() == height + 1 }
			for (next in neighbors) {
				queue.add(next)
			}
		}
		return result
	}

	fun part1(input: List<String>): Int {
		var result = 0
		for ((y, row) in input.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c == '0') result += bfsScore(input, Point(x, y))
			}
		}
		return result
	}

	fun part2(input: List<String>): Int {
		var result = 0
		for ((y, row) in input.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c == '0') result += bfsRating(input, Point(x, y))
			}
		}
		return result
	}

	val testInput = readInput("Day10_test")
	check(part1(testInput) == 36)
	check(part2(testInput) == 81)

	val input = readInput("Day10")
	part1(input).println() // 489
	part2(input).println() // 1086
}

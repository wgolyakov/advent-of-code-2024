import kotlin.math.abs

fun main() {
	class Point(val x: Int, val y: Int)

	fun findEnd(map: List<String>): Point {
		for ((y, row) in map.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c == 'E') return Point(x, y)
			}
		}
		error("End not found")
	}

	fun bfsDistances(map: List<String>, end: Point): Array<IntArray> {
		val distances = Array(map.size) { IntArray(map[it].length) { -1 } }
		val queue = mutableListOf<Point>()
		distances[end.y][end.x] = 0
		queue.add(end)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			val distance = distances[curr.y][curr.x]
			val neighbors = listOf(
				Point(curr.x - 1, curr.y),
				Point(curr.x + 1, curr.y),
				Point(curr.x, curr.y - 1),
				Point(curr.x, curr.y + 1)
			).filter { distances[it.y][it.x] == -1 && map[it.y][it.x] != '#' }
			for (next in neighbors) {
				distances[next.y][next.x] = distance + 1
				queue.add(next)
			}
		}
		return distances
	}

	fun part1(input: List<String>, n: Int = 100): Int {
		val end = findEnd(input)
		val distances = bfsDistances(input, end)
		var cheatCount = 0
		for ((y, row) in distances.withIndex()) {
			for ((x, dist) in row.withIndex()) {
				if (dist == -1) continue
				val cheats = listOf(
					Point(x - 2, y),
					Point(x + 2, y),
					Point(x, y - 2),
					Point(x, y + 2)
				).filter { it.y in distances.indices && it.x in distances[it.y].indices &&
						distances[it.y][it.x] != -1 }
				cheatCount += cheats.count { dist - distances[it.y][it.x] - 2 >= n }
			}
		}
		return cheatCount
	}

	fun part2(input: List<String>, n: Int = 100): Int {
		val end = findEnd(input)
		val distances = bfsDistances(input, end)
		var cheatCount = 0
		for ((y, row) in distances.withIndex()) {
			for ((x, dist) in row.withIndex()) {
				if (dist == -1) continue
				for (dy in -20..20) {
					for (dx in abs(dy) - 20 .. 20 - abs(dy)) {
						if (dx == 0 && dy == 0) continue
						val px = x + dx
						val py = y + dy
						if (py !in distances.indices || px !in distances[py].indices) continue
						val dist2 = distances[py][px]
						if (dist2 == -1) continue
						val d = abs(dx) + abs(dy)
						if (dist - dist2 - d >= n) cheatCount++
					}
				}
			}
		}
		return cheatCount
	}

	val testInput = readInput("Day20_test")
	check(part1(testInput, 50) == 1)
	check(part2(testInput, 50) == 32 + 31 + 29 + 39 + 25 + 23 + 20 + 19 + 12 + 14 + 12 + 22 + 4 + 3)

	val input = readInput("Day20")
	part1(input).println() // 1417
	part2(input).println() // 1014683
}

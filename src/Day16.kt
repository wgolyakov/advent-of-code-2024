private enum class Direction16(val dx: Int, val dy: Int) {
	Up(0, -1),
	Down(0, 1),
	Left(-1, 0),
	Right(1, 0);

	fun left() = when (this) {
		Up -> Left
		Down -> Right
		Left -> Down
		Right -> Up
	}

	fun right() = when (this) {
		Up -> Right
		Down -> Left
		Left -> Up
		Right -> Down
	}
}

fun main() {
	data class Point(val x: Int, val y: Int)

	class Move(val point: Point, val direction: Direction16, var score: Int, var weight: Int)

	class Move2(val point: Point, val direction: Direction16, var score: Int, var weight: Int,
				val path: List<Point> = listOf(point))

	fun findStart(map: List<String>): Point {
		for ((y, row) in map.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c == 'S') return Point(x, y)
			}
		}
		error("Start not found")
	}

	@Suppress("unused")
	fun printMap(map: List<String>, bestPoints: Set<Point>) {
		println()
		for (y in map.indices) {
			for (x in map[y].indices) {
				if (Point(x, y) in bestPoints) print('O') else print(map[y][x])
			}
			println()
		}
	}

	fun part1(map: List<String>): Int {
		val start = Move(findStart(map), Direction16.Right, 0, 0)
		val visited = Array(map.size) { BooleanArray(map[it].length) }
		val queue = mutableListOf<Move>()
		visited[start.point.y][start.point.x] = true
		queue.add(start)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			if (curr.weight > 0) {
				curr.weight--
				curr.score++
				if (visited[curr.point.y][curr.point.x]) continue
				if (curr.weight == 0)  {
					visited[curr.point.y][curr.point.x] = true
				} else {
					queue.add(curr)
					continue
				}
			}
			if (map[curr.point.y][curr.point.x] == 'E') return curr.score
			val directions = listOf(curr.direction, curr.direction.left(), curr.direction.right())
			for (direction in directions) {
				val nextPoint = Point(curr.point.x + direction.dx, curr.point.y + direction.dy)
				if (map[nextPoint.y][nextPoint.x] == '#' || visited[nextPoint.y][nextPoint.x]) continue
				if (direction === curr.direction) {
					visited[nextPoint.y][nextPoint.x] = true
					queue.add(Move(nextPoint, direction, curr.score + 1, 0))
				} else {
					queue.add(Move(nextPoint, direction, curr.score + 1, 1000))
				}
			}
		}
		error("End not found")
	}

	fun part2(map: List<String>): Int {
		val start = Move2(findStart(map), Direction16.Right, 0, 0)
		val scores = Array(map.size) { Array(map[it].length) { mutableMapOf<Direction16, Int>() } }
		val bestPoints = mutableSetOf<Point>()
		val queue = mutableListOf<Move2>()
		scores[start.point.y][start.point.x][start.direction] = 0
		queue.add(start)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			if (curr.weight > 0) {
				curr.weight--
				curr.score++
				val nextScore = scores[curr.point.y][curr.point.x][curr.direction]
				if (nextScore != null && nextScore < curr.score) continue
				if (curr.weight == 0)  {
					scores[curr.point.y][curr.point.x][curr.direction] = curr.score
					queue.add(Move2(curr.point, curr.direction, curr.score, 0, curr.path + curr.point))
				} else {
					queue.add(curr)
				}
				continue
			}
			if (map[curr.point.y][curr.point.x] == 'E') {
				if (curr.score > scores[curr.point.y][curr.point.x].values.min()) continue
				bestPoints.addAll(curr.path)
				continue
			}
			val directions = listOf(curr.direction, curr.direction.left(), curr.direction.right())
			for (direction in directions) {
				val nextPoint = Point(curr.point.x + direction.dx, curr.point.y + direction.dy)
				if (map[nextPoint.y][nextPoint.x] == '#') continue
				val nextScore = scores[nextPoint.y][nextPoint.x][direction]
				if (direction === curr.direction) {
					if (nextScore != null && nextScore < curr.score + 1) continue
					scores[nextPoint.y][nextPoint.x][direction] = curr.score + 1
					queue.add(Move2(nextPoint, direction, curr.score + 1, 0, curr.path + nextPoint))
				} else {
					if (nextScore != null && nextScore < curr.score + 1001) continue
					queue.add(Move2(nextPoint, direction, curr.score + 1, 1000, curr.path))
				}
			}
		}
		//printMap(map, bestPoints)
		return bestPoints.size
	}

	val testInput = readInput("Day16_test")
	val testInput2 = readInput("Day16_test2")
	check(part1(testInput) == 7036)
	check(part1(testInput2) == 11048)
	check(part2(testInput) == 45)
	check(part2(testInput2) == 64)

	val input = readInput("Day16")
	part1(input).println() // 95444
	part2(input).println() // 513
}

fun main() {
	data class Point(val x: Int, val y: Int)

	fun bfsDistance(corrupted: Set<Point>, start: Point, stop: Point): Int {
		val distances = Array(stop.y + 1) { IntArray(stop.x + 1) { -1 } }
		val queue = mutableListOf<Point>()
		distances[start.y][start.x] = 0
		queue.add(start)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			val distance = distances[curr.y][curr.x]
			if (curr == stop) return distance
			val neighbors = listOf(
				Point(curr.x - 1, curr.y),
				Point(curr.x + 1, curr.y),
				Point(curr.x, curr.y - 1),
				Point(curr.x, curr.y + 1)
			).filter { it.y in distances.indices && it.x in distances[it.y].indices &&
					distances[it.y][it.x] == -1 && it !in corrupted }
			for (next in neighbors) {
				distances[next.y][next.x] = distance + 1
				queue.add(next)
			}
		}
		error("Exit not reachable")
	}

	fun bfsReachable(corrupted: Set<Point>, start: Point, stop: Point): Boolean {
		val visited = Array(stop.y + 1) { BooleanArray(stop.x + 1) }
		val queue = mutableListOf<Point>()
		visited[start.y][start.x] = true
		queue.add(start)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			if (curr == stop) return true
			val neighbors = listOf(
				Point(curr.x - 1, curr.y),
				Point(curr.x + 1, curr.y),
				Point(curr.x, curr.y - 1),
				Point(curr.x, curr.y + 1)
			).filter { it.y in visited.indices && it.x in visited[it.y].indices &&
					!visited[it.y][it.x] && it !in corrupted }
			for (next in neighbors) {
				visited[next.y][next.x] = true
				queue.add(next)
			}
		}
		return false
	}

	fun part1(input: List<String>, exit: Point = Point(70, 70), bytes: Int = 1024): Int {
		val corrupted = input.take(bytes).map { it.split(',') }
			.map { (x, y) -> Point(x.toInt(), y.toInt()) }.toSet()
		return bfsDistance(corrupted, Point(0, 0), exit)
	}

	fun part2(input: List<String>, exit: Point = Point(70, 70)): String {
		val incomingBytes = input.map { it.split(',') }.map { (x, y) -> Point(x.toInt(), y.toInt()) }
		val currPosition = Point(0, 0)
		for ((i, p) in incomingBytes.withIndex()) {
			val corrupted = incomingBytes.subList(0, i + 1).toSet()
			if (!bfsReachable(corrupted, currPosition, exit)) return "${p.x},${p.y}"
		}
		error("Exit always reachable")
	}

	val testInput = readInput("Day18_test")
	check(part1(testInput, Point(6, 6), 12) == 22)
	check(part2(testInput, Point(6, 6)) == "6,1")

	val input = readInput("Day18")
	part1(input).println() // 404
	part2(input).println() // 27,60
}

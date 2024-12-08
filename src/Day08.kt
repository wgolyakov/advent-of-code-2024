fun main() {
	data class Point(val x: Int, val y: Int)

	fun findAntennas(map: List<String>): Map<Char, List<Point>>{
		val antennas = mutableMapOf<Char, MutableList<Point>>()
		for ((y, row) in map.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c != '.') antennas.getOrPut(c) { mutableListOf() }.add(Point(x, y))
			}
		}
		return antennas
	}

	fun findAntiNodes(antennas: Map<Char, List<Point>>, map: List<String>): Set<Point>{
		val antiNodes = mutableSetOf<Point>()
		for (points in antennas.values) {
			for ((i, p) in points.withIndex()) {
				for (q in points.subList(i + 1, points.size)) {
					val dx = q.x - p.x
					val dy = q.y - p.y
					val p1 = Point(p.x - dx, p.y - dy)
					val p2 = Point(q.x + dx, q.y + dy)
					if (p1.y in map.indices && p1.x in map[p1.y].indices) antiNodes.add(p1)
					if (p2.y in map.indices && p2.x in map[p2.y].indices) antiNodes.add(p2)
				}
			}
		}
		return antiNodes
	}

	fun findAntiNodes2(antennas: Map<Char, List<Point>>, map: List<String>): Set<Point>{
		val antiNodes = mutableSetOf<Point>()
		for (points in antennas.values) {
			for ((i, p) in points.withIndex()) {
				for (q in points.subList(i + 1, points.size)) {
					val dx = q.x - p.x
					val dy = q.y - p.y
					var j = 0
					while (true) {
						val p1 = Point(p.x - dx * j, p.y - dy * j)
						if (p1.y !in map.indices || p1.x !in map[p1.y].indices) break
						antiNodes.add(p1)
						j++
					}
					j = 0
					while (true) {
						val p2 = Point(q.x + dx * j, q.y + dy * j)
						if (p2.y !in map.indices || p2.x !in map[p2.y].indices) break
						antiNodes.add(p2)
						j++
					}
				}
			}
		}
		return antiNodes
	}

	fun part1(input: List<String>): Int {
		val antennas = findAntennas(input)
		val antiNodes = findAntiNodes(antennas, input)
		return antiNodes.size
	}

	fun part2(input: List<String>): Int {
		val antennas = findAntennas(input)
		val antiNodes = findAntiNodes2(antennas, input)
		return antiNodes.size
	}

	val testInput = readInput("Day08_test")
	check(part1(testInput) == 14)
	check(part2(testInput) == 34)

	val input = readInput("Day08")
	part1(input).println() // 329
	part2(input).println() // 1190
}

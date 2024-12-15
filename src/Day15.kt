fun main() {
	data class Point(val x: Int, val y: Int)

	val directions = mapOf(
		'>' to Point(1, 0),
		'<' to Point(-1, 0),
		'v' to Point(0, 1),
		'^' to Point(0, -1)
	)

	fun parse(input: List<String>): Pair<List<StringBuilder>, List<Point>> {
		var readMap = true
		val map = mutableListOf<StringBuilder>()
		val dirs = mutableListOf<Point>()
		for (line in input) {
			if (line.isEmpty()) {
				readMap = false
				continue
			}
			if (readMap) {
				map.add(StringBuilder(line))
			} else {
				dirs.addAll(line.map { directions[it]!! })
			}
		}
		return map to dirs
	}

	fun findRobot(map: List<StringBuilder>): Point {
		for ((y, row) in map.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c == '@') return Point(x, y)
			}
		}
		error("Robot not found")
	}

	fun move(map: List<StringBuilder>, robot: Point, dir: Point): Point {
		var n = 0
		var c: Char
		do {
			n++
			c = map[robot.y + dir.y * n][robot.x + dir.x * n]
		} while (c == 'O')
		if (c == '#') return robot
		for (i in n downTo 1) {
			map[robot.y + dir.y * i][robot.x + dir.x * i] = map[robot.y + dir.y * (i - 1)][robot.x + dir.x * (i - 1)]
		}
		map[robot.y][robot.x] = '.'
		return Point(robot.x + dir.x, robot.y + dir.y)
	}

	fun gpsSum(map: List<StringBuilder>): Int {
		var result = 0
		for ((y, row) in map.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c == 'O' || c == '[') result += y * 100 + x
			}
		}
		return result
	}

	fun widerMap(map: List<StringBuilder>): List<StringBuilder> {
		return map.map { row ->
			StringBuilder(row.map {
				when (it) {
					'#' -> "##"
					'O' -> "[]"
					'.' -> ".."
					'@' -> "@."
					else -> error("Wrong symbol on map: $it")
				}
			}.joinToString(""))
		}
	}

	fun moveWide(map: List<StringBuilder>, robot: Point, dir: Point): Point {
		var c = map[robot.y + dir.y][robot.x + dir.x]
		if (c == '#') {
			// Not move
			return robot
		}
		if (c == '.') {
			// Move without boxes
			map[robot.y + dir.y][robot.x + dir.x] = map[robot.y][robot.x]
			map[robot.y][robot.x] = '.'
			return Point(robot.x + dir.x, robot.y + dir.y)
		}
		if (dir.y == 0) {
			// Horizontal move
			var n = 0
			do {
				n++
				c = map[robot.y][robot.x + dir.x * n]
			} while (c == '[' || c == ']')
			if (c == '#') return robot
			for (i in n downTo 1) {
				map[robot.y][robot.x + dir.x * i] = map[robot.y][robot.x + dir.x * (i - 1)]
			}
			map[robot.y][robot.x] = '.'
			return Point(robot.x + dir.x, robot.y)
		}
		// Vertical move
		var boxes = mutableSetOf<Point>()
		if (map[robot.y + dir.y][robot.x] == '[') {
			boxes.add(Point(robot.x, robot.y + dir.y))
		} else if (map[robot.y + dir.y][robot.x] == ']') {
			boxes.add(Point(robot.x - 1, robot.y + dir.y))
		}
		val boxTree = mutableListOf<Set<Point>>()
		while (boxes.isNotEmpty()) {
			boxTree.add(boxes)
			val nextBoxes = mutableSetOf<Point>()
			for (box in boxes) {
				val c1 = map[box.y + dir.y][box.x]
				val c2 = map[box.y + dir.y][box.x + 1]
				if (c1 == '#' || c2 == '#') return robot
				if (c1 == ']') nextBoxes.add(Point(box.x - 1, box.y + dir.y))
				if (c1 == '[' && c2 == ']') nextBoxes.add(Point(box.x, box.y + dir.y))
				if (c2 == '[') nextBoxes.add(Point(box.x + 1, box.y + dir.y))
			}
			boxes = nextBoxes
		}
		for (boxRow in boxTree.reversed()) {
			for (box in boxRow) {
				map[box.y + dir.y][box.x] = map[box.y][box.x]
				map[box.y + dir.y][box.x + 1] = map[box.y][box.x + 1]
				map[box.y][box.x] = '.'
				map[box.y][box.x + 1] = '.'
			}
		}
		map[robot.y + dir.y][robot.x] = map[robot.y][robot.x]
		map[robot.y][robot.x] = '.'
		return Point(robot.x, robot.y + dir.y)
	}

	fun part1(input: List<String>): Int {
		val (map, dirs) = parse(input)
		var robot = findRobot(map)
		for (dir in dirs) {
			robot = move(map, robot, dir)
		}
		return gpsSum(map)
	}

	fun part2(input: List<String>): Int {
		val (map, dirs) = parse(input)
		val wMap = widerMap(map)
		var robot = findRobot(wMap)
		for (dir in dirs) {
			robot = moveWide(wMap, robot, dir)
		}
		return gpsSum(wMap)
	}

	val testInput = readInput("Day15_test")
	val testInput2 = readInput("Day15_test2")
	check(part1(testInput) == 2028)
	check(part1(testInput2) == 10092)
	check(part2(testInput2) == 9021)

	val input = readInput("Day15")
	part1(input).println() // 1563092
	part2(input).println() // 1582688
}

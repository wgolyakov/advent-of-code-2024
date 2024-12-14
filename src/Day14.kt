fun main() {
	class Point(val x: Int, val y: Int)
	class Robot(var p: Point, val v: Point)
	fun parsePoint(s: String) = s.drop(2).split(',').let { (x, y) -> Point(x.toInt(), y.toInt()) }

	fun parse(input: List<String>) = input.map { it.split(' ')
		.let { (p, v) -> Robot(parsePoint(p), parsePoint(v)) } }

	fun moveRobots(robots: List<Robot>, space: Point) {
		for (robot in robots) {
			robot.p = Point(
				(robot.p.x + robot.v.x + space.x) % space.x,
				(robot.p.y + robot.v.y + space.y) % space.y
			)
		}
	}

	fun safetyFactor(robots: List<Robot>, space: Point): Int {
		var q1 = 0
		var q2 = 0
		var q3 = 0
		var q4 = 0
		val cx = space.x / 2
		val cy = space.y / 2
		val left = 0 until cx
		val right = space.x - cx until space.x
		val up = 0 until cy
		val down = space.y - cy until space.y
		for (robot in robots) {
			if (robot.p.x in left && robot.p.y in up) q1++
			else if (robot.p.x in right && robot.p.y in up) q2++
			else if (robot.p.x in left && robot.p.y in down) q3++
			else if (robot.p.x in right && robot.p.y in down) q4++
		}
		return q1 * q2 * q3 * q4
	}

	fun printMap(robots: List<Robot>, space: Point) {
		val row = ".".repeat(space.x)
		val map = Array(space.y) { StringBuilder(row) }
		for (robot in robots) map[robot.p.y][robot.p.x] = '#'
		println(map.joinToString("\n"))
	}

	fun part1(input: List<String>, space: Point = Point(101, 103)): Int {
		val robots = parse(input)
		for (i in 0 until 100) moveRobots(robots, space)
		return safetyFactor(robots, space)
	}

	fun part2(input: List<String>, space: Point = Point(101, 103)): Int {
		val robots = parse(input)
		for (i in 0 .. space.x * space.y) {
			val centralVerticalLine = robots.filter { it.p.x == space.x / 2 }.sortedBy { it.p.y }
				.windowed(2).count { (r1, r2) -> r2.p.y - r1.p.y == 1 } + 1
			if (centralVerticalLine > space.y / 10) {
				printMap(robots, space)
				return i
			}
			moveRobots(robots, space)
		}
		return -1
	}

	val testInput = readInput("Day14_test")
	check(part1(testInput, Point(11, 7)) == 12)

	val input = readInput("Day14")
	part1(input).println() // 215476074
	part2(input).println() // 6285
}

fun main() {
	data class Point(val x: Long, val y: Long)
	fun tokens(aTimes: Long, bTimes: Long) = aTimes * 3 + bTimes

	class ClawMachine(val a: Point, val b: Point, val prize: Point) {
		var aTimesBest = 0L
		var bTimesBest = 0L
		var minTokens = 0L

		fun isWin(aTimes: Long, bTimes: Long): Boolean {
			val x = a.x * aTimes + b.x * bTimes
			val y = a.y * aTimes + b.y * bTimes
			return x == prize.x && y == prize.y
		}
	}

	fun parsePoint(s: String, prefix: String): Point {
		val (x, y) = s.substringAfter(prefix).split(", ").map { it.drop(2).toLong() }
		return Point(x, y)
	}

	fun parse(input: List<String>, partTwo: Boolean = false): List<ClawMachine> {
		val machines = mutableListOf<ClawMachine>()
		var a = Point(0, 0)
		var b = Point(0, 0)
		var prize = Point(0, 0)
		for (line in input) {
			if (line.startsWith("Button A: ")) {
				a = parsePoint(line, "Button A: ")
			} else if (line.startsWith("Button B: ")) {
				b = parsePoint(line, "Button B: ")
			} else if (line.startsWith("Prize: ")) {
				prize = parsePoint(line, "Prize: ")
				if (partTwo) prize = Point(prize.x + 10000000000000, prize.y + 10000000000000)
			} else {
				machines.add(ClawMachine(a, b, prize))
			}
		}
		machines.add(ClawMachine(a, b, prize))
		return machines
	}

	fun part1(input: List<String>): Long {
		val machines = parse(input)
		for (machine in machines) {
			for (aTimes in 0L..100L) {
				for (bTimes in 0L..100L) {
					if (machine.isWin(aTimes, bTimes)) {
						val tokens = tokens(aTimes, bTimes)
						if (tokens < machine.minTokens || machine.minTokens == 0L) {
							machine.aTimesBest = aTimes
							machine.bTimesBest = bTimes
							machine.minTokens = tokens
						}
					}
				}
			}
		}
		return machines.sumOf { it.minTokens }
	}

	// Ax*a + Bx*b = Px
	// Ay*a + By*b = Py
	// a = (Px*By - Bx*Py) / (Ax*By - Bx*Ay)
	// b = (Ax*Py - Px*Ay) / (Ax*By - Bx*Ay)
	fun part2(input: List<String>): Long {
		val machines = parse(input, true)
		for (machine in machines) {
			val an = machine.prize.x * machine.b.y - machine.b.x * machine.prize.y
			val d = machine.a.x * machine.b.y - machine.b.x * machine.a.y
			if (an % d != 0L) continue
			val a = an / d
			val bn = machine.a.x * machine.prize.y - machine.prize.x * machine.a.y
			if (bn % d != 0L) continue
			val b = bn / d
			machine.aTimesBest = a
			machine.bTimesBest = b
			machine.minTokens = tokens(a, b)
		}
		return machines.sumOf { it.minTokens }
	}

	val testInput = readInput("Day13_test")
	check(part1(testInput) == 480L)

	val input = readInput("Day13")
	part1(input).println() // 31589
	part2(input).println() // 98080815200063
}

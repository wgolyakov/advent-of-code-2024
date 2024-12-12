fun main() {
	data class Point(val x: Int, val y: Int)

	data class Region(val name: Char, val gardenPlots: MutableSet<Point>) {
		fun area() = gardenPlots.size

		fun perimeter(): Int {
			var result = 0
			for (gardenPlot in gardenPlots) {
				val perimeterSides = listOf(
					Point(gardenPlot.x - 1, gardenPlot.y),
					Point(gardenPlot.x + 1, gardenPlot.y),
					Point(gardenPlot.x, gardenPlot.y - 1),
					Point(gardenPlot.x, gardenPlot.y + 1)
				).filter { it !in gardenPlots }
				result += perimeterSides.size
			}
			return result
		}

		fun fencingPrice() = area() * perimeter()

		fun numberOfSides(): Int {
			val leftSides = mutableListOf<MutableSet<Point>>()
			val rightSides = mutableListOf<MutableSet<Point>>()
			val upSides = mutableListOf<MutableSet<Point>>()
			val downSides = mutableListOf<MutableSet<Point>>()
			for (gardenPlot in gardenPlots.sortedWith(compareBy({ it.y }, { it.x }))) {
				val left = Point(gardenPlot.x - 1, gardenPlot.y)
				val right = Point(gardenPlot.x + 1, gardenPlot.y)
				val up = Point(gardenPlot.x, gardenPlot.y - 1)
				val down = Point(gardenPlot.x, gardenPlot.y + 1)
				if (left !in gardenPlots) {
					val neighbor = Point(left.x, left.y - 1)
					val leftSide = leftSides.find { neighbor in it }
					if (leftSide == null) {
						leftSides.add(mutableSetOf(left))
					} else {
						leftSide.add(left)
					}
				}
				if (right !in gardenPlots) {
					val neighbor = Point(right.x, right.y - 1)
					val rightSide = rightSides.find { neighbor in it }
					if (rightSide == null) {
						rightSides.add(mutableSetOf(right))
					} else {
						rightSide.add(right)
					}
				}
				if (up !in gardenPlots) {
					val neighbor = Point(up.x - 1, up.y)
					val upSide = upSides.find { neighbor in it }
					if (upSide == null) {
						upSides.add(mutableSetOf(up))
					} else {
						upSide.add(up)
					}
				}
				if (down !in gardenPlots) {
					val neighbor = Point(down.x - 1, down.y)
					val downSide = downSides.find { neighbor in it }
					if (downSide == null) {
						downSides.add(mutableSetOf(down))
					} else {
						downSide.add(down)
					}
				}
			}
			return leftSides.size + rightSides.size + upSides.size + downSides.size
		}

		fun discountPrice() = area() * numberOfSides()
	}

	fun makeRegion(map: List<String>, start: Point): Region {
		val c = map[start.y][start.x]
		val gardenPlots = mutableSetOf<Point>()
		val queue = mutableListOf<Point>()
		gardenPlots.add(start)
		queue.add(start)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			val neighbors = listOf(
				Point(curr.x - 1, curr.y),
				Point(curr.x + 1, curr.y),
				Point(curr.x, curr.y - 1),
				Point(curr.x, curr.y + 1)
			).filter { it.y in map.indices && it.x in map[it.y].indices && map[it.y][it.x] == c && it !in gardenPlots }
			for (next in neighbors) {
				gardenPlots.add(next)
				queue.add(next)
			}
		}
		return Region(map[start.y][start.x], gardenPlots)
	}

	fun findRegions(map: List<String>): List<Region> {
		val regions = mutableListOf<Region>()
		val regionsMap = Array(map.size) { Array<Region?>(map[it].length) { null } }
		for ((y, row) in map.withIndex()) {
			for (x in row.indices) {
				var region = regionsMap[y][x]
				if (region != null) continue
				region = makeRegion(map, Point(x, y))
				regions.add(region)
				for (p in region.gardenPlots) regionsMap[p.y][p.x] = region
			}
		}
		return regions
	}

	fun part1(input: List<String>): Int {
		return findRegions(input).sumOf { it.fencingPrice() }
	}

	fun part2(input: List<String>): Int {
		return findRegions(input).sumOf { it.discountPrice() }
	}

	val testInput = readInput("Day12_test")
	val testInput2 = readInput("Day12_test2")
	val testInput3 = readInput("Day12_test3")
	val testInput4 = readInput("Day12_test4")
	val testInput5 = readInput("Day12_test5")
	check(part1(testInput) == 140)
	check(part1(testInput2) == 772)
	check(part1(testInput3) == 1930)
	check(part2(testInput) == 80)
	check(part2(testInput2) == 436)
	check(part2(testInput3) == 1206)
	check(part2(testInput4) == 236)
	check(part2(testInput5) == 368)

	val input = readInput("Day12")
	part1(input).println() // 1370100
	part2(input).println() // 818286
}

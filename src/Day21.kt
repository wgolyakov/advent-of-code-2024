fun main() {
	data class Point(val x: Int, val y: Int)
	class Move(val point: Point, val path: String)

	val numericKeypad = arrayOf(
		"789",
		"456",
		"123",
		" 0A"
	)

	val directionalKeypad = arrayOf(
		" ^A",
		"<v>"
	)

	val directions = mapOf(
		'^' to Point(0, -1),
		'v' to Point(0, 1),
		'<' to Point(-1, 0),
		'>' to Point(1, 0)
	)

	fun bfsPaths(map: Array<String>, start: Point, end: Point): Set<String> {
		val result = mutableSetOf<String>()
		val distances = Array(map.size) { IntArray(map[it].length) { -1 } }
		val queue = mutableListOf<Move>()
		distances[start.y][start.x] = 0
		queue.add(Move(start, ""))
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			if (curr.point == end) {
				result.add(curr.path + 'A')
				continue
			}
			val distance = distances[curr.point.y][curr.point.x]
			for ((c, dir) in directions) {
				val next = Point(curr.point.x + dir.x, curr.point.y + dir.y)
				if (next.y !in map.indices || next.x !in map[next.y].indices) continue
				if (distances[next.y][next.x] != -1 && distances[next.y][next.x] != distance + 1) continue
				if (map[next.y][next.x] == ' ') continue
				distances[next.y][next.x] = distance + 1
				queue.add(Move(next, curr.path + c))
			}
		}
		return result
	}

	fun makePaths(map: Array<String>): Map<String, Set<String>> {
		val paths = mutableMapOf<String, Set<String>>()
		for ((y1, row1) in map.withIndex()) {
			for ((x1, c1) in row1.withIndex()) {
				if (c1 == ' ') continue
				for ((y2, row2) in map.withIndex()) {
					for ((x2, c2) in row2.withIndex()) {
						if (c2 == ' ') continue
						if (c1 == c2) {
							paths["$c1$c2"] = setOf("A")
							continue
						}
						paths["$c1$c2"] = bfsPaths(map, Point(x1, y1), Point(x2, y2))
					}
				}
			}
		}
		return paths
	}

	fun convert(code: String, paths: Map<String, Set<String>>): List<String> {
		var sequences = listOf("")
		for (s in "A$code".windowed(2)) {
			val nextSequences = mutableListOf<String>()
			for (path in paths[s]!!) {
				for (sequence in sequences) nextSequences.add(sequence + path)
			}
			sequences = nextSequences
		}
		return sequences
	}

	fun convert(codes: List<String>, paths: Map<String, Set<String>>): List<String> {
		val sequences = mutableListOf<String>()
		for (code in codes) sequences.addAll(convert(code, paths))
		return sequences
	}

	fun minSequences(sequences: List<String>): List<String> {
		val minLen = sequences.minOf { it.length }
		return sequences.filter { it.length == minLen }
	}

	fun calcLengthRecurs(sequence: String, level: Int, dirPaths: Map<String, Set<String>>,
						 cache: MutableMap<Pair<String, Int>, Long>): Long {
		if (level > 25) return sequence.length.toLong()
		var result = 0L
		for (s in "A$sequence".windowed(2)) {
			var len = cache[s to level]
			if (len == null)  {
				val paths = dirPaths[s]!!
				len = paths.minOf { calcLengthRecurs(it, level + 1, dirPaths, cache) }
				cache[s to level] = len
			}
			result += len
		}
		return result
	}

	fun part1(input: List<String>): Int {
		val numPaths = makePaths(numericKeypad)
		val dirPaths = makePaths(directionalKeypad)
		var result = 0
		for (code in input) {
			val sequences1 = minSequences(convert(code, numPaths))
			val sequences2 = minSequences(convert(sequences1, dirPaths))
			val sequences3 = minSequences(convert(sequences2, dirPaths))
			val minLen = sequences3.minOf { it.length }
			val numericPart = code.dropLast(1).toInt()
			val complexity = minLen * numericPart
			result += complexity
		}
		return result
	}

	fun part2(input: List<String>): Long {
		val numPaths = makePaths(numericKeypad)
		val dirPaths = makePaths(directionalKeypad)
		var result = 0L
		for (code in input) {
			val sequences = convert(code, numPaths)
			val minLen = sequences.minOf { calcLengthRecurs(it, 1, dirPaths, mutableMapOf()) }
			val numericPart = code.dropLast(1).toInt()
			val complexity = minLen * numericPart
			result += complexity
		}
		return result
	}

	val testInput = readInput("Day21_test")
	check(part1(testInput) == 126384)

	val input = readInput("Day21")
	part1(input).println() // 123096
	part2(input).println() // 154517692795352
}

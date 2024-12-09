fun main() {
	class Chunk(var fileID: Int, var size: Int) {
		fun isFree() = fileID == -1
	}

	fun part1(input: List<String>): Long {
		val s = input[0]
		var checksum = 0L
		var i = 0
		var j = s.lastIndex
		// Skip free space
		if (j % 2 != 0) j--
		var lastFileSize = s[j].digitToInt()
		var blockPosition = 0
		while (i < j) {
			val size = s[i].digitToInt()
			if (i % 2 == 0) { // file
				val fileID = i / 2
				for (t in 0 until size) {
					checksum += blockPosition * fileID
					blockPosition++
				}
			} else { // free space
				for (t in 0 until size) {
					if (lastFileSize == 0) {
						j -= 2
						if (j <= i) break
						lastFileSize = s[j].digitToInt()
					}
					val fileID = j / 2
					checksum += blockPosition * fileID
					blockPosition++
					lastFileSize--
				}
			}
			i++
		}
		if (i == j && i % 2 == 0 && lastFileSize > 0) { // file
			val fileID = i / 2
			for (t in 0 until lastFileSize) {
				checksum += blockPosition * fileID
				blockPosition++
			}
		}
		return checksum
	}

	fun part2(input: List<String>): Long {
		// Fill chunks
		val chunks = input[0].withIndex().map { (i, c) ->
			val fileID = if (i % 2 == 0) i / 2 else -1
			val size = c.digitToInt()
			Chunk(fileID, size)
		}.toMutableList()

		// Move files to free space
		for (toMove in chunks.reversed()) {
			if (toMove.isFree() || toMove.size == 0) continue
			for ((i, chunk) in chunks.withIndex()) {
				if (chunk === toMove) break
				if (!chunk.isFree() || chunk.size == 0) continue
				if (toMove.size <= chunk.size) {
					chunk.fileID = toMove.fileID
					if (toMove.size < chunk.size) {
						chunks.add(i + 1, Chunk(-1, chunk.size - toMove.size))
						chunk.size = toMove.size
					}
					toMove.fileID = -1
					break
				}
			}
		}

		// Calculate checksum
		var checksum = 0L
		var blockPosition = 0
		for (chunk in chunks) {
			if (chunk.isFree()) { // free space
				blockPosition += chunk.size
			} else { // file
				for (t in 0 until chunk.size) {
					checksum += blockPosition * chunk.fileID
					blockPosition++
				}
			}
		}
		return checksum
	}

	val testInput = readInput("Day09_test")
	check(part1(testInput) == 1928L)
	check(part2(testInput) == 2858L)

	val input = readInput("Day09")
	part1(input).println() // 6385338159127
	part2(input).println() // 6415163624282
}

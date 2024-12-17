fun main() {
	fun parse(input: List<String>): Pair<LongArray, List<Int>> {
		val registers = LongArray(3)
		var program = listOf<Int>()
		for (line in input) {
			if (line.startsWith("Register A: "))
				registers[0] = line.substringAfter("Register A: ").toLong()
			else if (line.startsWith("Register B: "))
				registers[1] = line.substringAfter("Register B: ").toLong()
			else if (line.startsWith("Register C: "))
				registers[1] = line.substringAfter("Register C: ").toLong()
			else if (line.startsWith("Program: "))
				program = line.substringAfter("Program: ").split(',').map { it.toInt() }
		}
		return registers to program
	}

	fun pow2(x: Long) = if (x == 0L) 1L else 2L.shl(x.toInt() - 1)

	fun combo(operand: Int, reg: LongArray) = if (operand in 0..3) operand.toLong() else reg[operand - 4]

	fun run(reg: LongArray, program: List<Int>): List<Int> {
		val output = mutableListOf<Int>()
		var i = 0
		while (i in program.indices) {
			val opcode = program[i]
			val operand = program[i + 1]
			when (opcode) {
				// adv - division to A
				0 -> reg[0] = reg[0] / pow2(combo(operand, reg))
				// bxl - B XOR o
				1 -> reg[1] = reg[1].xor(operand.toLong())
				// bst - lowest 3 bits
				2 -> reg[1] = combo(operand, reg) % 8
				// jnz - jump if not zero
				3 -> if (reg[0] != 0L) i = operand - 2
				// bxc - B XOR C
				4 -> reg[1] = reg[1].xor(reg[2])
				// out - output
				5 -> output.add((combo(operand, reg) % 8).toInt())
				// bdv - division to B
				6 -> reg[1] = reg[0] / pow2(combo(operand, reg))
				// cdv - division to C
				7 -> reg[2] = reg[0] / pow2(combo(operand, reg))
				else -> error("Wrong opcode: $opcode")
			}
			i += 2
		}
		return output
	}

	fun part1(input: List<String>): String {
		val (reg, program) = parse(input)
		return run(reg, program).joinToString(",")
	}

	fun part2Slow(input: List<String>): Long {
		val (reg, program) = parse(input)
		for (a in 0 .. Long.MAX_VALUE) {
			reg[0] = a
			if (run(reg, program) == program) return a
		}
		return -1
	}

	fun findARecurs(program: List<Int>, digitIndex: Int, a: Long): Long {
		if (digitIndex > program.lastIndex) return a
		val p = program[program.lastIndex - digitIndex]
		for (j in 0 .. 7) {
			if (digitIndex == 0 && j == 0) continue
			val t = a * 8 + j
			val out = run(longArrayOf(t, 0, 0), program)
			if (out[0] == p) {
				val r = findARecurs(program, digitIndex + 1, t)
				if (r != -1L) return r
			}
		}
		return -1
	}

	fun part2(input: List<String>): Long {
		val (_, program) = parse(input)
		return findARecurs(program, 0, 0L)
	}

	val reg = LongArray(3)
	reg[2] = 9; run(reg, listOf(2,6)); check(reg[1] == 1L)
	reg[0] = 10; check(run(reg, listOf(5,0,5,1,5,4)) == listOf(0,1,2))
	reg[0] = 2024; check(run(reg, listOf(0,1,5,4,3,0)) == listOf(4,2,5,6,7,7,7,7,3,1,0)); check(reg[0] == 0L)
	reg[1] = 29; run(reg, listOf(1,7)); check(reg[1] == 26L)
	reg[1] = 2024; reg[2] = 43690; run(reg, listOf(4,0)); check(reg[1] == 44354L)

	val testInput = readInput("Day17_test")
	val testInput2 = readInput("Day17_test2")
	check(part1(testInput) == "4,6,3,5,6,3,5,2,1,0")
	check(part2Slow(testInput2) == 117440L)
	check(part2(testInput2) == 117440L)

	val input = readInput("Day17")
	part1(input).println() // 6,5,7,4,5,7,3,1,0
	part2(input).println() // 105875099912602
}

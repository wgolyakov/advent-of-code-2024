import kotlin.random.Random

private class Wire(val name: String, var value: Int, var fromGate: Gate? = null) {
	val toGates: MutableSet<Gate> = mutableSetOf()

	fun value(): Int {
		if (value == -1) {
			value = -2
			val w1 = fromGate!!.in1.value()
			// Cycle detection
			if (w1 == -2) return -2
			val w2 = fromGate!!.in2.value()
			// Cycle detection
			if (w2 == -2) return -2
			value = fromGate!!.operation(w1, w2)
		}
		return value
	}

	override fun toString() = name
}

private class Gate(val in1: Wire, val in2: Wire, val operation: (Int, Int) -> Int, var out: Wire) {
	override fun toString() = "[$in1,$in2 -> $out]"
}

fun main() {
	fun parse(input: List<String>): Pair<List<Wire>, List<Gate>> {
		val gates = mutableListOf<Gate>()
		val wiresMap = mutableMapOf<String, Wire>()
		for (line in input) {
			if (line.isEmpty()) continue
			if (line[3] == ':') {
				val wireName = line.substring(0, 3)
				val value = line.takeLast(1).toInt()
				val wire = Wire(wireName, value)
				wiresMap[wireName] = wire
			} else {
				val (s, wn3) = line.split(" -> ")
				val wn1 = s.take(3)
				val wn2 = s.takeLast(3)
				val w1 = wiresMap.getOrPut(wn1) { Wire(wn1, -1) }
				val w2 = wiresMap.getOrPut(wn2) { Wire(wn2, -1) }
				val w3 = wiresMap.getOrPut(wn3) { Wire(wn3, -1) }
				val oper = s.substring(4, s.length - 4)
				val operation = when (oper) {
					"AND" -> Int::and
					"OR" -> Int::or
					"XOR" -> Int::xor
					else -> error("Wrong operation: $oper")
				}
				val gate = Gate(w1, w2, operation, w3)
				w1.toGates.add(gate)
				w2.toGates.add(gate)
				w3.fromGate = gate
				gates.add(gate)
			}
		}
		return wiresMap.values.toList() to gates
	}

	fun eval(zWires: List<Wire>): Long {
		val bits = zWires.map { it.value().toLong() }
		if (bits.any { it < 0 }) return -1
		return bits.reduceRight { j, acc -> acc * 2 + j }
	}

	fun calculate(x: Long, y: Long, gates: List<Gate>, xWires: List<Wire>, yWires: List<Wire>, zWires: List<Wire>): Long {
		for (gate in gates) gate.out.value = -1
		var a = 1L
		for (i in xWires.indices) {
			xWires[i].value = if (x and a != 0L) 1 else 0
			yWires[i].value = if (y and a != 0L) 1 else 0
			a = a.shl(1)
		}
		return eval(zWires)
	}

	fun swap(g1: Gate, g2: Gate) {
		val tmp = g1.out
		g1.out = g2.out
		g2.out = tmp
		g1.out.fromGate = g1
		g2.out.fromGate = g2
	}

	// @return Whether cycle detected
	fun clearValueRecurs(gate: Gate, path: MutableSet<Gate> = mutableSetOf()): Boolean {
		// Cycle detection
		if (gate in path) return true
		val wire = gate.out
		wire.value = -1
		path.add(gate)
		for (g in wire.toGates) {
			val res = clearValueRecurs(g, path)
			if (res) return true
		}
		path.remove(gate)
		return false
	}

	fun findParentsRecurs(gate: Gate?, path: MutableSet<Gate> = mutableSetOf()): List<Gate> {
		if (gate == null) return emptyList()
		// Cycle detection
		if (gate in path) return emptyList()
		val g1 = gate.in1.fromGate
		val g2 = gate.in2.fromGate
		path.add(gate)
		val res1 = findParentsRecurs(g1, path)
		val res2 = findParentsRecurs(g2, path)
		path.remove(gate)
		return listOf(gate) + res1 + res2
	}

	fun getXYCounts(i: Int, wires: List<Wire>): Pair<List<Int>, List<Int>> {
		val n = i.toString().padStart(2, '0')
		val zn = "z$n"
		val wz = wires.first { it.name == zn }
		val gz = wz.fromGate!!
		val xWires = findParentsRecurs(gz).asSequence().map { listOf(it.in1.name, it.in2.name) }.flatten()
			.filter { it[0] == 'x' }.map { it.drop(1).toInt() }.sorted().toList()
		val xCounts = mutableMapOf<Int, Int>()
		for (w in xWires) xCounts[w] = (xCounts[w] ?: 0) + 1
		val yWires = findParentsRecurs(gz).asSequence().map { listOf(it.in1.name, it.in2.name) }.flatten()
			.filter { it[0] == 'y' }.map { it.drop(1).toInt() }.sorted().toList()
		val yCounts = mutableMapOf<Int, Int>()
		for (w in yWires) yCounts[w] = (yCounts[w] ?: 0) + 1
		return xCounts.values.toList() to yCounts.values.toList()
	}

	fun isGoodCounts(counts: List<Int>): Boolean {
		return counts.size > 1 &&
				counts[0] == 1 &&
				counts.subList(1, counts.size - 1).all { it == 2 } &&
				counts[counts.size - 1] == 1
	}

	fun findSwap(wires: List<Wire>, zeroBit: Int, candidateGates: List<Gate>, swapped: MutableList<Gate>): Boolean {
		var swapFound = false
		val zeroWire = wires.first { it.name[0] == 'z' && it.name.drop(1).toInt() == zeroBit }
		for ((i1, g1) in candidateGates.withIndex()) {
			for (i2 in i1 + 1 until candidateGates.size) {
				val g2 = candidateGates[i2]
				if (g1 === g2) continue
				swap(g1, g2)
				val isCycle1 = clearValueRecurs(g1)
				if (isCycle1) {
					swap(g1, g2)
					continue
				}
				val isCycle2 = clearValueRecurs(g2)
				if (isCycle2) {
					swap(g1, g2)
					continue
				}
				val v = zeroWire.value()
				if (v == 1) {
					val (xCounts, yCounts) = getXYCounts(zeroBit, wires)
					if (isGoodCounts(xCounts) && isGoodCounts(yCounts)) {
						swapFound = true
						swapped.add(g1)
						swapped.add(g2)
						break
					}
				}
				swap(g2, g1)
				clearValueRecurs(g1)
				clearValueRecurs(g2)
			}
			if (swapFound) break
		}
		return swapFound
	}

	fun findSwaps(wires: List<Wire>, zWires: List<Wire>, gates: List<Gate>): List<Gate> {
		val swapped = mutableListOf<Gate>()
		while (swapped.size < 4 * 2) {
			val bits = zWires.map { it.value() }
			// Find zero bit to fix it by swap
			val zeroBit = bits.indexOfFirst { it != 1 }
			// Gates with correct outs
			val goodGates = mutableSetOf<Gate>()
			for (i in 0 until zeroBit) {
				val wz = wires.first { it.name[0] == 'z' && it.name.drop(1).toInt() == i }
				val gz = wz.fromGate!!
				goodGates.addAll(findParentsRecurs(gz))
			}
			// Candidates for swap
			val candidateGates = mutableListOf<Gate>()
			for (gate in gates.toSet() - goodGates) {
				val parents = findParentsRecurs(gate)
				val xyWires = parents.map { listOf(it.in1.name, it.in2.name) }.flatten()
					.filter { it[0] == 'x' || it[0] == 'y' }
					.map { it.drop(1).toInt() }
				if (xyWires.all { it <= zeroBit }) candidateGates.add(gate)
			}
			// Find good swap
			val swapFound = findSwap(wires, zeroBit, candidateGates, swapped)
			if (!swapFound) error("Can't find swap for bit: $zeroBit")
		}
		return swapped
	}

	fun part1(input: List<String>): Long {
		val (wires, _) = parse(input)
		val zWires = wires.filter { it.name[0] == 'z' }.sortedBy { it.name }
		return eval(zWires)
	}

	fun part2(input: List<String>): String {
		val (wires, gates) = parse(input)
		val xWires = wires.filter { it.name[0] == 'x' }.sortedBy { it.name }
		val yWires = wires.filter { it.name[0] == 'y' }.sortedBy { it.name }
		val zWires = wires.filter { it.name[0] == 'z' }.sortedBy { it.name }
		// Set test values
		// x =  111...1
		// y =  000...0
		// z = 0111...1
		for (i in xWires.indices) {
			xWires[i].value = 1
			yWires[i].value = 0
		}
		val swapped = findSwaps(wires, zWires, gates)
		// Test sum by random values
		val max = 1L.shl(xWires.size) - 1
		for (i in 1..100) {
			val x = Random.nextLong(max + 1)
			val y = Random.nextLong(max + 1)
			val z = calculate(x, y, gates, xWires, yWires, zWires)
			if (z != x + y) error("Wrong result: $x + $y = $z (${x + y})")
		}
		return swapped.map { it.out.name }.sorted().joinToString(",")
	}

	val testInput = readInput("Day24_test")
	val testInput2 = readInput("Day24_test2")
	check(part1(testInput) == 4L)
	check(part1(testInput2) == 2024L)

	val input = readInput("Day24")
	part1(input).println() // 52956035802096
	part2(input).println() // hnv,hth,kfm,tqr,vmv,z07,z20,z28
}

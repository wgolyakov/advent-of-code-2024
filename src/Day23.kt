fun main() {
	fun parse(input: List<String>): Map<String, Set<String>> {
		val graph = mutableMapOf<String, MutableSet<String>>()
		for (line in input) {
			val (comp1, comp2) = line.split('-')
			graph.getOrPut(comp1) { mutableSetOf() }.add(comp2)
			graph.getOrPut(comp2) { mutableSetOf() }.add(comp1)
		}
		return graph
	}

	fun part1(input: List<String>): Int {
		val graph = parse(input)
		val threes = mutableSetOf<Set<String>>()
		for ((comp1, links1) in graph) {
			if (links1.size < 2) continue
			for (comp2 in links1) {
				val links2 = graph[comp2]!!
				if (links2.size < 2) continue
				for (comp3 in links2) {
					if (comp3 == comp1) continue
					val links3 = graph[comp3]!!
					if (links3.size < 2) continue
					if (comp1 in links3) threes.add(setOf(comp1, comp2, comp3))
				}
			}
		}
		return threes.count { three -> three.any { it[0] == 't' } }
	}

	fun part2(input: List<String>): String {
		val graph = parse(input)
		val groups = mutableListOf<MutableSet<String>>()
		for ((comp, links) in graph) {
			val compGroups = groups.filter { comp in it }
			for (neighbor in links) {
				if (compGroups.any { neighbor in it }) continue
				val nLinks = graph[neighbor]!!
				val cnGroups = compGroups.filter { gr -> gr.all { it in nLinks } }
				if (cnGroups.isEmpty()) {
					groups.add(mutableSetOf(comp, neighbor))
				} else {
					for (gr in cnGroups) gr.add(neighbor)
				}
			}
		}
		val lanParty = groups.maxBy { it.size }
		return lanParty.sorted().joinToString(",")
	}

	val testInput = readInput("Day23_test")
	check(part1(testInput) == 7)
	check(part2(testInput) == "co,de,ka,ta")

	val input = readInput("Day23")
	part1(input).println() // 1064
	part2(input).println() // aq,cc,ea,gc,jo,od,pa,rg,rv,ub,ul,vr,yy
}

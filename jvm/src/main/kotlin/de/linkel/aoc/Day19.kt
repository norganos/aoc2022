package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton

@Singleton
class Day19(): AbstractLinesAdventDay<Day19.Result>() {
    override val day = 19

    override fun process(lines: Sequence<String>): Result {
        val bluePrints = lines
            .filter { it.isNotBlank() }
            .map { line ->
                val id = line.substringAfter("Blueprint ").substringBefore(":").toInt()
                val robots = line.substringAfter(": ")
                    .split(".")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .associate { robotLine ->
                        val resource = Resource.valueOf(robotLine.substringAfter("Each ").substringBefore(" robot").uppercase())
                        val costs = robotLine.substringAfter(" costs ")
                            .split(" and ")
                            .associate { costLine ->
                                Resource.valueOf(costLine.substringAfter(" ").uppercase()) to costLine.substringBefore(" ").toInt()
                            }
                        resource to Resources(costs)
                    }
                BluePrint(id, robots)
            }
            .toList()


//        test(bluePrints)

        val startRobots = Resources(mapOf(Resource.ORE to 1))
        val minutes = 24
        val plans = bluePrints.map { b ->
//            val firstResourceInRound = mutableMapOf<Resource, Int>()
//            firstResourceInRound[Resource.ORE] = 1
//            while (Resource.values().any { it !in firstResourceInRound }) {
//                Resource.values().forEach { r ->
//                    if (r !in firstResourceInRound) {
//                        if (b.blueprints[r]!!.amount.all { a -> a.key in firstResourceInRound }) {
//                            firstResourceInRound[r] = b.blueprints[r]!!.amount.entries.maxOf { d -> firstResourceInRound[d.key]!! + d.value / 4 + 1 }
//                        }
//                    }
//                }
//            }
            val latestRoundForResource = mutableMapOf<Resource, Int>()
            latestRoundForResource[Resource.GEODE] = minutes - 1
            latestRoundForResource[Resource.OBSIDIAN] = latestRoundForResource[Resource.GEODE]!! - getMinRoundsToEarn(b.blueprints[Resource.GEODE]!![Resource.OBSIDIAN])
            latestRoundForResource[Resource.CLAY] = latestRoundForResource[Resource.OBSIDIAN]!! - getMinRoundsToEarn(b.blueprints[Resource.OBSIDIAN]!![Resource.CLAY])
//
//            val bestAmountOfRobots = mutableMapOf<Resource, Int>()
//            bestAmountOfRobots[Resource.GEODE] = (minutes - firstResourceInRound[Resource.GEODE]!!) / b.blueprints[Resource.GEODE]!!.max
//            bestAmountOfRobots[Resource.OBSIDIAN] = (minutes - firstResourceInRound[Resource.OBSIDIAN]!!) / b.blueprints[Resource.OBSIDIAN]!!.max
//            bestAmountOfRobots[Resource.CLAY] = (minutes - firstResourceInRound[Resource.CLAY]!!) / b.blueprints[Resource.CLAY]!!.max
//            bestAmountOfRobots[Resource.ORE] = (minutes - firstResourceInRound[Resource.CLAY]!!) / b.blueprints[Resource.CLAY]!!.max

//            Pair(b.id, 24 - firstResourceInRound[Resource.GEODE]!!)

            val log = Log(maxDepth = 24, blueprints = b.blueprints, robots = startRobots, latestRoundForResource = latestRoundForResource)
            val best = SolutionHolder()
            dfs(log, best)
            Pair(b.id, best.geodes)
        }

        return Result(plans.sumOf { it.first * it.second })
    }

//    fun test(bluePrints: List<BluePrint>) {
//        val minutes = 24
//        val startRobots = Resources(mapOf(Resource.ORE to 1))
//        val b = bluePrints.find { it.id == 1 }!!
//        val latestRoundForResource = mutableMapOf<Resource, Int>()
//        latestRoundForResource[Resource.GEODE] = minutes - 1
//        latestRoundForResource[Resource.OBSIDIAN] = latestRoundForResource[Resource.GEODE]!! - getMinRoundsToEarn(b.blueprints[Resource.GEODE]!![Resource.OBSIDIAN])
//        latestRoundForResource[Resource.CLAY] = latestRoundForResource[Resource.OBSIDIAN]!! - getMinRoundsToEarn(b.blueprints[Resource.OBSIDIAN]!![Resource.CLAY])
//        val example = Log(maxDepth = 24, blueprints = b.blueprints, robots = startRobots, latestRoundForResource = latestRoundForResource)
//            .nextRound(null)        // 1
//            .nextRound(null)        // 2
//            .nextRound(Resource.CLAY)     // 3
//            .nextRound(null)        // 4
//            .nextRound(Resource.CLAY)     // 5
//            .nextRound(null)        // 6
//            .nextRound(Resource.CLAY)     // 7
//            .nextRound(null)        // 8
//            .nextRound(null)        // 9
//            .nextRound(null)        // 10
//            .nextRound(Resource.OBSIDIAN) // 11
//            .nextRound(Resource.CLAY)     // 12
//            .nextRound(null)        // 13
//            .nextRound(null)        // 14
//            .nextRound(Resource.OBSIDIAN) // 15
//            .nextRound(null)        // 16
//            .nextRound(null)        // 17
//            .nextRound(Resource.GEODE)    // 18
//            .nextRound(null)        // 19
//            .nextRound(null)        // 20
//            .nextRound(Resource.GEODE)    // 21
//            .nextRound(null)        // 22
//            .nextRound(null)        // 23
//            .nextRound(null)        // 24
//            assert(example.inventory[Resource.GEODE] == 9)
//            assert(example.inventory[Resource.OBSIDIAN] == 8)
//            assert(example.inventory[Resource.CLAY] == 41)
//            assert(example.inventory[Resource.ORE] == 6)
//    }

    private fun getMinRoundsToEarn(amount: Int): Int {
        return if (amount == 0) 0
        else if (amount <= 1) 2
        else if (amount <= 3) 3
        else if (amount <= 6) 4
        else if (amount <= 10) 5
        else if (amount <= 15) 6
        else if (amount <= 21) 7
        else if (amount <= 28) 8
        else if (amount <= 36) 9
        else 10
    }

    private fun dfs(
        log: Log,
        best: SolutionHolder,
        buildBlacklist: Resource? = null
    ) {
        if (log.dead) {
            return
        }
        if (log.done) {
            if (best.geodes < log.inventory[Resource.GEODE]) {
                best.geodes = log.inventory[Resource.GEODE]
            }
        } else {
            val buildPossibilities = log.getPossibleBuilds().filter { it != buildBlacklist }
            for (robot in buildPossibilities) {
                dfs(log.nextRound(robot), best)
            }
            if (buildPossibilities.size == 1) {
                dfs(log.nextRound(null), best, buildPossibilities.first())
            } else {
                dfs(log.nextRound(null), best)
            }
        }
    }

    class SolutionHolder {
        var geodes = 0
    }

    data class BluePrint(
        val id: Int,
        val blueprints: Map<Resource, Resources>,
    ) {

    }

    data class Log(
        val maxDepth: Int,
        val blueprints: Map<Resource, Resources>,
        val robots: Resources,
        val latestRoundForResource: Map<Resource, Int>,
        val inventory: Resources = Resources.ZERO,
        val round: Int = 1
    ) {
        val maximumRequirement = Resource.values()
            .associateWith { r ->
                if (r == Resource.GEODE) maxDepth
                else blueprints.entries.maxOf { it.value[r] }
            }
        val done = round > maxDepth
        val dead = latestRoundForResource.entries.any { it.value < round && robots[it.key] == 0 }

        fun getPossibleBuilds(): Collection<Resource> {
            return if (round == maxDepth)
                emptyList()
            else if (blueprints[Resource.GEODE]!! in inventory)
                listOf(Resource.GEODE)
            else blueprints
                .filter {
                    it.value in inventory
                }
                .filter {
                    inventory[it.key] <= maximumRequirement[it.key]!! + maximumRequirement[it.key]!! - robots[it.key]
                }
                .filter {
                    robots[it.key] <= maximumRequirement[it.key]!!
                }
                .map {
                    it.key
                }
                .sortedByDescending { it.ordinal }
        }
        fun nextRound(robot: Resource?): Log {
            if (round > maxDepth) {
                throw IllegalArgumentException("time is over")
            }
            if (robot != null && blueprints[robot]!! !in inventory) {
                throw IllegalArgumentException("can't afford $robot robot right now")
            }
            val nextInventory = inventory - (blueprints[robot] ?: Resources.ZERO) + robots
            val nextRobots = if (robot != null) robots + robot else robots
            return copy(
                inventory = nextInventory,
                robots = nextRobots,
                round = round + 1
            )
        }
    }

    data class Resources(
        val amount: Map<Resource, Int>
    ) {
        val max = amount.entries.maxOfOrNull { it.value } ?: 0
        companion object {
            val ZERO = Resources(emptyMap())
        }
        operator fun get(item: Resource): Int = amount[item] ?: 0
        operator fun plus(other: Resources): Resources {
            return copy(
                amount = Resource.values()
                    .associateWith { r -> (amount[r] ?: 0) + (other.amount[r] ?: 0) }
                    .filterValues { it > 0 }
            )
        }
        operator fun plus(robot: Resource): Resources {
            return copy(
                amount = Resource.values()
                    .associateWith { r -> (amount[r] ?: 0) + if (r == robot) 1 else 0 }
                    .filterValues { it > 0 }
            )
        }
        operator fun minus(other: Resources): Resources {
            return copy(
                amount = Resource.values()
                    .associateWith { r -> (amount[r] ?: 0) - (other.amount[r] ?: 0) }
                    .filterValues { it > 0 }
            )
        }
        operator fun contains(other: Resources): Boolean {
            return Resource.values()
                .all { r ->
                    (other.amount[r] ?: 0) <= (amount[r] ?: 0)
                }
        }
    }
    enum class Resource { ORE, CLAY, OBSIDIAN, GEODE }

//    data class RobotCosts(
//        val ore: Resources,
//        val clay: Resources,
//        val obdidian: Resources,
//        val geode: Resources,
//    )

//    data class Resources(
//        val ore: Int = 0,
//        val clay: Int = 0,
//        val obsidian: Int = 0,
//        val geode: Int = 0
//    ) {
//        operator fun plus(other: Resources): Resources {
//            return copy(
//                ore = ore + other.ore,
//                clay = clay + other.clay,
//                obsidian = obsidian + other.obsidian,
//                geode = geode + other.geode,
//            )
//        }
//        operator fun minus(other: Resources): Resources {
//            return copy(
//                ore = ore - other.ore,
//                clay = clay - other.clay,
//                obsidian = obsidian - other.obsidian,
//                geode = geode - other.geode,
//            )
//        }
//    }

    data class Result(
        val qualityLevels: Int
    ) {
        override fun toString(): String {
            return "sum of quality levels: $qualityLevels"
        }
    }
}

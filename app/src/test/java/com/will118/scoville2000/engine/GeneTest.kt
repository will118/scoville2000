package com.will118.scoville2000.engine

import org.junit.Assert
import org.junit.Test

class GeneTest {
    @Test
    fun `flip n bits`() {
        val gene = Gene.withOneBits(bitCount = 24)

        Assert.assertEquals(24, gene.popCount())
    }

    @Test
    fun `genes give reasonable scoville values`() {
        val gene = Gene.withOneBits(bitCount = 4)

        val plantType = PlantType(
            displayName = "",
            phases = Phases.DEFAULT,
            chromosome = Chromosome(
                scovilleCount = gene,
            ),
            id = 11,
        )

        Assert.assertEquals(16_000, plantType.scovilles.count)
    }

    @Test
    fun `mutate toggles`() {
        val gene = Gene(
            lo = (1UL).shl(3), // bit 3 is on
            hi = 0b0.toULong(), // bit 3 is off
        )

        val mutated = gene.mutate(3) // toggle bit 3

        Assert.assertEquals(0b0.toULong(), mutated.lo) // should be off now
        Assert.assertEquals((1UL).shl(3), mutated.hi) // should be on now
    }
}
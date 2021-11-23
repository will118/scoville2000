package com.will118.scoville2000.engine

import org.junit.Assert.assertEquals
import org.junit.Test

class GeneTests {
    @Test
    fun `flip n bits`() {
        val gene = Gene(
            lsb = flipBits(20),
            msb = flipBits(4),
        )

        assertEquals(24, gene.popCount())
    }

    @Test
    fun `genes give reasonable scoville values`() {
        val gene = Gene(
            lsb = flipBits(2),
            msb = flipBits(2),
        )

        val plantType = PlantType(
            displayName = "",
            phases = Phases.DEFAULT,
            chromosome = Chromosome(
                scovilleCount = gene,
            ),
            cost = null,
        )

        assertEquals(16_000, plantType.scovilles.count)
    }
}
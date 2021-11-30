package com.will118.scoville2000.engine

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class GeneticsNameCrossKtTest {
    @Test
    fun `scovilles changes pepper name`() {
        val random = Random(5)

        nameCross(
            Chromosome(scovilleCount = Gene.withOneBits(bitCount = 5, random = random))
        ).also {
            assertEquals("Spotted Pepper", it)
        }

        nameCross(
            Chromosome(scovilleCount = Gene.withOneBits(bitCount = 12, random = random))
        ).also {
            assertEquals("Spotted Wiggler", it)
        }

        nameCross(
            Chromosome(scovilleCount = Gene.withOneBits(bitCount = 30, random = random))
        ).also {
            assertEquals("Spotted Wiggler", it)
        }

        nameCross(
            Chromosome(scovilleCount = Gene.withOneBits(bitCount = 70, random = random))
        ).also {
            assertEquals("Warm Scorcher", it)
        }

        nameCross(
            Chromosome(scovilleCount = Gene.withOneBits(bitCount = 128, random = random))
        ).also {
            assertEquals("Spicy Dragon", it)
        }
    }


    @Test
    fun `other stats change adjective`() {
        val random = Random(5)

        nameCross(
            Chromosome(scovilleCount = Gene.withOneBits(bitCount = 5, random = random))
        ).also {
            assertEquals("Spotted Pepper", it)
        }

        nameCross(
            Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 5, random = random),
                growthDuration = Gene.withOneBits(bitCount = 50, random = random)
            )
        ).also {
            assertEquals("Warm Pepper", it)
        }
    }
}
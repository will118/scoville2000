package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import kotlin.random.Random

// Based on: https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/src/kotlin/random/XorWowRandom.kt
/**
 * Random number generator, using Marsaglia's "xorwow" algorithm
 * Cycles after 2^192 - 2^32 repetitions.
 * For more details, see Marsaglia, George (July 2003). "Xorshift RNGs". Journal of Statistical Software. 8 (14). doi:10.18637/jss.v008.i14
 * Available at https://www.jstatsoft.org/v08/i14/paper
 */
@Serializable
data class SerializableRandom(
    private var x: Int,
    private var y: Int,
    private var z: Int,
    private var w: Int,
    private var v: Int,
    private var addend: Int
){
    init {
        // some trivial seeds can produce several values with zeroes in upper bits, so we discard first 64
        repeat(64) { nextInt() }
    }

    companion object {
        fun fromSeed(seed: Int = Random.Default.nextInt()) = SerializableRandom(
            x = seed,
            y = seed.shr(31),
            z = 0,
            w = 0,
            v = seed.inv(),
            addend = (seed shl 10) xor (seed.shr(31) ushr 4)
        )

        private fun fastLog2(value: Int): Int = 31 - value.countLeadingZeroBits()

        private fun Int.takeUpperBits(bitCount: Int): Int =
            this.ushr(32 - bitCount) and (-bitCount).shr(31)
    }


    fun nextInt(from: Int, until: Int): Int {
        val n = until - from
        if (n > 0 || n == Int.MIN_VALUE) {
            val rnd = if (n and -n == n) {
                val bitCount = fastLog2(n)
                nextBits(bitCount)
            } else {
                var v: Int
                do {
                    val bits = nextInt().ushr(1)
                    v = bits % n
                } while (bits - v + (n - 1) < 0)
                v
            }
            return from + rnd
        } else {
            while (true) {
                val rnd = nextInt()
                if (rnd in from until until) return rnd
            }
        }
    }

    fun nextInt(): Int {
        // Equivalent to the xorxow algorithm
        // From Marsaglia, G. 2003. Xorshift RNGs. J. Statis. Soft. 8, 14, p. 5
        var t = x
        t = t xor (t ushr 2)
        x = y
        y = z
        z = w
        val v0 = v
        w = v0
        t = (t xor (t shl 1)) xor v0 xor (v0 shl 4)
        v = t
        addend += 362437
        return t + addend
    }

    private fun nextBits(bitCount: Int): Int = nextInt().takeUpperBits(bitCount)
}
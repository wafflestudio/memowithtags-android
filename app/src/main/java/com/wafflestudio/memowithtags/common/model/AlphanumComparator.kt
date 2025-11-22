package com.wafflestudio.memowithtags.common.model

class AlphanumComparator : Comparator<String> {
    override fun compare(s1: String, s2: String): Int {
        val thisChunks = s1.chunked()
        val thatChunks = s2.chunked()
        val maxLength = maxOf(thisChunks.size, thatChunks.size)

        for (i in 0 until maxLength) {
            val thisChunk = thisChunks.getOrNull(i) ?: ""
            val thatChunk = thatChunks.getOrNull(i) ?: ""

            val result = compareChunk(thisChunk, thatChunk)
            if (result != 0) return result
        }
        return 0
    }

    private fun compareChunk(c1: String, c2: String): Int {
        val isDigit1 = c1.all { it.isDigit() }
        val isDigit2 = c2.all { it.isDigit() }

        return if (isDigit1 && isDigit2 && c1.isNotEmpty() && c2.isNotEmpty()) {
            c1.toBigInteger().compareTo(c2.toBigInteger())
        } else {
            c1.compareTo(c2)
        }
    }

    private fun String.chunked(): List<String> {
        val result = mutableListOf<String>()
        var current = ""
        var lastIsDigit: Boolean? = null

        for (ch in this) {
            val isDigit = ch.isDigit()
            if (lastIsDigit != null && isDigit != lastIsDigit) {
                result.add(current)
                current = ""
            }
            current += ch
            lastIsDigit = isDigit
        }
        if (current.isNotEmpty()) result.add(current)
        return result
    }
}

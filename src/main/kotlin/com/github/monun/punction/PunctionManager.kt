package com.github.monun.punction

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedMap
import java.io.File
import java.util.*
import java.util.logging.Logger

class PunctionManager(
    val logger: Logger,
    val folder: File
) {
    lateinit var punctions: Map<String, Punction>
        private set

    internal fun load() {
        folder.mkdirs()
        val punctions = TreeMap<String, Punction>(naturalOrder())

        folder.listFiles { _, name -> name.endsWith(".punction") }?.forEach { file ->
            val name = file.nameWithoutExtension
            val commands = file.readLines().asSequence().mapNotNull { command ->
                command.trim().takeIf { it.isNotEmpty() }
            }.toList()
            punctions[name] = Punction(ImmutableList.copyOf(commands))
        }

        this.punctions = ImmutableSortedMap.copyOf(punctions)

        for (key in punctions.keys) {
            logger.info(" - $key")
        }
    }

    fun punction(name: String): Punction? {
        return punctions[name]
    }
}
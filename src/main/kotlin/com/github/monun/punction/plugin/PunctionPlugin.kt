package com.github.monun.punction.plugin

import com.github.monun.punction.Punction
import com.github.monun.punction.PunctionManager
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.*
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Monun
 */
class PunctionPlugin : JavaPlugin() {

    private lateinit var punctionManager: PunctionManager

    override fun onEnable() {
        punctionManager = PunctionManager(logger, dataFolder).apply { load() }
        setupKommands()
    }

    private fun setupKommands() = kommand {
        register("punction") {
            val punctionArgument = dynamic { _, input ->
                punctionManager.punction(input)
            }.apply {
                suggests(punctionManager.punctions::values, { it.name }) {
                    val commands = it.commands

                    if (commands.isEmpty()) empty()
                    else {
                        val builder = text()
                        val iterator = commands.iterator()

                        while (true) {
                            builder.append(text().content(iterator.next())); if (!iterator.hasNext()) break
                            builder.append(newline())
                        }

                        builder
                    }
                }
            }

            then("punction" to punctionArgument) {
                executes {
                    val punction: Punction by it
                    val server = server
                    for (command in punction.commands) {
                        broadcast(text().content("${sender.name}: $command"))
                        server.dispatchCommand(sender, command)
                    }
                }
            }
        }
        register("punction-reload") {
            executes {
                punctionManager.load()
                broadcast(text().content("Punction reload completed."))
            }
        }
    }
}
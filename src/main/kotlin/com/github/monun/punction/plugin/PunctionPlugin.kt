package com.github.monun.punction.plugin

import com.github.monun.kommand.KommandContext
import com.github.monun.kommand.argument.KommandArgument
import com.github.monun.kommand.argument.suggestions
import com.github.monun.kommand.kommand
import com.github.monun.kommand.sendFeedback
import com.github.monun.punction.Punction
import com.github.monun.punction.PunctionManager
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
            then("name" to PunctionArgument()) {
                executes {
                    val sender = it.sender
                    val punction = it.parseArgument<Punction>("name")
                    val server = server
                    for (command in punction.commands) {
                        server.dispatchCommand(sender, command)
                    }
                }
            }
        }
        register("punction-reload") {
            executes {
                punctionManager.load()
                it.sender.sendFeedback("Punction reload completed.")
            }
        }
    }

    inner class PunctionArgument: KommandArgument<Punction> {
        override fun parse(context: KommandContext, param: String): Punction? {
            return punctionManager.punction(param)
        }

        override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
            return punctionManager.punctions.keys.suggestions(target)
        }
    }
}
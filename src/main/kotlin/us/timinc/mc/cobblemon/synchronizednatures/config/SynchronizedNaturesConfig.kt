package us.timinc.mc.cobblemon.synchronizednatures.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment
import us.timinc.mc.cobblemon.synchronizednatures.SynchronizedNatures

@Config(name = SynchronizedNatures.MOD_ID)
class SynchronizedNaturesConfig : ConfigData {
    @Comment("Whether or not the Pokemon with synchronize must be the first in your party in order to be considered")
    val mustBeFirst = true
    @Comment("The distance at which a spawning Pokemon takes a player into consideration")
    val effectiveRange = 64
    @Comment("How many good marbles?")
    val chance = 1
    @Comment("How many marbles?")
    val marbles = 2
}
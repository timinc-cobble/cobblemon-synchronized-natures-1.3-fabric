package us.timinc.mc.cobblemon.synchronizednatures

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.pokemon.Nature
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ModInitializer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.synchronizednatures.config.SynchronizedNaturesConfig
import kotlin.random.Random

object SynchronizedNatures : ModInitializer {
    const val MOD_ID = "synchronized_natures"
    private lateinit var synchronizedNaturesConfig: SynchronizedNaturesConfig

    override fun onInitialize() {
        AutoConfig.register(
            SynchronizedNaturesConfig::class.java,
            ::JanksonConfigSerializer
        )
        synchronizedNaturesConfig = AutoConfig.getConfigHolder(SynchronizedNaturesConfig::class.java)
            .config
    }

    private fun getSynchronizedNature(player: ServerPlayer): Nature? {
        val playerPartyStore = Cobblemon.storage.getParty(player)
        if (synchronizedNaturesConfig.mustBeFirst) {
            val firstPartyMember = playerPartyStore.firstOrNull()
            if (firstPartyMember?.ability?.name != "synchronize") {
                return null
            }
            return firstPartyMember.nature
        }

        return playerPartyStore.find { it.ability.name == "synchronize" }?.nature
    }

    fun possiblyChangeNature(ctx: SpawningContext, props: PokemonProperties) {
        val world = ctx.world
        if (Random.Default.nextInt(synchronizedNaturesConfig.marbles) >= synchronizedNaturesConfig.chance) {
            return
        }

        val naturePool = world.getNearbyPlayers(
            TargetingConditions.forNonCombat()
                .ignoreLineOfSight()
                .ignoreInvisibilityTesting(),
            null,
            AABB.ofSize(
                Vec3.atCenterOf(ctx.position),
                synchronizedNaturesConfig.effectiveRange.toDouble(),
                synchronizedNaturesConfig.effectiveRange.toDouble(),
                synchronizedNaturesConfig.effectiveRange.toDouble()
            )
        ).mapNotNull { (it as? ServerPlayer)?.let { serverPlayer -> getSynchronizedNature(serverPlayer) } }
        if (naturePool.isEmpty()) {
            return
        }
        val pickedNature = naturePool[Random.Default.nextInt(naturePool.size)]
        props.nature = pickedNature.name.path
    }
}
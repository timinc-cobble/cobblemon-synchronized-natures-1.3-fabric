package us.timinc.mc.cobblemon.synchronizednatures

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.pokemon.Nature
import net.fabricmc.api.ModInitializer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

object SynchronizedNatures : ModInitializer {
    const val MOD_ID = "synchronized_natures"

    override fun onInitialize() {}

    fun getSynchronizedNature(player: ServerPlayer): Nature? {
        val playerPartyStore = Cobblemon.storage.getParty(player)
        val synchronizedPartyMember =
            playerPartyStore.find { it.ability.name == "synchronize" }
        return synchronizedPartyMember?.nature
    }

    fun possiblyChangeNature(ctx: SpawningContext, props: PokemonProperties) {
        val world = ctx.world
        val naturePool = world.getNearbyPlayers(
            TargetingConditions.forNonCombat()
                .ignoreLineOfSight()
                .ignoreInvisibilityTesting(),
            null,
            AABB.ofSize(
                Vec3.atCenterOf(ctx.position),
                64.0,
                64.0,
                64.0
            )
        ).mapNotNull { getSynchronizedNature(it as ServerPlayer) }
        println(naturePool.size)
        if (naturePool.isEmpty()) {
            return
        }
        val pickedNature = naturePool[Random.Default.nextInt(naturePool.size)]
        props.nature = pickedNature.name.path
    }
}
package uwu.nekorise.reCape.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
import com.github.retrooper.packetevents.protocol.player.PlayerModelType;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uwu.nekorise.reCape.ReCape;
import uwu.nekorise.reCape.mannequin.MannequinRegistry;
import uwu.nekorise.reCape.mannequin.MannequinSession;
import uwu.nekorise.reCape.util.PoseNaming;

import java.util.List;

public class MannequinPacketSender {
    public int getTask(Player owner)
    {
        return new BukkitRunnable() {
            @Override
            public void run() {
                MannequinSession mannequinSession = MannequinRegistry.getByUsername(owner.getName());
                if (mannequinSession == null) return;

                Mannequin mannequin = mannequinSession.getMannequin();
                if (!owner.isValid() || !mannequin.isValid()) {
                    return;
                }

                ItemProfile profileForAll = getItemProfile(mannequinSession, false);
                ItemProfile profileForOwner = getItemProfile(mannequinSession, true);

                List<EntityData<?>> metadataForAll = List.of(
                        new EntityData<>(
                                6,
                                EntityDataTypes.ENTITY_POSE,
                                EntityPose.valueOf(
                                        PoseNaming.getNamingForPacketEvents(owner.getPose().toString())
                                )
                        ),
                        new EntityData<>(
                                17,
                                EntityDataTypes.RESOLVABLE_PROFILE,
                                profileForAll
                        )
                );

                List<EntityData<?>> metadataForOwner = List.of(
                        new EntityData<>(
                                6,
                                EntityDataTypes.ENTITY_POSE,
                                EntityPose.valueOf(
                                        PoseNaming.getNamingForPacketEvents(owner.getPose().toString())
                                )
                        )
                        ,
                        new EntityData<>(
                                17,
                                EntityDataTypes.RESOLVABLE_PROFILE,
                                profileForOwner
                        )
                );

                WrapperPlayServerEntityMetadata packetForAll =
                        new WrapperPlayServerEntityMetadata(
                                mannequin.getEntityId(),
                                metadataForAll
                        );

                WrapperPlayServerEntityMetadata packetForOwner =
                        new WrapperPlayServerEntityMetadata(
                                mannequin.getEntityId(),
                                metadataForOwner
                        );

                PacketEvents.getAPI()
                        .getPlayerManager()
                        .sendPacket(owner, packetForOwner);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.equals(owner)) continue;

                    PacketEvents.getAPI()
                            .getPlayerManager()
                            .sendPacket(player, packetForAll);
                }

                WrapperPlayServerEntityMetadata disableCapePacket =
                        getWrapperPlayServerEntityMetadata();

                for (Player viewer : Bukkit.getOnlinePlayers()) {
                    PacketEvents.getAPI()
                            .getPlayerManager()
                            .sendPacket(viewer, disableCapePacket);
                }
            }

            private @NotNull WrapperPlayServerEntityMetadata getWrapperPlayServerEntityMetadata() {
                byte skinPartsWithoutCape = (byte) (
                                0x02 | // Jacket
                                0x04 | // Left sleeve
                                0x08 | // Right sleeve
                                0x10 | // Left pants leg
                                0x20 | // Right pants leg
                                0x40   // Hat
                );

                List<EntityData<?>> playerSkinMetadata = List.of(
                        new EntityData<>(
                                16,
                                EntityDataTypes.BYTE,
                                skinPartsWithoutCape
                        )
                );

                return new WrapperPlayServerEntityMetadata(
                        owner.getEntityId(),
                        playerSkinMetadata
                );
            }
        }.runTaskTimer(ReCape.getInstance(), 0L, 1L).getTaskId();
    }

    private static @NotNull ItemProfile getItemProfile(MannequinSession mannequinSession, boolean isOwner) {
        ResourceLocation capeTexture = getResourceLocation(mannequinSession, isOwner);

        ItemProfile newProfile = new ItemProfile(
                mannequinSession.getMannequin().getName(),
                mannequinSession.getMannequin().getUniqueId(),
                List.of(),
                null);
        ItemProfile.SkinPatch skinPatch = new ItemProfile.SkinPatch(
                new ResourceLocation("minecraft", "entity/player/trans"),
                capeTexture,
                null,
                PlayerModelType.SLIM
        );

        newProfile.setSkinPatch(skinPatch);
        return newProfile;
    }

    private static @Nullable ResourceLocation getResourceLocation(MannequinSession mannequinSession, boolean isOwner) {
        Pose pose = mannequinSession.getOwner().getPose();
        ResourceLocation capeTexture = null;
        String capeType = mannequinSession.getCapeType();
        Material chestplate = mannequinSession.getOwner().getEquipment().getChestplate().getType();
        switch (pose) {
            case SWIMMING -> {
                capeTexture = new ResourceLocation("minecraft", isOwner ? "entity/player/capes/" + capeType + "_swimming" : "entity/player/capes/3rd_" + capeType + "_swimming");
            }
            case SNEAKING -> {
                capeTexture = new ResourceLocation("minecraft", isOwner ? "entity/player/capes/" + capeType + "_sneaking" : "entity/player/capes/3rd_" + capeType + "_sneaking");
            }
            case DYING, SPIN_ATTACK, SLEEPING ->  {
                capeTexture = null;
            }
            default -> {
                capeTexture = new ResourceLocation("minecraft",isOwner ?  "entity/player/capes/" + capeType : "entity/player/capes/3rd_" + capeType);
            }
        }
        switch (chestplate) {
            case ELYTRA -> {
                capeTexture = null;
            }
            default -> {}
        }
        return capeTexture;
    }
}

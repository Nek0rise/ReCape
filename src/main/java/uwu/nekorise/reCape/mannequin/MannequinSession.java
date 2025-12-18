package uwu.nekorise.reCape.mannequin;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.ResolvableProfile.SkinPatch;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import uwu.nekorise.reCape.packet.MannequinPacketSender;

import java.awt.*;

@Getter
public class MannequinSession {
    private final Interaction interaction;
    private final Mannequin mannequin;
    private final Player owner;
    private final String capeType;
    private final int packetSenderTaskId;
    private final int serverUpdaterTaskId;

    public MannequinSession(Player owner, String capeType) {
        this.interaction = owner.getWorld().spawn(owner.getLocation(), Interaction.class, i -> {
            i.setInteractionHeight(25f);
            i.setInteractionWidth(0f);
            i.setResponsive(false);
        });
        this.mannequin = owner.getWorld().spawn(owner.getLocation(), Mannequin.class, m -> {
            m.setCollidable(false);
            m.setInvulnerable(true);
            m.setCanPickupItems(false);
            m.setSilent(true);
            m.setGravity(false);
            ResolvableProfile profile = ResolvableProfile.resolvableProfile()
                    .skinPatch(builder -> builder
                            .body(Key.key("minecraft", "entity/player/trans"))
                    )
                    .build();
            m.setProfile(profile);
        });
        this.owner = owner;
        this.capeType = capeType;
        interaction.addPassenger(mannequin);
        owner.addPassenger(interaction);

        this.packetSenderTaskId = new MannequinPacketSender().getTask(owner);
        this.serverUpdaterTaskId = new MannequinServerUpdater().getTask(owner, mannequin);
    }

    public void stopTask() {
        Bukkit.getScheduler().cancelTask(packetSenderTaskId);
        Bukkit.getScheduler().cancelTask(serverUpdaterTaskId);
    }
}

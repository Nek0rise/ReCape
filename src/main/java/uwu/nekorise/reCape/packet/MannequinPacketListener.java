package uwu.nekorise.reCape.packet;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.bukkit.entity.Player;
import uwu.nekorise.reCape.mannequin.MannequinRegistry;
import uwu.nekorise.reCape.mannequin.MannequinSession;

import java.util.List;

public class MannequinPacketListener implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) return;
        WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(event);

        int entityId = wrapper.getEntityId();

        MannequinSession interactionSession = MannequinRegistry.getByInteractionEntityId(entityId);
        Player receiver = event.getPlayer();
        List<EntityData<?>> metadata = wrapper.getEntityMetadata();

        if (interactionSession != null) {
            boolean isOwner = receiver.getName().equals(interactionSession.getOwner().getName());

            for (int i = 0; i < metadata.size(); i++) {
                EntityData data = metadata.get(i);
                if (data.getIndex() == 9 && data.getValue() instanceof Float) {
                    float height = isOwner ? 3.0f : 0.0f;
                    metadata.set(i, new EntityData(
                            data.getIndex(),
                            data.getType(),
                            height
                    ));
                }
            }
        }
    }
}
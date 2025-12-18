package uwu.nekorise.reCape.mannequin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MannequinRegistry {
    private static final Map<String, MannequinSession> registry = new ConcurrentHashMap<>();

    private MannequinRegistry() {}
    public static void register(MannequinSession session) {
        registry.put(session.getOwner().getName(), session);
    }

    public static void unregister(String ownerUsername) {
        registry.get(ownerUsername).stopTask();
        remove(ownerUsername);
        registry.remove(ownerUsername);
    }

    public static boolean isRegistered(String ownerUsername) {
        return registry.containsKey(ownerUsername);
    }

    public static MannequinSession getByUsername(String username) {
        return registry.get(username);
    }
    public static MannequinSession getByInteractionEntityId(int entityId) {
        for (MannequinSession session : registry.values()) {
            if (session.getInteraction().getEntityId() == entityId) {
                return session;
            }
        }
        return null;
    }
    public static MannequinSession getByMannequinEntityId(int entityId) {
        for (MannequinSession session : registry.values()) {
            if (session.getMannequin().getEntityId() == entityId) {
                return session;
            }
        }
        return null;
    }

    public static Collection<MannequinSession> getAllSessions() {
        return registry.values();
    }

    public static void remove(String ownerUsername) {
        for (MannequinSession session : registry.values()) {
            if (session.getOwner().getName().equals(ownerUsername)) {
                removeBySession(session);
            }
        }
    }

    public static void removeAll() {
        for (MannequinSession session : registry.values()) {
            removeBySession(session);
        }
        registry.clear();
    }

    private static void removeBySession(MannequinSession session) {
        Entity intercation = Bukkit.getEntity(session.getInteraction().getUniqueId());
        Entity mannequin = Bukkit.getEntity(session.getMannequin().getUniqueId());

        if (intercation != null && intercation.isValid()) { intercation.remove(); }
        if (mannequin != null && mannequin.isValid()) { mannequin.remove(); }
    }
}

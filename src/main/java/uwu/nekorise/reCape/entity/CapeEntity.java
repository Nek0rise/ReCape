package uwu.nekorise.reCape.entity;

import lombok.Getter;

@Getter
public class CapeEntity {
    private final int id;
    private final String username;
    private final String capeType;

    public CapeEntity(int id, String username, String capeType) {
        this.id = id;
        this.username = username;
        this.capeType = capeType;
    }

    public CapeEntity(String username, String capeType) {
        this(-1, username, capeType);
    }
}


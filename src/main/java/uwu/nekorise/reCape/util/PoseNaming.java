package uwu.nekorise.reCape.util;

public class PoseNaming {
    public static String getNamingForPacketEvents(String vanillaPose){
        switch (vanillaPose){
            case "SNEAKING":
                return "CROUCHING";
            default:
                return vanillaPose;
        }
    }
    public static String getNamingForVanilla(String packetEventsPose){
        switch (packetEventsPose){
            case "CROUCHING":
                return "SNEAKING";
            default:
                return packetEventsPose;
        }
    }
}

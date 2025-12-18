package uwu.nekorise.reCape.recourcepack;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import uwu.nekorise.reCape.ReCape;
import uwu.nekorise.reCape.config.LanguageDataStorage;
import uwu.nekorise.reCape.config.MainDataStorage;
import uwu.nekorise.reCape.util.Log;
import uwu.nekorise.reCape.util.color.MMessage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcepackBuilder {

    private final ReCape plugin = ReCape.getInstance();

    public void buildAsync(@Nullable CommandSender sender) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    File pluginDir = plugin.getDataFolder();
                    pluginDir.mkdirs();

                    File capesDir = new File(pluginDir, "capes");
                    boolean capesExisted = capesDir.exists();
                    capesDir.mkdirs();

                    if (!capesExisted) {
                        copyDefaultResource("fox.png", capesDir);
                        copyDefaultResource("heart.png", capesDir);
                        copyDefaultResource("readme.txt", capesDir);
                    }

                    List<String> capeNames = collectCapeNames(capesDir);
                    MainDataStorage.setCapes(capeNames);

                    Path tempDir = Files.createTempDirectory("resourcepack_temp");
                    Path resourcepackRoot = tempDir.resolve("resourcepack");

                    copyResourceFolder("resourcepack", resourcepackRoot);

                    Path targetCapesDir = resourcepackRoot
                            .resolve("assets/minecraft/textures/entity/player/capes");
                    Files.createDirectories(targetCapesDir);

                    File[] capeFiles = capesDir.listFiles((dir, name) ->
                            name.toLowerCase(Locale.ROOT).endsWith(".png")
                    );
                    if (capeFiles == null) return;

                    for (File cape : capeFiles) {
                        processCape(cape, targetCapesDir);
                    }

                    File zip = new File(pluginDir, "resourcepack.zip");
                    zipFolder(resourcepackRoot, zip);

                    deleteRecursive(tempDir.toFile());

                    Log.info(plugin,
                            MMessage.getText(
                                    MMessage.applyColor(LanguageDataStorage.getReCapeRepackSucc())
                            )
                    );

                    if (sender != null && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(
                                MMessage.applyColor(LanguageDataStorage.getReCapeRepackSucc())
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private String normalizeCapeName(String fileName) {
        return fileName
                .toLowerCase(Locale.ROOT)
                .replace(".png", "")
                .replaceAll("[ _\\-!]", "");
    }

    private void copyDefaultResource(String name, File capesDir) throws IOException {
        InputStream in = plugin.getResource("capes/" + name);
        if (in == null) {
            throw new IllegalStateException("Missing default cape in resources: " + name);
        }

        File outFile = new File(capesDir, name);
        Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        in.close();
    }

    private List<String> collectCapeNames(File capesDir) {
        List<String> result = new ArrayList<>();

        File[] files = capesDir.listFiles((dir, name) ->
                name.toLowerCase(Locale.ROOT).endsWith(".png")
        );
        if (files == null) return result;

        for (File file : files) {
            String normalizedName = normalizeCapeName(file.getName());
            result.add(normalizedName);
        }

        return result;
    }

    private void processCape(File original, Path outputDir) throws IOException {
        String baseName = normalizeCapeName(original.getName());

        BufferedImage image = ImageIO.read(original);

        createVariant(image, outputDir, baseName + ".png", new Color(255, 0, 255));
        createVariant(image, outputDir, baseName + "_sneaking.png", new Color(255, 255, 0));
        createVariant(image, outputDir, baseName + "_swimming.png", new Color(0, 255, 255));

        createVariant(image, outputDir, "3rd_" + baseName + ".png", new Color(127, 0, 127));
        createVariant(image, outputDir, "3rd_" + baseName + "_sneaking.png", new Color(127, 127, 0));
        createVariant(image, outputDir, "3rd_" + baseName + "_swimming.png", new Color(0, 127, 127));
    }

    private void createVariant(BufferedImage source, Path dir, String fileName, Color color) throws IOException {
        BufferedImage copy = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = copy.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();

        copy.setRGB(0, 0, color.getRGB());

        ImageIO.write(copy, "PNG", dir.resolve(fileName).toFile());
    }

    private void copyResourceFolder(String resourcePath, Path target) throws IOException {
        try (InputStream list = plugin.getResource(resourcePath + "/.list")) {
            if (list == null) {
                throw new IllegalStateException("Missing .list file in resourcepack");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(list));
            String line;

            while ((line = reader.readLine()) != null) {
                InputStream res = plugin.getResource(resourcePath + "/" + line);
                if (res == null) {
                    throw new IllegalStateException("Missing resource: " + resourcePath + "/" + line);
                }

                Path out = target.resolve(line);
                Files.createDirectories(out.getParent());
                Files.copy(res, out, StandardCopyOption.REPLACE_EXISTING);
                res.close();
            }
        }
    }

    private void zipFolder(Path source, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            Files.walk(source).forEach(path -> {
                try {
                    if (Files.isDirectory(path)) return;

                    ZipEntry entry = new ZipEntry(
                            source.relativize(path).toString().replace("\\", "/")
                    );
                    zos.putNextEntry(entry);
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteRecursive(f);
            }
        }
        file.delete();
    }
}

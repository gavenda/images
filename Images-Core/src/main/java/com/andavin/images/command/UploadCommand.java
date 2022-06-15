package com.andavin.images.command;

import com.andavin.images.Images;
import com.andavin.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class UploadCommand extends BaseCommand {

    private static final Predicate<String> URL_TEST = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]").asPredicate();
    private final Path imagesDirectory = Images.getImagesDirectory().toPath();

    protected UploadCommand() {
        super("upload", "images.command.upload");
        this.setAliases("up");
        this.setMinimumArgs(2);
        this.setUsage("/image upload-file <url> <file-name>");
        this.setDesc("Upload an image by url with the specified file name.");
    }

    @Override
    public void execute(Player player, String label, String[] args) {
        var urlParam = args[0];
        var fileNameParam = args[1];
        var fileDestination = imagesDirectory.resolve(fileNameParam);

        Runnable imageDownloadTask = () -> {
            player.sendMessage("§eUploading image...");

            try {
                var url = new URL(urlParam);
                StandardOpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};

                try (var readableByteChannel = Channels.newChannel(url.openStream())) {
                    try (var fileChannel = FileChannel.open(fileDestination, options)) {
                        long availableBytes;
                        do {
                            availableBytes = fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                        } while (availableBytes > 0);
                    }
                }

                player.sendMessage("§eImage uploaded");
            } catch (Exception e) {
                Logger.severe(e);
                player.sendMessage("§cUnable to download image, upload to imgur and get the direct link if possible");
            }
        };

        if (URL_TEST.test(urlParam)) {
            Bukkit.getScheduler().runTaskAsynchronously(Images.getInstance(), imageDownloadTask);
        } else {
            player.sendMessage("§cInvalid image, must be §7jpg§c, §7gif§c, §7jpeg§c, or §7png");
        }
    }

    @Override
    public void tabComplete(CommandSender sender, String[] args, List<String> completions) {
        if (args.length == 1) {
            completions.add("<url>");
        }
        if (args.length == 2) {
            completions.add("<file-name>");
        }
    }
}

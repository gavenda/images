package com.andavin.images.command;

import com.andavin.images.Images;
import com.andavin.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DeleteFileCommand extends BaseCommand {

    private final Path imagesDirectory = Images.getImagesDirectory().toPath();

    protected DeleteFileCommand() {
        super("file", "images.command.delete.file");
        this.setAliases("f");
        this.setMinimumArgs(1);
        this.setUsage("/image delete file <file-name>");
        this.setDesc("Deletes an image file with the specified name.");
    }

    @Override
    public void execute(Player player, String label, String[] args) {
        var fileNameParam = args[0];
        var fileDestination = imagesDirectory.resolve(fileNameParam);

        Runnable deleteFileRunner = () -> {
            try {
                if (Files.deleteIfExists(fileDestination)) {
                    player.sendMessage("§eImage deleted");
                } else {
                    player.sendMessage("§eImage to delete not found");
                }
            } catch (IOException e) {
                Logger.severe(e);
                player.sendMessage("§cUnable to delete image, please contact staff");
            }
        };

        Bukkit.getScheduler().runTaskAsynchronously(Images.getInstance(), deleteFileRunner);
    }

    @Override
    public void tabComplete(CommandSender sender, String[] args, List<String> completions) {
        if (args.length == 1) {
            Images.getImageFiles().forEach(file -> {
                String name = file.getName();
                if (StringUtil.startsWithIgnoreCase(name, args[0])) {
                    completions.add(name.replace(' ', '_'));
                }
            });
        }
    }
}

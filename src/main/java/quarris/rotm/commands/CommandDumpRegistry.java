package quarris.rotm.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommandDumpRegistry<T extends IForgeRegistryEntry<T>> extends CommandBase {

    public String commandName;
    public IForgeRegistry<T> registry;

    public CommandDumpRegistry(String commandName, IForgeRegistry<T> registry) {
        this.commandName = commandName;
        this.registry = registry;
    }

    @Override
    public String getName() {
        return this.commandName;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /" + this.commandName;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        File dumpFile = server.getFile(this.commandName+".log");
        if (!dumpFile.exists()) {
            try {
                dumpFile.createNewFile();
            } catch (IOException e) {
                sender.sendMessage(new TextComponentString("Could not create the dump file!"));
                e.printStackTrace();
                return;
            }
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(dumpFile));
            for (ResourceLocation name : this.registry.getKeys()) {
                writer.write(name.toString());
                writer.newLine();
            }
            sender.sendMessage(new TextComponentString("Dumped registry keys to " + dumpFile.getPath()));
            writer.close();
        } catch (IOException e) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            e.printStackTrace();
        }

    }
}

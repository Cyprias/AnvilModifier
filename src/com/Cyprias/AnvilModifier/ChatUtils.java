package com.Cyprias.AnvilModifier;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.Cyprias.AnvilModifier.configuration.Config;

public class ChatUtils {

	public static void broadcast(String format, Object... args) {
		broadcast(String.format(format, args));
	}

	public static void broadcastRaw(String format, Object... args) {
		broadcastRaw(String.format(format, args));
	}


	public static void notify(CommandSender sender, String message) {
		send(sender, getChatPrefix() + message);
	}

	public static void sendSpam(CommandSender sender, String message) {
		message = replaceColorCodes(message);
		sender.sendMessage(message);
	}

	public static void send(CommandSender sender, String message) {
		sendSpam(sender, message);
		if (Config.getBoolean("properties.show-plugin-replies") && !(sender instanceof ConsoleCommandSender))
			Logger.infoRaw(ChatColor.stripColor(getChatPrefix() + sender.getName() + " > " + message));
	}

	public static void send(CommandSender sender, ChatColor color, String format, Object... args) {
		sender.sendMessage(color + getChatPrefix() + String.format(format, args));
	}

	public static void sendRaw(CommandSender sender, ChatColor color, String format, Object... args) {
		sender.sendMessage(color + String.format(format, args));
	}

	public static void sendCommandHelp(CommandSender sender, String line, org.bukkit.command.Command cmd) {
		sendCommandHelp(sender, "- ", line, cmd);
	}

	public static void sendCommandHelp(CommandSender sender, String prefix, String line, org.bukkit.command.Command cmd) {
		sendRaw(sender, ChatColor.GRAY, prefix + line, cmd.getLabel());
	}


	public static void error(CommandSender sender, String format, Object... args) {
		sender.sendMessage(getChatPrefix() + ChatColor.RED + String.format(format, args));
	}

	public static void errorRaw(CommandSender sender, String format, Object... args) {
		sender.sendMessage(ChatColor.RED + String.format(format, args));
	}

	public static final String getChatPrefix() {
		return replaceColorCodes(Config.getString("properties.chat-prefix"));
	}

	// replace color codes with the colors
	public static final String replaceColorCodes(String mess) {
		return mess.replaceAll("(&([" + colorCodes + "]))", "\u00A7$2");
	}

	// get rid of color codes
	public static final String cleanColorCodes(String mess) {
		return mess.replaceAll("(&([" + colorCodes + "]))", "");
	}

	private static final String colorCodes;

	static {
		String string = "";
		for (ChatColor color : ChatColor.values()) {
			char c = color.getChar();
			if (!Character.isLetter(c)) {
				string += c;
			} else {
				string += Character.toUpperCase(c);
				string += Character.toLowerCase(c);
			}
		}
		colorCodes = string;
	}
}
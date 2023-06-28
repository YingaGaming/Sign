/*
    Copyright (C) 2022  Marcus Huber (Xenorio)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package games.yinga.Commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import games.yinga.Sign;

public class SignCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        // sign.create permission
        if (!player.hasPermission("sign.create")) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.no-permission")));
            return true;
        }

        // sign.note permission
        if (args.length > 0 && !player.hasPermission("sign.note")) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.no-permission")));
            return true;
        }

        // check if item in main hand
        if (player.getInventory().getItemInMainHand() == null) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.no-item")));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        ItemMeta meta = item.getItemMeta();

        // check if item is already signed
        if (meta.getPersistentDataContainer().get(new NamespacedKey(Sign.getInstance(), "signed"),
                PersistentDataType.STRING) != null && !player.hasPermission("sign.overwrite")) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.no-overwrite")));
            return true;
        }

        List<String> loreList = Sign.config.getStringList("lore");
        ArrayList<String> parsedList = new ArrayList<String>();

        String date = (new SimpleDateFormat(Sign.config.getString("date-format"))).format(new Date());

        String note = String.join(" ", args);

        // only apply color codes if player has permission
        if (player.hasPermission("sign.note.format")) {
            note = ChatColor.translateAlternateColorCodes('&', note);
        }

        // split note into lines
        List<String> splitNote = Arrays
                .asList(note.split("(?<=\\G.{" + Sign.config.getString("note-chars-per-line") + "})"));

        // check if note is too long
        if (splitNote.size() > Sign.config.getInt("note-max-lines")) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.note-too-long")));
            return true;
        }

        // add lore to output list, and replace variables
        loreList.forEach(line -> {
            parsedList.add(
                    ChatColor.translateAlternateColorCodes('&',
                            line
                                    .replaceAll("%PLAYER%", player.getDisplayName())
                                    .replaceAll("%DATE%", date)));
        });

        // if there is a note, apply note prefix
        if (args.length > 0) {
            Sign.config.getStringList("note-prefix").forEach(line -> {
                parsedList.add(
                        ChatColor.translateAlternateColorCodes('&',
                                line
                                        .replaceAll("%PLAYER%", player.getDisplayName())
                                        .replaceAll("%DATE%", date)));
            });
        }

        // add note to output list
        splitNote.forEach(line -> {
            if (!line.equals("") && !line.equals(" ")) {
                parsedList.add(line);
            }
        });

        // if there is a note, apply note suffix
        if (args.length > 0) {
            Sign.config.getStringList("note-suffix").forEach(line -> {
                parsedList.add(
                        ChatColor.translateAlternateColorCodes('&',
                                line
                                        .replaceAll("%PLAYER%", player.getDisplayName())
                                        .replaceAll("%DATE%", date)));
            });
        }

        // add output list to item
        meta.setLore(parsedList);

        // mark item as signed, using player UUID
        meta.getPersistentDataContainer().set(new NamespacedKey(Sign.getInstance(), "signed"),
                PersistentDataType.STRING, player.getUniqueId().toString());

        item.setItemMeta(meta);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.sign-success")));
        return true;
    }

}

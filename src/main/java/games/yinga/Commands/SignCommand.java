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

        if (!player.hasPermission("sign.use")) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.no-permission")));
            return true;
        }

        if (player.getInventory().getItemInMainHand() == null) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.no-item")));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        ItemMeta meta = item.getItemMeta();

        List<String> loreList = Sign.config.getStringList("lore");
        ArrayList<String> parsedList = new ArrayList<String>();

        String date = (new SimpleDateFormat(Sign.config.getString("date-format"))).format(new Date());

        String note = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));

        List<String> splitNote = Arrays.asList(note.split("(?<=\\G.{" + Sign.config.getString("note-chars-per-line") + "})"));

        if (splitNote.size() > Sign.config.getInt("note-max-lines")) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.note-too-long")));
            return true;
        }

        loreList.forEach(line -> {
            parsedList.add(
                ChatColor.translateAlternateColorCodes('&', 
                    line
                        .replaceAll("%PLAYER%", player.getDisplayName())
                        .replaceAll("%DATE%", date)
                )
            );
        });
        
        splitNote.forEach(line -> {
            parsedList.add(line);
        });

        if (player.hasPermission("sign.note")) {
            meta.setLore(parsedList);   
        }

        meta.getPersistentDataContainer().set(new NamespacedKey(Sign.getInstance(), "signed"), PersistentDataType.STRING, player.getUniqueId().toString());

        item.setItemMeta(meta);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.sign-success")));
        return true;
    }
    
}

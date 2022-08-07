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

package games.yinga.Listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import games.yinga.Sign;

public class UseListener implements Listener {

    @EventHandler()
    public void onUse(PlayerInteractEvent event) {

        if (event.getItem() == null) {
            return;
        }
        
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        if (event.getItem().getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(Sign.getInstance(), "signed"), PersistentDataType.STRING) == null) {
            return;
        }

        List<String> whitelist = Sign.config.getStringList("use-whitelist");

        if (whitelist.contains(event.getItem().getType().toString())) {
            return;
        }

        event.setCancelled(true);

        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Sign.config.getString("messages.use-disabled")));

    }
    
}

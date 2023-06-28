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

package games.yinga;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import games.yinga.Commands.SignCommand;
import games.yinga.Listeners.UseListener;

public class Sign extends JavaPlugin {

    public static final int CURRENT_CONFIG_VERSION = 2;

    public static FileConfiguration config;

    private static Sign instance;

    @Override
    public void onEnable() {

        Sign.instance = this;

        this.saveDefaultConfig();

        Sign.config = this.getConfig();

        Sign.config.options().copyDefaults(true);
        saveConfig();

        this.getCommand("sign").setExecutor(new SignCommand());
        this.getServer().getPluginManager().registerEvents(new UseListener(), this);

    }

    public static Sign getInstance() {
        return instance;
    }

}

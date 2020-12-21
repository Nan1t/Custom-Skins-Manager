/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.plugin.example;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.event.SkinChangeEvent;

public class ExamplePlugin extends JavaPlugin implements Listener {

    private static final String TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTU5NTM2MjIxOTE0MSwKICAicHJvZmlsZUlkIiA6ICJiMGQ3MzJmZTAwZjc0MDdlOWU3Zjc0NjMwMWNkOThjYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPUHBscyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81YThjNTUzOGI3NjI5ZGJlMjVkZmY1ZjJiMjkyY2E2ODE3MDJjNmUwOTY3ODVjZDY2YjVhYTc5MjM5NDFkOTI1IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=";
    private static final String SIGNATURE = "HoT9lIHhyQd/C4/CmrBIKIw9w8OI3Fk4kQ/HLyGRaPRgLnY5UuOaJe5+s9XRanjTOgnXmQPIXKI1Xx1At2/pcft7drvxLClqY8gkamIummt030vsff/m5ub0P3BYVOivo1fmxOXYi4U3u+ydYvaUk1kagyY212U6ufobR3Mu1ITmeSVO1E9r/HEOnu0zGwWL76zi2c6i7V+5fmxWdv9OyaDGscexyT57cqgrR5NEZOIeS6puOxKmXx0gXFcDj5y8S9bQNgZo1ZAuSjSOMhs9oy+Jh3p+vLfwTF9AOSgLNc3aly1nAxIQrrAGqivnKdK0FHUuSDOLP6vMJ7GgprtHvvKYOOYCY5lrPDoR9vrNoIu2ibNlq8oNii8SVu9nmMyuudc8m44xkWvJ9/HdRwaGtsPQP8A4DW5KhEmBEywPpYZxH/YlFCPqkoVj+3ije/Vss3X7j9CenZ2h4XLZgnmF12kKGEg5tyhvrd7mRoYnaWOUuvDYgyfYsVdFaPfMkI5jm3KdEaAAMvPE9sFmAbMUFMPecQBGnciYT95lONugglEyUT0wIF5tCpJE/m+A2idlorlJ3MvXRno8WY6tcyZYnAFooDY+y6yDydNw102BA5qqzLupMXl9n0A39VztB4wA9OLad+sx4tOG6qWVcCSJZOXJmjOI7E8fEXSq89ELzis=";

    private SkinsAPI<Player> api;

    @Override
    public void onEnable() {
        api = getServer().getServicesManager().getRegistration(SkinsAPI.class).getProvider();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && label.equals("skintest")){
            Skin skin = new Skin(TEXTURE, SIGNATURE);
            api.setCustomSkin((Player) sender, skin);
        }

        return true;
    }

    @EventHandler
    public void onSkinChange(SkinChangeEvent event){
        event.setNewSkin(new Skin(TEXTURE, SIGNATURE));
    }
}

package ru.csm.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class PlayerListeners {

    private final SkinsAPI<Player> api;

    public PlayerListeners(SkinsAPI<Player> api){
        this.api = api;
    }

    @Subscribe
    public void profileRequest(GameProfileRequestEvent event){
        SkinPlayer player = api.getPlayer(event.getUsername());

        if (player != null){
            event.setGameProfile(getProfile(event.getGameProfile(), player));
            player.applySkin();
            player.refreshSkin();
        } else {
            player = api.loadPlayer(event.getGameProfile().getId(), event.getUsername());

            if (player == null){
                player = api.buildPlayer(event.getGameProfile().getId(), event.getUsername());
                api.createNewPlayer(player);
            }

            api.addPlayer(player);

            player.applySkin();
            player.refreshSkin();

            event.setGameProfile(getProfile(event.getGameProfile(), player));
        }
    }

    private GameProfile getProfile(GameProfile def, SkinPlayer player){
        Skin skin = player.hasCustomSkin() ? player.getCustomSkin() : player.getDefaultSkin();
        List<GameProfile.Property> props = Collections.singletonList(new GameProfile.Property("textures", skin.getValue(), skin.getSignature()));
        return def.withProperties(props);
    }

    @Subscribe
    public void onLogout(DisconnectEvent event){
        api.removePlayer(event.getPlayer().getUniqueId());
    }

}

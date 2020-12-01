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

package ru.csm.api.upload;

import ru.csm.api.event.EventSkinChange;
import ru.csm.api.event.EventSkinReset;
import ru.csm.api.event.Events;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.MojangAPI;
import ru.csm.api.services.SkinHash;
import ru.csm.api.services.SkinsAPI;

import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public final class NameQueue implements Runnable {

    private final SkinsAPI<?> api;
    private final Queue<Request> queue = new ConcurrentLinkedQueue<>();
    private final int period;

    public NameQueue(SkinsAPI<?> api, int period){
        this.api = api;
        this.period = period;
    }

    protected Optional<Request> pop(){
        return Optional.ofNullable(queue.poll());
    }

    public int getWaitSeconds(){
        return queue.size() * period;
    }

    public void push(SkinPlayer player, String name){
        queue.offer(new Request(player, name));
    }

    @Override
    public void run() {
        pop().ifPresent((request)->{
            if (request.getPlayer().isOnline()){
                Optional<Skin> hashed = SkinHash.get(request.getName());

                if (hashed.isPresent()){
                    fireEvent(request.getPlayer(), hashed.get(), (event)->{
                        if (!event.isCancelled()){
                            api.setCustomSkin(request.getPlayer(), hashed.get());
                        }
                    });
                    return;
                }

                UUID targetUUID = MojangAPI.getUUID(request.getName());

                if(targetUUID != null){
                    Skin skin = MojangAPI.getPremiumSkin(targetUUID);

                    if(skin != null){
                        fireEvent(request.getPlayer(), skin, (event)->{
                            if (!event.isCancelled()){
                                SkinHash.add(request.getName(), skin);
                                api.setCustomSkin(request.getPlayer(), skin);
                            }
                        });
                        return;
                    }
                }

                request.getPlayer().sendMessage(api.getLang().of("skin.name.error"));
            }
        });
    }

    private void fireEvent(SkinPlayer player, Skin skin, Consumer<EventSkinChange> callback){
        EventSkinChange event = new EventSkinChange(player, player.getCurrentSkin(), skin, EventSkinChange.Source.USERNAME);
        Events.fireSkinChange(event, callback);
    }

    private static class Request {

        private final SkinPlayer player;
        private final String name;

        public Request(SkinPlayer player, String name){
            this.player = player;
            this.name = name;
        }

        public SkinPlayer getPlayer() {
            return player;
        }

        public String getName() {
            return name;
        }
    }
}

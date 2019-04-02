package ru.csm.api.serializers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ru.csm.api.upload.entity.Profile;

public class ProfileSerializer implements TypeSerializer<Profile> {

    @Override
    public Profile deserialize(TypeToken<?> type, ConfigurationNode node) {
        String login = node.getNode("login").getString();
        String password = node.getNode("password").getString();
        return new Profile(login, password);
    }

    @Override
    public void serialize(TypeToken<?> type, Profile profile, ConfigurationNode node) {

    }
}

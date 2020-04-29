import com.google.gson.JsonObject;
import org.junit.Test;
import ru.csm.api.network.MessageReceiver;
import ru.csm.api.network.MessageSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MathTest {

    private static final MessageSender<Object> SENDER = new Sender();
    private static final MessageReceiver RECEIVER = new MessageReceiver();

    @Test
    public void testMessage(){
        RECEIVER.registerHandler("test", (result)->{
            System.out.println("Received: " + result.toString().length());
            System.out.println(result.toString());
        });

        JsonObject json = getJson();

        System.out.println("Sent " + json.toString().length());

        SENDER.sendMessage(null, "test", json);
    }

    private static class Sender extends MessageSender<Object> {

        @Override
        public void send(Object o, String channel, byte[] data) {
            RECEIVER.receive(channel, data);
        }

    }

    private static JsonObject getJson(){
        Path file = Paths.get("/mnt/1802EB4C64E592DE/lorem.txt");
        String str;

        try{
            str = new String(Files.readAllBytes(file));
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }

        JsonObject json = new JsonObject();
        json.addProperty("test", "vasya");
        json.addProperty("test2", str);
        return json;
    }
}

import com.google.gson.JsonObject;
import org.junit.Test;
import ru.csm.api.network.MessageReceiver;
import ru.csm.api.network.MessageSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MathTest {

    @Test
    public void test(){

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

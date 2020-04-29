import org.junit.Test;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.MySQLDatabase;
import ru.csm.api.storage.database.Row;

public class TestDatabase {

    @Test
    public void testSkinsRequest() throws Exception {
        Database database = new MySQLDatabase("localhost", 3306, "testing", "root", "y9AKU5rL=u7gh^v!mRL!_NGN&zQG8G&e");

        int page = 2;
        int range = 45;
        int offset = (page-1) * range;

        String sql = "SELECT name,custom_value,custom_signature FROM %s WHERE custom_value IS NOT NULL LIMIT %s OFFSET %s";
        Row[] rows = database.getRowsWithRequest(String.format(sql, "skins", range, offset));

        for (Row row : rows){
            System.out.println(" ");
            System.out.println(row.getField("name"));
            System.out.println(row.getField("custom_value"));
            System.out.println(row.getField("custom_signature"));
        }

        System.out.println("\nResult " + rows.length);
    }

    @Test
    public void generateRows(){
        String sql = "INSERT INTO `skins` (`id`, `uuid`, `name`, `default_value`, `default_signature`, `custom_value`, `custom_signature`) VALUES (NULL, 'uuid', '%s', 'default value', 'default signa', '%s', '%s');";
        String value = "eyJ0aW1lc3RhbXAiOjE1ODc5MDEyMjU5NzQsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWNkZjFiZTIzMTgyOGE3NTNiNzQyMjllNGYzZDNkN2IyZTNiZmRjYmJjOWVhYmE1YmRkYTJjZDQ5NjA3ZWQwYiJ9fX0=";
        String signature = "k3IN+SJeZ/EE1o9flmdLeN9Spl7Ov4ISzPLFIAtGQyytmB8UjUlytRBayzmpoDFWy+ciynmx6wx6lXg2NqUa1k5E4GYylsJpKqFHGYeTR0DKwb/euoOUKTKnh80ErnhSLsAaCZeYZwVXWA+pDMqeO+GuOvirK/BZFWa28h32BOLyh+KYXF/xwegq6JTd9zGsRWdxo2pD4s8sBJrv2jLFkzhasQ6PE2yDXknAM+GaGysxRbquvQ/nq1klTfedOpBxZ32MqH/VDC0AJ/pO5iWRBW3mQTMjJ0c2KRLhn2lg0MdpTIw4+Yg4nYVPkWqO9B6jngi4h1v+kiAYMI8hcFkPSeyutiyyt7dHP4kTgBn9qD5KHE/jItIIVIX+p/AhaU453JfTdgGTlHLFcG0SMLyZZJzMuuJZi0fIizV+G66AJ3OZJdL0/AytKuqJuUeXGIve1NcUrA+UFALVHODtQmacrh9AeajGbKvzNWflHczfXrLU4oaUThAodiJuFFTrImIfv9i6VIiiJHLNF5SudwGywnlmSY9JBVrj6H352dBssBReH+tS9hx0u/hdkMTIfZ2DfWgrSpg3iWiCac4rMbm0ULa6pX/00lLelOAFO30NpasCG3EFAZ+VD54XqfqIuFwxHUYZR+UcPgb/2SyHjZE0SbcAvUmTwoHqsZV0a6uFSmw=";

        for (int i = 0; i < 100; i++){
            System.out.println(String.format(sql, "Player_"+i, value, signature));
        }
    }

    @Test
    public void test(){
        int length = 68000;
        int parts = (int) Math.ceil(length/63000f);
        int lastPart = length % 63000;

        System.out.println(parts);
        System.out.println(lastPart);
    }

}

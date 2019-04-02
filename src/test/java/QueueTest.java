import java.util.UUID;

public class QueueTest {

    public static void main(String[] args) {

        String defaultValue = "eyJ0aW1lc3RhbXAiOjE1NTQwMDM4NzkzMDgsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE2MDZlZTkxYTk4YjVmOTQ2MDYyOGM1ZDhhY2ZiYmE0MmIyMTQ1NTcyMmVhMmNlMTkyOTEwNmE5Yzk4OWYzNTYifX19";
        String defaultSignature = "BJJpRhm2Z0mMLXUSmm+ivetQwye4e5QJudyJdgiG9tcAKCuIzLe5R7XyX/3j7pM8oaAJplz/Lx29t21NQR/z6lTAUNLrQdoCJk2tdwsFhH+ff6Ktq19cGEtLUlRf0NJ2eqp4Cf/zFkPl49wICMv2taaO5T27Uvc58nWALjOR+XRQ1AMwAoXJt9HEvfbSDVlqO7rK04EwHvCQreaQhNOyB+fdtOu+z+3FAAKi9rXriJE92x6DbpMRBfnGizl6tl6wbi99Up11xckSWv91CmdvxC3fWS1cVxSdgk5oIzq/UXMViVsag1OtvVNZTUEQ4wVtk8S1LX4FyXz2H7I591AYM6ecnpIrQo8rKt4dl0bfLpnvUyPluOeydEHWzrssA5btXzP11Q5Q1UPrC+GjPWytLBtVPJO2GGGFVdUZOTgWhmjGX9C6Y29z6pntT67EuW3olvCTRWu+oGqucjtoTY9/t7C4SVV83nyHO5W4b7WB3rm+pbz+0rPW0c3MkEurbXM1/ic2gX3WJNevPzeYLsI1DzsgfGk95KfSrsZpwghACwAwfWnLUgfP/PgQ5AQE59+V/6BsQdOqo3rvnWzsO8LVLUx8r69aCe2z22uWpbIQe9w3u+gO1zFBuzpIC6DltFfMlCAq6Wl2pDfpf/ZycR7Xq4H2DCQ5A+BehD0ND+HE5+k=";
        String customValue = "eyJ0aW1lc3RhbXAiOjE1NTQwMDkzNTM0NjgsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQwZGYwZTY5OTQwY2MxYTk3Mjc1OTRjNTIxNzMzOGJjYmQyYzkwNjgyZDhhMTdmYjgzMDA5YjlhMmUyYWIyMSJ9fX0=";
        String customSignature = "p2aeOrVw1UDkakkkLak9mL+S0LUJN0SciItxxYC833upXTJy2cnxka/j8Uog+66cHftkhKFNNxchM5UnvSk3X2V6hxJ3JBDjMswNk0tnWufw52QdMwVbK0eqiOwq8IdU2zpSsY7MGLBt7TzJ7imHG7MwTGYtYxwJoO2eXlOQM18xv69t1kPG2TA8iMi/i+Hhq0HEnFDBaJLGV2Bbj2kqxjQycUDTb9bhFci9Me7RJP2yoL0zxdHyfYJX8KT8Lf7nyLFkw229GQj1pQlRWR3jOUeUFmHJf9AnHP/oAbsHvYeuPoy7cxAg64q0N80BcJ+4Jqafp5+NwrIh+1Vf8ZqZECtRzmpfTmJqhNnKThohSoeLu3C0NZfh5A7VkOKDdrgDN9dzAtys/ghPIyyJbn+cKDD8b6vKajrV7OwmlcTd5+ByHREH2XAzsl6gL+H0/d2d8QutTyWYFv/0SbMF1lMq+4llTi6W6JfFEm7OuK8NUKoGwt1shnrJ0gd2JWeJICvzJmSISYEMnxZDRacrrzZ3x587DIBZkLTQHYoSF65TRHCBmii+kZ02hwNw3ctnmYlTsqMNqh3WQbN35UnEHIXjmpsrPay/EPiYQUzmlhT2HpdiMx3hXQm6L+A1tS+p1TFY/5oVcJ1xBMe3ojOPX6nxX5TJMN7qnQI/Y/67ClNoLl0=";

        String name = "Player";

        for(int i = 0; i <= 80; i++){
            UUID uuid = UUID.randomUUID();
            String n = name+"_"+i;
            String sql = "INSERT INTO `skins` (`uuid`, `name`, `default_value`, `default_signature`, `custom_value`, `custom_signature`) VALUES ('"+uuid.toString()+"', '"+n+"', '"+defaultValue+"', '"+defaultSignature+"', '"+customValue+"', '"+customSignature+"');";
            System.out.println(sql);
        }
    }
}

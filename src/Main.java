import spark.Spark;

import java.util.HashMap;
import java.util.Objects;

public class Main {
    public static void main(String[] args){
        Spark.port(8080);
        Spark.webSocket("/echo", EchoWebSocket.class);
        Spark.get("/", ((request, response) -> {
            return "Hello World";
        }));
        Spark.init();
    }
}

package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import wacc_05.WaccCompiler;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        WaccCompiler.runCompiler("webapp/server/src/main/resources/longSplitExpr2.wacc", 1, false, false);
        System.out.println(WaccCompiler.testingApi());
    }

}


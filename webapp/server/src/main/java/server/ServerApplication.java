package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        String filePath = "# operator missing second operand\n" +
                "\n" +
                "# Output:\n" +
                "# #syntax_error#\n" +
                "\n" +
                "# Exit:\n" +
                "# 100\n" +
                "\n" +
                "# Program:\n" +
                "\n" +
                "begin\n" +
                "  int b = 2 - \n" +
                "end\n";
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "build/libs/wacc_05.jar", filePath, "0", "false", "false", "false");
        pb.redirectErrorStream(true);
        String result = "";
        try {
            Process exec = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            result = br.lines().collect(Collectors.joining(System.lineSeparator()));
            System.out.println("Process exited with " + exec.waitFor());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

}

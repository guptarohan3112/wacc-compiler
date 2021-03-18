package server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RestController
public class compilerController {

    @CrossOrigin
    @PostMapping(value = "/compile", produces = "application/json")
    public Response greeting(@RequestBody String code) {
        return compile(code);
    }

    private Response compile(String code) {
        Response res = new Response();

        code = code.replaceAll("\"","\\\\\"");
        System.out.println(code);

        // create .s file
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "build/libs/wacc_05.jar", code, "0", "false", "false", "false");
        pb.redirectErrorStream(true);
        String result = "";

        // get error message from terminal output
        try {
            Process exec = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            result = br.lines().collect(Collectors.joining(System.lineSeparator()));
            int errorCode = exec.waitFor();
            System.out.println("Process exited with " + errorCode);
            res.setErrorCode(errorCode == 0 ? 1 : 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // read assembly from file
        String path = "result.s";

        if (res.getErrorCode() != 0) {
            try {
                // default StandardCharsets.UTF_8
                String content = Files.readString(Paths.get(path));
                System.out.println(content);
                res.setAssembly(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            new File("result.s").delete();
        }

        res.setErrorMsg(result);

        return res;
    }

}

class Response {

    private String assembly = "";
    private String errorMsg;
    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }
}

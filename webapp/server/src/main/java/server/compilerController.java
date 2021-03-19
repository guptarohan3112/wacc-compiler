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
    @PostMapping(value = "/compile", consumes = "application/json", produces = "application/json")
    public Response greeting(@RequestBody Request request) {
        return compile(request.getCode(), request.getOptimise());
    }

    private Response compile(String code, int optimise) {
        Response res = new Response();

        // create .s file
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "../../build/libs/wacc_05.jar", code, String.valueOf(optimise), "false", "false", "false");
        pb.redirectErrorStream(true);
        String result = "";

        // get error message from terminal output
        try {
            Process exec = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            result = br.lines().collect(Collectors.joining(System.lineSeparator()));
            int errorCode = exec.waitFor();
            res.setErrorCode(errorCode == 0 ? 1 : 0);
            res.setErrorMsg(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        String path = "result.s";

        // read assembly from file
        if (res.getErrorCode() == 0) {
            return res;
        }

        // run arm-linux-gnueabi
        pb = new ProcessBuilder("arm-linux-gnueabi-gcc", "-o", "result", "-mcpu=arm1176jzf-s", "-mtune=arm1176jz-s", path);
        // get error message from terminal output
        try {
            Process exec = pb.start();
            int errorCode = exec.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // run qemu-arm
        pb = new ProcessBuilder("qemu-arm", "-L", "/usr/arm-linux-gnueabi/", "result");
        pb.redirectErrorStream(true);
        result = "";

        // get output from terminal output
        try {
            Process exec = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            result = br.lines().collect(Collectors.joining(System.lineSeparator()));
            int errorCode = exec.waitFor();
            res.setOutput(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            // default StandardCharsets.UTF_8
            String content = Files.readString(Paths.get(path));
            res.setAssembly(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new File("result.s").delete();
        new File("result").delete();
        return res;
    }

}

class Request {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private int optimise;

    public int getOptimise() {
        return optimise;
    }

    public void setOptimise(int optimise) {
        this.optimise = optimise;
    }
}

class Response {

    private String assembly = "";
    private String errorMsg;
    private String output;
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

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}

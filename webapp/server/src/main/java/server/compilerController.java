package server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class compilerController {

    @GetMapping("/compile")
    public String greeting(@RequestBody String code) {
        return compile(code);
    }

    private String compile(String code) {
        return code;
    }


}

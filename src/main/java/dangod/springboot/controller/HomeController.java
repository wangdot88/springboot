package dangod.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "api-docs";
    }
    
    @GetMapping("/api-docs")
    public String apiDocs() {
        return "api-docs";
    }
}
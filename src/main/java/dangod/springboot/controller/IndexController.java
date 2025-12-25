package dangod.springboot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController {
    @Value("${properties.demo}")
    private String str;

    @RequestMapping(value = {"","/","index"})
    public String index(HttpServletRequest request, HttpServletResponse response, Model model){
        model.addAttribute("str", str);
        return "index";
    }
    
    @RequestMapping(value = "/ticket")
    public String ticket(HttpServletRequest request, HttpServletResponse response, Model model){
        return "ticket";
    }
}

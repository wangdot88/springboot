package dangod.springboot.controller;

import dangod.springboot.core.util.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @GetMapping("/health")
    public ApiResponse health() {
        return ApiResponse.success("系统运行正常");
    }
    
    @GetMapping("/info")
    public ApiResponse info() {
        return ApiResponse.success("校园智能体育管理系统", "v1.0");
    }
}
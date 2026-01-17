package dangod.springboot.controller;

import dangod.springboot.common.Result;
import dangod.springboot.core.util.JwtUtil;
import dangod.springboot.entity.Member;
import dangod.springboot.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "认证")
public class AuthController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @ApiOperation("登录")
    public Result<Map<String, Object>> login(@RequestParam String phone, @RequestParam String password) {
        Member member = memberService.login(phone, password);
        
        String token = jwtUtil.generateToken(member.getPhone(), member.getId(), "MEMBER");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("memberId", member.getId());
        result.put("name", member.getName());
        result.put("phone", member.getPhone());
        result.put("cardLevel", member.getCardLevel().getName());
        
        return Result.success(result);
    }

    @PostMapping("/refresh")
    @ApiOperation("刷新token")
    public Result<Map<String, String>> refresh(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                Long userId = jwtUtil.getUserIdFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                
                String newToken = jwtUtil.generateToken(username, userId, role);
                
                Map<String, String> result = new HashMap<>();
                result.put("token", newToken);
                
                return Result.success(result);
            }
        }
        
        return Result.error("401", "无效的token");
    }
}

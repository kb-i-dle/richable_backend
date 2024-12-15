package com.idle.kb_i_dle_backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    /**
     * RestController가 아닌 Controller에서는 화면을 리턴해주면 된다. string값을 리턴 하면 spring이 config에서 설정해둔 /WEB-INF/views/ 안의 String값의
     * jsp파일을 찾아와 준다.
     *
     * @return
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}

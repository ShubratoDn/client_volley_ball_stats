package com.volleystats.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // You can log the error or show different views based on status code
        return "error/general-error"; // Thymeleaf template name
    }

    // (Optional in Spring Boot 2.3+)
    public String getErrorPath() {
        return "/error";
    }
}

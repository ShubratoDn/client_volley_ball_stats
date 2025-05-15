package com.volleystats.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String handleGenericError() {
        return "error/general-error"; // this should match the HTML you saved
    }
}

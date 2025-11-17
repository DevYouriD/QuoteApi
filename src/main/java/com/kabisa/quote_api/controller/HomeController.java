package com.kabisa.quote_api.controller;

import com.kabisa.quote_api.model.Quote;
import com.kabisa.quote_api.service.QuoteService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Getter @Setter
@AllArgsConstructor
public class HomeController {

    private final QuoteService quoteService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("randomquote")
    @ResponseBody
    public Quote getQuote() {
        return quoteService.getRandomQuote();
    }

}

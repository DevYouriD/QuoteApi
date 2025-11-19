package com.kabisa.quote_api.api.controller;

import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.dto.QuoteRequestDto;
import com.kabisa.quote_api.api.model.dto.QuoteWithRating;
import com.kabisa.quote_api.api.service.QuoteService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Getter @Setter
@AllArgsConstructor
@RequestMapping("/api/v1/quote")
public class QuoteRestController {

    private final QuoteService quoteService;

    @GetMapping("/random")
    public ResponseEntity<Quote> getRandomQuote() {
        Quote quote = quoteService.getRandomQuote();
        return ResponseEntity.ok(quote);
    }

    @PostMapping("/rate")
    public ResponseEntity<QuoteWithRating> rateQuote(@RequestBody QuoteRequestDto quote){
        QuoteWithRating quoteWithRating = quoteService.rateQuote(quote);
        return ResponseEntity.ok(quoteWithRating);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<QuoteWithRating>> getTopRatedQuotes() {
        List<QuoteWithRating> topQuotes = quoteService.getHighestRatedQuotes();
        return ResponseEntity.ok(topQuotes);
    }

}

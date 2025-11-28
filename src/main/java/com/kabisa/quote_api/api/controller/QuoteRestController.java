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

import static com.kabisa.quote_api.api.utility.Constants.GET_RANDOM_QUOTE_PATH;
import static com.kabisa.quote_api.api.utility.Constants.GET_TOP_RATED_QUOTES_PATH;
import static com.kabisa.quote_api.api.utility.Constants.RATE_QUOTE_PATH;
import static com.kabisa.quote_api.api.utility.Constants.REST_BASE_PATH;

@RestController
@Getter @Setter
@AllArgsConstructor
@RequestMapping(REST_BASE_PATH)
public class QuoteRestController {

    private final QuoteService quoteService;

    @GetMapping(GET_RANDOM_QUOTE_PATH)
    public ResponseEntity<Quote> getRandomQuote() {
        Quote quote = quoteService.getRandomQuote();
        return ResponseEntity.ok(quote);
    }

    @PostMapping(RATE_QUOTE_PATH)
    public ResponseEntity<Object> rateQuote(@RequestBody QuoteRequestDto quote){
        try{
            QuoteWithRating quoteWithRating = quoteService.rateQuote(quote);
            return ResponseEntity.ok(quoteWithRating);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }

    @GetMapping(GET_TOP_RATED_QUOTES_PATH)
    public ResponseEntity<List<QuoteWithRating>> getTopRatedQuotes() {
        List<QuoteWithRating> topQuotes = quoteService.getHighestRatedQuotes();
        return ResponseEntity.ok(topQuotes);
    }

}

package com.kabisa.quote_api.service;

import com.kabisa.quote_api.model.Quote;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
public class QuoteService {

    private DummyJsonQuoteService dummyJsonQuoteService;
    private ZenQuoteService zenQuoteService;

    public Quote getRandomQuote() {

        CompletableFuture<Quote> dummyJsonQuote = dummyJsonQuoteService.getRandomQuote();
        CompletableFuture<Quote> zenQuote = zenQuoteService.getRandomQuote();

        return CompletableFuture.anyOf(dummyJsonQuote, zenQuote)
                .thenApply(Quote.class::cast)
                .join();
    }

}

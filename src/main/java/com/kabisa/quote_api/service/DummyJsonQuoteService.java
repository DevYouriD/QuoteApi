package com.kabisa.quote_api.service;

import com.kabisa.quote_api.model.Quote;
import com.kabisa.quote_api.model.dummyjson.DummyJsonQuote;
import com.kabisa.quote_api.model.mapper.DummyJsonToQuoteMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class DummyJsonQuoteService implements QuoteInterface {

    private WebClient webClient;
    private DummyJsonToQuoteMapper dummyJsonToQuoteMapper;

    @Override
    public CompletableFuture<Quote> getRandomQuote() {
        return webClient.get()
                .uri("https://dummyjson.com/quotes/random")
                .retrieve()
                .bodyToMono(DummyJsonQuote.class)
                .map(dummyJsonToQuoteMapper::dummyJsonToQuote)
                .toFuture();
    }

}
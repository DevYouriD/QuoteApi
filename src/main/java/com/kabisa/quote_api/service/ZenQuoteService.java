package com.kabisa.quote_api.service;

import com.kabisa.quote_api.model.Quote;
import com.kabisa.quote_api.model.mapper.ZenToQuoteMapper;
import com.kabisa.quote_api.model.zenquotes.ZenQuote;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class ZenQuoteService implements QuoteInterface {

    private WebClient webClient;
    private ZenToQuoteMapper zenToQuoteMapper;

    @Override
    public CompletableFuture<Quote> getRandomQuote() {
        return webClient.get()
                .uri("https://zenquotes.io/api/random")
                .retrieve()
                .bodyToMono(ZenQuote[].class)
                .map(arr -> arr[0])
                .map(zenToQuoteMapper::zenToQuote)
                .toFuture();
    }

}

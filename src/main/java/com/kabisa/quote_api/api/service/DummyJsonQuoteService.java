package com.kabisa.quote_api.api.service;

import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.dto.dummyjson.DummyJsonQuote;
import com.kabisa.quote_api.api.model.mapper.DummyJsonToQuoteMapper;
import com.kabisa.quote_api.api.utility.QuoteUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
                .map(quote -> {
                    if (quote == null
                            || !StringUtils.hasText(quote.quote())
                            || !StringUtils.hasText(quote.author())) {
                        return QuoteUtils.fallback(new RuntimeException("Invalid API response"));
                    }
                    return quote;
                })
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(ex -> Mono.just(QuoteUtils.fallback(ex)))
                .toFuture();
    }

}
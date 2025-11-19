package com.kabisa.quote_api.api.service;

import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.mapper.ZenToQuoteMapper;
import com.kabisa.quote_api.api.model.dto.zenquotes.ZenQuote;
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
public class ZenQuoteService implements QuoteInterface {

    private WebClient webClient;
    private ZenToQuoteMapper zenToQuoteMapper;

    @Override
    public CompletableFuture<Quote> getRandomQuote() {
        return webClient.get()
                .uri("https://zenquotes.io/api/random")
                .retrieve()
                .bodyToMono(ZenQuote[].class)
                .map(arr -> arr.length > 0 ? arr[0] : null)
                .map(zenToQuoteMapper::zenToQuote)
                .map(quote -> {
                    if (quote == null
                            || !StringUtils.hasText(quote.quote())
                            || quote.quote().equalsIgnoreCase("Unrecognized API request. Visit zenquotes.io for documentation.".toLowerCase())
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

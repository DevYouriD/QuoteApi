package com.kabisa.quote_api.api.service;

import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.dto.dummyjson.DummyJsonQuote;
import com.kabisa.quote_api.api.model.mapper.DummyJsonToQuoteMapper;
import com.kabisa.quote_api.api.utility.RedisRateLimiter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static com.kabisa.quote_api.api.utility.Constants.DUMMY_JSON_PATH;

@Service
@AllArgsConstructor
public class DummyJsonQuoteService implements QuoteInterface {

    private final WebClient webClient;
    private final RedisRateLimiter rateLimiter;
    private final DummyJsonToQuoteMapper dummyJsonToQuoteMapper;
    private final ClientIpService clientIpService;

    @Override
    public CompletableFuture<Quote> getRandomQuote() {
        String ip = clientIpService.getClientIp();

        if (rateLimiter.tryConsume("dummyjson:" + ip, 10, Duration.ofSeconds(30))) {
            return CompletableFuture.failedFuture(
                    new RuntimeException("429 Error: DummyJSON rate limit exceeded!")
            );
        }

        return webClient.get()
                .uri(DUMMY_JSON_PATH)
                .retrieve()
                .bodyToMono(DummyJsonQuote.class)
                .map(dummyJsonToQuoteMapper::dummyJsonToQuote)
                .flatMap(quote -> {
                    if (quote == null
                            || !StringUtils.hasText(quote.quote())
                            || !StringUtils.hasText(quote.author())) {
                        return Mono.error(new RuntimeException("Invalid API response"));
                    }
                    return Mono.just(quote);
                })
                .timeout(Duration.ofSeconds(5))
                .toFuture();
    }

}
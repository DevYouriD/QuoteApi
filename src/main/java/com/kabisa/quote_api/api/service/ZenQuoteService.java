package com.kabisa.quote_api.api.service;

import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.dto.zenquotes.ZenQuote;
import com.kabisa.quote_api.api.model.mapper.ZenToQuoteMapper;
import com.kabisa.quote_api.api.utility.RedisRateLimiter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static com.kabisa.quote_api.api.utility.Constants.ZEN_QUOTE_API_ERROR;
import static com.kabisa.quote_api.api.utility.Constants.ZEN_QUOTE_PATH;

@Service
@AllArgsConstructor
public class ZenQuoteService implements QuoteInterface {

    private final WebClient webClient;
    private final RedisRateLimiter rateLimiter;
    private final ZenToQuoteMapper zenToQuoteMapper;
    private final ClientIpService clientIpService;

    @Override
    public CompletableFuture<Quote> getRandomQuote() {
        String ip = clientIpService.getClientIp();

        if (rateLimiter.tryConsume("zenquotes:" + ip, 5, Duration.ofSeconds(30))) {
            return CompletableFuture.failedFuture(
                    new RuntimeException("429 Error: ZenQuotes rate limit exceeded!")
            );
        }

        return webClient.get()
                .uri(ZEN_QUOTE_PATH)
                .retrieve()
                .bodyToMono(ZenQuote[].class)
                .map(arr -> arr.length > 0 ? arr[0] : null)
                .map(zenToQuoteMapper::zenToQuote)
                .flatMap(quote -> {
                    if (quote == null
                            || !StringUtils.hasText(quote.quote())
                            || quote.quote().equalsIgnoreCase(ZEN_QUOTE_API_ERROR.toLowerCase())
                            || !StringUtils.hasText(quote.author())) {
                        return Mono.error(new RuntimeException("Invalid API response"));
                    }
                    return Mono.just(quote);
                })
                .timeout(Duration.ofSeconds(5))
                .toFuture();
    }

}

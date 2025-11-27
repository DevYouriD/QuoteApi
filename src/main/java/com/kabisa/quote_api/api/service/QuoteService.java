package com.kabisa.quote_api.api.service;

import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.dto.QuoteRequestDto;
import com.kabisa.quote_api.api.model.dto.QuoteWithRating;
import com.kabisa.quote_api.api.model.entity.RatedQuote;
import com.kabisa.quote_api.api.repository.RatedQuoteRepository;
import com.kabisa.quote_api.api.utility.QuoteUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
public class QuoteService {

    private final RatedQuoteRepository ratedQuoteRepository;
    private final DummyJsonQuoteService dummyJsonQuoteService;
    private final ZenQuoteService zenQuoteService;

    public Quote getRandomQuote() {

        // Return null if getRandomQuote fails
        CompletableFuture<Quote> dummyQuote = dummyJsonQuoteService.getRandomQuote().exceptionally(ex -> null);
        CompletableFuture<Quote> zenQuote = zenQuoteService.getRandomQuote().exceptionally(ex -> null);

        // Return fastest result if not null
        Quote fastest = dummyQuote.applyToEither(zenQuote, q -> q).join();
        if (fastest != null) return fastest;

        // Return slower result if fastest result fails
        Quote dummyResult = dummyQuote.join();
        if (dummyResult != null) return dummyResult;

        Quote zenResult = zenQuote.join();
        if (zenResult != null) return zenResult;

        // Generic return if both fastest and slower are null
        return QuoteUtils.fallback(new RuntimeException("Both APIs failed"));
    }

    @Transactional
    public QuoteWithRating rateQuote(QuoteRequestDto quote) {
        if (quote.starRating() < 1 || quote.starRating() > 5) {
            throw new IllegalArgumentException("Quote rating must be between 1 and 5");
        }
        RatedQuote savedQuote = ratedQuoteRepository.findByQuoteAndAuthor(quote.quote(), quote.author())
                .map(existing -> {
                    existing.setTotalRating(existing.getTotalRating() + quote.starRating());
                    existing.setRatingCount(existing.getRatingCount() + 1);
                    return ratedQuoteRepository.save(existing);
                })
                .orElseGet(() -> ratedQuoteRepository.save(buildRatedQuote(quote)));

        return buildQuoteWithRating(savedQuote);
    }

    public List<QuoteWithRating> getHighestRatedQuotes() {
        List<RatedQuote> topRated = ratedQuoteRepository.findTopRatedByAverageRating(PageRequest.of(0, 5));
        return topRated.stream()
                .map(this::buildQuoteWithRating)
                .toList();
    }

    private QuoteWithRating buildQuoteWithRating(RatedQuote ratedQuote) {
        return QuoteWithRating.builder()
                .quote(ratedQuote.getQuote())
                .author(ratedQuote.getAuthor())
                .avgRating(Math.round(ratedQuote.getAverageRating() * 10.0) / 10.0) // round to 1 decimal
                .build();
    }

    private RatedQuote buildRatedQuote(QuoteRequestDto quote) {
        return RatedQuote.builder()
                .quote(quote.quote())
                .author(quote.author())
                .totalRating(quote.starRating())
                .ratingCount(1)
                .build();
    }

}

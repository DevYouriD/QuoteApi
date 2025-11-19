package com.kabisa.quote_api.api.service;

import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.dto.QuoteRequestDto;
import com.kabisa.quote_api.api.model.dto.QuoteWithRating;
import com.kabisa.quote_api.api.model.entity.RatedQuote;
import com.kabisa.quote_api.api.repository.RatedQuoteRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
public class QuoteService {

    private RatedQuoteRepository ratedQuoteRepository;
    private DummyJsonQuoteService dummyJsonQuoteService;
    private ZenQuoteService zenQuoteService;

    public Quote getRandomQuote() {

        CompletableFuture<Quote> dummyJsonQuote = dummyJsonQuoteService.getRandomQuote();
        CompletableFuture<Quote> zenQuote = zenQuoteService.getRandomQuote();

        return CompletableFuture.anyOf(dummyJsonQuote, zenQuote)
                .thenApply(Quote.class::cast)
                .join();
    }

    @Transactional
    public QuoteWithRating rateQuote(QuoteRequestDto quote) {
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

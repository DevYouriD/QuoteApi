package com.kabisa.quote_api;

import com.kabisa.quote_api.api.model.dto.QuoteRequestDto;
import com.kabisa.quote_api.api.model.dto.QuoteWithRating;
import com.kabisa.quote_api.api.model.entity.RatedQuote;
import com.kabisa.quote_api.api.repository.RatedQuoteRepository;
import com.kabisa.quote_api.api.service.QuoteService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class QuoteServiceIntegrationTest {

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private RatedQuoteRepository repository;

    @Test
    void testGetHighestRatedQuotes() {
        saveQuote("Quote A", "Author A", 9, 3);
        saveQuote("Quote B", "Author B", 18, 4);

        List<QuoteWithRating> topRated = quoteService.getHighestRatedQuotes();

        Assertions.assertEquals(2, topRated.size());
        Assertions.assertEquals("Quote B", topRated.getFirst().quote());
        Assertions.assertEquals(4.5, topRated.getFirst().avgRating());
    }

    @Test
    void testRateQuote_affectsTopRated() {
        QuoteRequestDto quote = new QuoteRequestDto("Quote C", "Author C", 5);
        quoteService.rateQuote(quote);

        List<QuoteWithRating> top = quoteService.getHighestRatedQuotes();
        QuoteWithRating first = top.getFirst();
        Assertions.assertEquals("Quote C", first.quote());
        Assertions.assertEquals(5.0, first.avgRating());
    }

    private void saveQuote(String quote, String author, int totalRating, int ratingCount) {
        repository.save(RatedQuote.builder()
                .quote(quote)
                .author(author)
                .totalRating(totalRating)
                .ratingCount(ratingCount)
                .build());
    }
}

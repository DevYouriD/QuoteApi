package com.kabisa.quote_api;

import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.dto.QuoteRequestDto;
import com.kabisa.quote_api.api.model.dto.QuoteWithRating;
import com.kabisa.quote_api.api.model.entity.RatedQuote;
import com.kabisa.quote_api.api.repository.RatedQuoteRepository;
import com.kabisa.quote_api.api.service.DummyJsonQuoteService;
import com.kabisa.quote_api.api.service.QuoteService;
import com.kabisa.quote_api.api.service.ZenQuoteService;
import com.kabisa.quote_api.api.utility.QuoteUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class QuoteServiceTest {

    @Autowired
    private QuoteService quoteService;

    @MockitoBean
    private RatedQuoteRepository repository;

    @Mock
    private DummyJsonQuoteService dummyService;

    @Mock
    private ZenQuoteService zenService;

    @Test
    void testReturnsFastestQuote() {

        quoteService = new QuoteService(repository, dummyService, zenService);

        Quote fastQuote = new Quote("Fast quote", "Fast Author");
        Quote slowQuote = new Quote("Slow quote", "Slow Author");

        CompletableFuture<Quote> slowFuture = CompletableFuture.supplyAsync(
                () -> slowQuote,
                CompletableFuture.delayedExecutor(200, TimeUnit.MILLISECONDS)
        );

        CompletableFuture<Quote> fastFuture =
                CompletableFuture.completedFuture(fastQuote);

        when(dummyService.getRandomQuote()).thenReturn(slowFuture);
        when(zenService.getRandomQuote()).thenReturn(fastFuture);

        Quote result = quoteService.getRandomQuote();

        assertEquals("Fast quote", result.quote());
        assertEquals("Fast Author", result.author());
    }

    @Test
    void testShouldReturnFallback_whenBothAPIsFail() {
        quoteService = new QuoteService(repository, dummyService, zenService);
        Quote fallback = QuoteUtils.fallback(new RuntimeException("test"));

        when(dummyService.getRandomQuote()).thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("FAILED")));
        when(zenService.getRandomQuote()).thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("FAILED")));

        Quote result = quoteService.getRandomQuote();

        assertEquals(fallback.quote(), result.quote());
        assertEquals(fallback.author(), result.author());
    }

    @Test
    void testRateQuote_existingQuote() {
        String quoteText = "Test quote";
        String author = "Author";
        QuoteRequestDto quote = getQuoteDTO(quoteText, author);
        RatedQuote existing = buildRatedQuote(quoteText, author, 3, 1);

        when(repository.findByQuoteAndAuthor(quoteText, author))
                .thenReturn(Optional.of(existing));

        when(repository.save(any(RatedQuote.class)))
                .thenReturn(existing);

        quoteService.rateQuote(quote);

        assertEquals(6, existing.getTotalRating());
        assertEquals(2, existing.getRatingCount());
    }

    @Test
    void testRateQuote_newQuote() {
        String quoteText = "New quote";
        String author = "Author";
        QuoteRequestDto quote = getQuoteDTO(quoteText, author);
        RatedQuote savedRatedQuote = buildRatedQuote(quoteText, author, 3, 1);

        when(repository.findByQuoteAndAuthor(quote.quote(), quote.author()))
                .thenReturn(Optional.empty());

        when(repository.save(any(RatedQuote.class)))
                .thenReturn(savedRatedQuote);

        quoteService.rateQuote(quote);

        ArgumentCaptor<RatedQuote> captor = ArgumentCaptor.forClass(RatedQuote.class);
        verify(repository, times(1)).save(captor.capture());

        RatedQuote saved = captor.getValue();
        assertEquals("New quote", saved.getQuote());
        assertEquals("Author", saved.getAuthor());
        assertEquals(3, saved.getTotalRating());
        assertEquals(1, saved.getRatingCount());
    }

    @Test
    void testShouldReturnMappedHighestRatedQuotes() {

        RatedQuote q1 = buildRatedQuote("Quote 1", "Author 1", 14 ,3);
        RatedQuote q2 = buildRatedQuote("Quote 2", "Author 2", 9,2);

        List<RatedQuote> topRated = List.of(q1, q2);

        when(repository.findTopRatedByAverageRating(PageRequest.of(0, 5)))
                .thenReturn(topRated);

        QuoteService service = new QuoteService(repository, dummyService, zenService);

        List<QuoteWithRating> result = service.getHighestRatedQuotes();

        assertEquals(2, result.size());

        assertEquals("Quote 1", result.getFirst().quote());
        assertEquals("Author 1", result.getFirst().author());
        assertEquals(4.7, result.getFirst().avgRating());

        assertEquals("Quote 2", result.get(1).quote());
        assertEquals("Author 2", result.get(1).author());
        assertEquals(4.5, result.get(1).avgRating());

        verify(repository, times(1))
                .findTopRatedByAverageRating(PageRequest.of(0, 5));
    }

    private QuoteRequestDto getQuoteDTO(String quoteText, String author) {
        return new QuoteRequestDto(quoteText, author, 3);
    }

    private static RatedQuote buildRatedQuote(String quoteText, String author, int totalRating, int ratingCount) {
        return RatedQuote.builder()
                .quote(quoteText)
                .author(author)
                .totalRating(totalRating)
                .ratingCount(ratingCount)
                .build();
    }
}

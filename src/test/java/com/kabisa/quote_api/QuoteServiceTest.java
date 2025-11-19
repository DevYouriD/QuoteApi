package com.kabisa.quote_api;

import com.kabisa.quote_api.api.model.dto.QuoteRequestDto;
import com.kabisa.quote_api.api.model.entity.RatedQuote;
import com.kabisa.quote_api.api.repository.RatedQuoteRepository;
import com.kabisa.quote_api.api.service.QuoteService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class QuoteServiceTest {

    @MockitoBean
    private RatedQuoteRepository repository;

    @Autowired
    private QuoteService quoteService;

    @Test
    void testRateQuote_existingQuote() {
        String quoteText = "Test quote";
        String author = "Author";
        QuoteRequestDto quote = getQuoteDTO(quoteText, author);
        RatedQuote existing = buildRatedQuote(quoteText, author);

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
        RatedQuote savedRatedQuote = buildRatedQuote(quoteText, author);

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


    private QuoteRequestDto getQuoteDTO(String quoteText, String author) {
        return new QuoteRequestDto(quoteText, author, 3);
    }

    private static RatedQuote buildRatedQuote(String quoteText, String author) {
        return RatedQuote.builder()
                .quote(quoteText)
                .author(author)
                .totalRating(3)
                .ratingCount(1)
                .build();
    }
}

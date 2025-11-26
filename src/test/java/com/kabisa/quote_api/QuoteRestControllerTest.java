package com.kabisa.quote_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kabisa.quote_api.api.controller.QuoteRestController;
import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.dto.QuoteRequestDto;
import com.kabisa.quote_api.api.model.dto.QuoteWithRating;
import com.kabisa.quote_api.api.service.QuoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuoteRestController.class)
class QuoteRestControllerTest {

    public static final String BASE_PATH = "/api/v1/quote";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuoteService quoteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getRandomQuote_returnsQuote() throws Exception {
        Quote quote = new Quote("Test quote", "Author");
        when(quoteService.getRandomQuote()).thenReturn(quote);

        mockMvc.perform(get(BASE_PATH + "/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quote").value("Test quote"))
                .andExpect(jsonPath("$.author").value("Author"));
    }

    @Test
    void rateQuote_returnsQuoteWithRating() throws Exception {
        QuoteRequestDto request = new QuoteRequestDto("Hello world", "Author", 5);
        QuoteWithRating response = QuoteWithRating.builder()
                .quote("Hello world")
                .author("Author")
                .avgRating(5.0)
                .build();

        when(quoteService.rateQuote(request)).thenReturn(response);

        mockMvc.perform(post(BASE_PATH + "/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quote").value("Hello world"))
                .andExpect(jsonPath("$.author").value("Author"))
                .andExpect(jsonPath("$.avgRating").value(5.0));
    }

    @Test
    void getTopRatedQuotes_returnsListOfQuotes() throws Exception {
        List<QuoteWithRating> topQuotes = List.of(
                QuoteWithRating.builder().quote("Quote 1").author("Author 1").avgRating(4.7).build(),
                QuoteWithRating.builder().quote("Quote 2").author("Author 2").avgRating(4.5).build()
        );

        when(quoteService.getHighestRatedQuotes()).thenReturn(topQuotes);

        mockMvc.perform(get(BASE_PATH + "/top-rated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].quote").value("Quote 1"))
                .andExpect(jsonPath("$[0].author").value("Author 1"))
                .andExpect(jsonPath("$[0].avgRating").value(4.7))
                .andExpect(jsonPath("$[1].quote").value("Quote 2"))
                .andExpect(jsonPath("$[1].author").value("Author 2"))
                .andExpect(jsonPath("$[1].avgRating").value(4.5));
    }
}

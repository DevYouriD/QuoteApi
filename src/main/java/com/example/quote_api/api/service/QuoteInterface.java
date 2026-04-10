package com.example.quote_api.api.service;

import com.example.quote_api.api.model.dto.Quote;
import java.util.concurrent.CompletableFuture;

public interface QuoteInterface {

    CompletableFuture<Quote> getRandomQuote();

}
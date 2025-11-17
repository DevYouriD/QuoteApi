package com.kabisa.quote_api.service;

import com.kabisa.quote_api.model.Quote;
import java.util.concurrent.CompletableFuture;

public interface QuoteInterface {

    CompletableFuture<Quote> getRandomQuote();

}
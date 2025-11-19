package com.kabisa.quote_api.api.model.dto;

import lombok.Builder;

@Builder
public record QuoteWithRating(String quote, String author, Double avgRating) { }

package com.kabisa.quote_api.api.model.dto;

import lombok.Builder;

@Builder
public record Quote(String quote, String author) { }

package com.kabisa.quote_api.api.utility;

import com.kabisa.quote_api.api.model.dto.Quote;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuoteUtils {

    private static final Quote FALLBACK_QUOTE = Quote.builder()
            .quote("Just because something works, doesn't mean it can't be improved.")
            .author("Youri Dera")
            .build();

    public static Quote fallback(Throwable ex) {
        log.warn("Using fallback quote due to: {}", ex.getMessage());
        return FALLBACK_QUOTE;
    }

}

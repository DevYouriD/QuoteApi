package com.kabisa.quote_api.api.utility;

public final class Constants {

    /**
     * Paths.
     */
    public static final String REST_BASE_PATH = "/api/v1/quote";

    public static final String GET_RANDOM_QUOTE_PATH = "/random";

    public static final String RATE_QUOTE_PATH = "/rate";

    public static final String GET_TOP_RATED_QUOTES_PATH = "/top-rated";

    public static final String DUMMY_JSON_PATH = "https://dummyjson.com/quotes/random";

    public static final String ZEN_QUOTE_PATH = "https://zenquotes.io/api/random";


    /**
     * Error messages.
     */
    public static final String ZEN_QUOTE_API_ERROR = "Unrecognized API request. Visit zenquotes.io for documentation.";

    private Constants() { /* Empty constructor to prohibit initialisation. */ }
}

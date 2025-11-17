package com.kabisa.quote_api.model.mapper;

import com.kabisa.quote_api.model.Quote;
import com.kabisa.quote_api.model.zenquotes.ZenQuote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ZenToQuoteMapper {

    @Mapping(source = "q", target = "quote")
    @Mapping(source = "a", target = "author")
    Quote zenToQuote(ZenQuote quote);

}

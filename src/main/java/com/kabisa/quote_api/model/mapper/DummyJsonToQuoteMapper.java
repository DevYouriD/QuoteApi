package com.kabisa.quote_api.model.mapper;

import com.kabisa.quote_api.model.Quote;
import com.kabisa.quote_api.model.dummyjson.DummyJsonQuote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DummyJsonToQuoteMapper {
    Quote dummyJsonToQuote(DummyJsonQuote quote);
}

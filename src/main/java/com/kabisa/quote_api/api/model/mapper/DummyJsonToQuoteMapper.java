package com.kabisa.quote_api.api.model.mapper;

import com.kabisa.quote_api.api.model.dto.Quote;
import com.kabisa.quote_api.api.model.dto.dummyjson.DummyJsonQuote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DummyJsonToQuoteMapper {
    Quote dummyJsonToQuote(DummyJsonQuote quote);
}

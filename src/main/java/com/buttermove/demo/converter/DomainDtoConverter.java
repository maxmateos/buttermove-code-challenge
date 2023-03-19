package com.buttermove.demo.converter;

public interface DomainDtoConverter<S, T> {
  T convert(S source);
}

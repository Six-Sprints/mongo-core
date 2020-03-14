package com.sixsprints.core.converters;

import java.io.IOException;
import java.time.LocalTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LocalTimeSerializerAsString extends StdSerializer<LocalTime> {

  private static final long serialVersionUID = 4111856141215422046L;

  public static final LocalTimeSerializerAsString INSTANCE = new LocalTimeSerializerAsString();

  private LocalTimeSerializerAsString() {
    super(LocalTime.class);
  }

  @Override
  public void serialize(LocalTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
    generator.writeString(value.toString());
  }
}
package io.github.koryl.contacts.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ContactTypeSerializer extends JsonSerializer<ContactType> {

    public void serialize(ContactType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        gen.writeStartObject();
        gen.writeFieldName(value.toString());
        gen.writeEndObject();
    }
}

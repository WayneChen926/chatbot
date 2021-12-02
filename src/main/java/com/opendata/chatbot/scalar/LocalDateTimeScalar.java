package com.opendata.chatbot.scalar;

import com.netflix.graphql.dgs.DgsScalar;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@DgsScalar(name = "LocalDateTime")
public class LocalDateTimeScalar implements Coercing<LocalDateTime, String> {
    @Override
    public String serialize(@NotNull Object input) throws CoercingSerializeException {
        if (input instanceof LocalDateTime) {
            return ((LocalDateTime) input).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            throw new CoercingSerializeException("Not a valid DateTime");
        }
    }

    @NotNull
    @Override
    public LocalDateTime parseValue(@NotNull Object input) throws CoercingParseValueException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse((String) input, dateTimeFormatter);
    }

    @NotNull
    @Override
    public LocalDateTime parseLiteral(@NotNull Object input) throws CoercingParseLiteralException {
        if (input instanceof StringValue) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(((StringValue) input).getValue(), dateTimeFormatter);
        }
        throw new CoercingParseLiteralException("Value is not a valid ISO date time");
    }
}


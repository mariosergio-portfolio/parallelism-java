package com.mycompany.portfolio.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter INSTANT_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss SSSSSSSS'Z'").withZone(ZoneOffset.UTC);

    @Bean
    public JsonMapperBuilderCustomizer instantFormatterCustomizer() {
        return (JsonMapper.Builder builder) -> {
            SimpleModule module = new SimpleModule();
            module.addSerializer(new StdSerializer<Instant>(Instant.class) {
                @Override
                public void serialize(Instant value, JsonGenerator gen, SerializationContext provider) {
                    gen.writeString(INSTANT_FORMATTER.format(value));
                }
            });
            builder.addModule(module);
        };
    }
}

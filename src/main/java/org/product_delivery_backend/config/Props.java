package org.product_delivery_backend.config;

import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.stream.Stream;

@Slf4j
public class Props {
static private final String propsFilename = ".properties";
static private Properties props;

private Props()
{
}

static Properties get()
{
        if(props != null)
                return props;

        props = new Properties();

        try (var reader = new FileReader(propsFilename)) {
                props.load(reader);

                return props;
        } catch (IOException e) {
                log.error("{}: {}", propsFilename, e.getMessage());

                throw new RuntimeException(e);
        }
}

static public boolean getBool(String key)
{
        var value = props.getProperty(key, "false");

        return Stream.of("yes", "true", "1", "on", "ok")
                .filter(s -> value.compareToIgnoreCase(s) == 0)
                .map(s -> true)
                .findFirst()
                .orElse(false);
}

static public long getLong(String key)
{
        var sValue = props.getProperty(key, "0");
        long value = 0;

        try {
                value = Long.parseLong(sValue);
        } catch (NumberFormatException ignored) {
        }

        return value;
}

static public int getInt(String key)
{
        return (int) getLong(key);
}
}
package org.parser.service.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesFactory {

    private static final Properties properties = new Properties();

    static {
        try (InputStream in = PropertiesFactory.class.getClassLoader().getResourceAsStream("parser.properties")) {

            if (in == null)
                throw new RuntimeException("Could not find parser properties file");
            properties.load(in);

        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static Properties create() {
        return properties;
    }
}

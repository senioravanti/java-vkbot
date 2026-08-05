package ru.senioravanti.vkbot.utils;

import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.InputStream;
import java.util.*;

import static java.util.stream.Collectors.toMap;

import static ru.senioravanti.vkbot.config.JacksonConfig.OBJECT_TYPE_REFERENCE;

public class YamlResourceBundle extends ResourceBundle {
    private static final YAMLMapper YAML = new YAMLMapper();

    private final Map<String, Object> entries;

    public YamlResourceBundle(InputStream docs) {
        this(Parser.parseDocs(docs));
    }

    private YamlResourceBundle(Map<String, Object> entries) {
        this.entries = entries;
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(entries.keySet());
    }

    @Override
    protected Set<String> handleKeySet() {
        return entries.keySet();
    }

    @Override
    protected Object handleGetObject(String key) {
        return entries.get(key);
    }

    public static class Control extends ResourceBundle.Control {
        private static final List<String> FORMAT_YAML = List.of("yaml", "yml");

        @Override
        public List<String> getFormats(String baseName) {
            return FORMAT_YAML;
        }

        @Override
        public ResourceBundle newBundle(
            String baseName,
            Locale locale,
            String format,
            ClassLoader loader,
            boolean reload
        ) {
            if (!getFormats(baseName).contains(format)) throw new IllegalArgumentException("Unknown formatData: " + format);

            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, format);

            var resource = loader.getResource(resourceName);
            if (resource == null) return null;

            try {
                var connection = resource.openConnection();
                if (reload) connection.setUseCaches(false);
                try (var is = connection.getInputStream()) {
                    return new YamlResourceBundle(is);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class Parser {
        static Map<String, Object> parseDocs(InputStream input) {
            var root = YAML.readValue(input, OBJECT_TYPE_REFERENCE);
            var entries = new LinkedHashMap<String, Object>();
            root.forEach((k, v) -> flatten(k, v, entries));
            return entries.entrySet().stream()
                .filter(it -> it.getValue() != null)
                .collect(toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (a, b) -> b,
                    LinkedHashMap::new
                ));
        }

        private static void flatten(String key, Object value, Map<String, Object> result) {
            if (value instanceof Map<?, ?> map) {
                map.forEach((k, v) -> flatten(key + "." + k, v, result));
                return;
            }
            if (value instanceof List<?> list) {
                result.put(key, list.stream()
                    .filter(it -> it instanceof String)
                    .toArray()
                );
                for (int i = 0; i < list.size(); i++) {
                    flatten(key + "[" + i + "]", list.get(i), result);
                }
                return;
            }
            result.put(key, value);
        }
    }
}

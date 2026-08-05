package ru.senioravanti.vkbot.utils;


import java.net.http.HttpRequest;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import ru.senioravanti.commons.files.FileUtils;

// https://kodejava.org/how-do-i-upload-a-file-using-multipart-with-java-11-httpclient/
// https://github.com/ralscha/blog2019/blob/master/java11httpclient/client/src/main/java/ch/rasc/httpclient/File.java#L69
public class HttpClientUtils {
    private static final String CRLF = "\r\n";

    private HttpClientUtils() {
    }

    public static HttpRequest.BodyPublisher ofMimeMultipartData(Map<String, Object> data, String boundary) throws IOException {
        var byteArrays = new ArrayList<byte[]>();
        var separator = "--" + boundary + CRLF
            + "Content-Disposition: form-data";
        for (var it : data.entrySet()) {
            var part = separator
                + "; name=\"%s\"".formatted(it.getKey());
            switch (it.getValue()) {
                case Path path -> {
                    byteArrays.add((part
                        + "; filename=\"%s\"".formatted(path.getFileName())
                        + CRLF
                        + "Content-Type: " + FileUtils.guessContentType(path)
                        + CRLF
                        + CRLF
                    ).getBytes(StandardCharsets.UTF_8));
                    byteArrays.add(Files.readAllBytes(path));
                }
                case InputStream is -> {
                    var bytes = is.readAllBytes();
                    var mimeType = FileUtils.TIKA.detect(bytes);
                    // С точкой: "image/png" -> ".png"
                    var ext = FileUtils.getExtension(mimeType);
                    byteArrays.add((part
                        + "; filename=\"file%s\"".formatted(ext)
                        + CRLF
                        + "Content-Type: " + mimeType
                        + CRLF
                        + CRLF
                    ).getBytes(StandardCharsets.UTF_8));
                    byteArrays.add(bytes);
                }
                default -> byteArrays.add((part
                    + CRLF
                    + CRLF
                    + it.getValue()
                ).getBytes(StandardCharsets.UTF_8));
            }
            byteArrays.add(CRLF.getBytes(StandardCharsets.UTF_8));
        }
        byteArrays.add(("--" + boundary + "--" + CRLF).getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}

package itu.mg.rh.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FileHandler {
    @Autowired
    private ResourceLoader resourceLoader;

    public String readFile(String fileName) {
        try {
            Resource resource = resourceLoader.getResource("classpath:/data/" + fileName);
            InputStream inputStream = resource.getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            return new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(String.format("An error occured when reading file %s. Messages: %s ", fileName,e.getMessage()));
        }
    }
}

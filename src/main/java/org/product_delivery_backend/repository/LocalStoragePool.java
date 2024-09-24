package org.product_delivery_backend.repository;

import lombok.extern.slf4j.Slf4j;
import org.product_delivery_backend.config.UseProps;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
public class LocalStoragePool
        implements StoragePool, UseProps {
private final File prefix;

public LocalStoragePool()
{
        this.prefix = new File(props.getProperty(fileStorage, "/var/tmp/files"));
        prefix.mkdirs();
}

@Override
public long store(InputStream in, String filename)
{
        String destFileName = prefix.getAbsolutePath() + '/' + filename;

        try (FileOutputStream os = new FileOutputStream(destFileName)) {
                return in.transferTo(os);
        } catch (IOException e) {
                throw new RuntimeException(e);
        }
}

@Override
public InputStream load(String filename)
{
        String filePath = prefix.getAbsolutePath() + '/' + filename;

        try {
                return new FileInputStream(filePath);
        } catch (IOException e) {
                log.error("Can't load {}: {}", filePath, e.getMessage());

                throw new RuntimeException(e);
        }
}
}

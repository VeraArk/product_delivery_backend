package org.product_delivery_backend.repository;

import lombok.extern.slf4j.Slf4j;
import org.product_delivery_backend.config.Config;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
public class FSPool
        implements StoragePool {
private final File poolRoot;

public FSPool(Config config)
{
        this.poolRoot = new File(config.getStorageRoot());
        poolRoot.mkdirs();
}

@Override
public String path(String filename)
{
        return poolRoot.getAbsolutePath() + '/' + filename;
}

@Override
public long store(InputStream in, String filename)
{
        String filePath = path(filename);

        try (FileOutputStream os = new FileOutputStream(filePath)) {
                return in.transferTo(os);
        } catch (IOException e) {
                log.error("Can't store {}: {}", filePath, e.getMessage());

                throw new RuntimeException(e);
        }
}

@Override
public InputStream load(String filename)
{
        String filePath = path(filename);

        try {
                return new FileInputStream(filePath);
        } catch (IOException e) {
                log.error("Can't load {}: {}", filePath, e.getMessage());

                throw new RuntimeException(e);
        }
}
}

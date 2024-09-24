package org.product_delivery_backend.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product_delivery_backend.common.Pair;
import org.product_delivery_backend.config.Config;
import org.product_delivery_backend.entity.FileMetadata;
import org.product_delivery_backend.repository.FileMetadataRepo;
import org.product_delivery_backend.repository.StoragePool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@NoArgsConstructor
public class FileServiceImpl implements FileService {
@Autowired
private StoragePool localStoragePool;

@Autowired
private FileMetadataRepo repo;

@Autowired
private Config config;

@Override
public UUID store(MultipartFile file)
{
        if(file == null || file.isEmpty() || file.getSize() > 1024 * config.getMaxFileSize()) {
                throw new RuntimeException("File is empty or out of bounds");
        }

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // TODO add keyword(s) handling
        FileMetadata fileMetadata = new FileMetadata(
                id,
                now,
                file.getSize(),
                file.getOriginalFilename(),
                file.getContentType(),
                null
        );

        // TODO create exceptions
        try {
                // 1. store the file
                long size = localStoragePool.store(file.getInputStream(), id.toString());

                log.info("file: {}, size: {}k", file.getOriginalFilename(), size / 1024);

                // 2. store the metadata
                FileMetadata saved = repo.save(fileMetadata);

                return saved.getId();
        } catch (IOException e) {
                throw new RuntimeException(e);
        }
}

@Override
public Pair<Resource, FileMetadata> load(UUID id)
{
        Optional<FileMetadata> meta = repo.findById(id);

        if(meta.isEmpty())
                throw new RuntimeException("File metadata not found");

        Resource resource = new InputStreamResource(localStoragePool.load(id.toString()));

        if(!resource.exists())
                throw new RuntimeException("File not found");

        return new Pair<>(resource, meta.get());
}
}

package org.product_delivery_backend.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product_delivery_backend.common.Pair;
import org.product_delivery_backend.config.UseProps;
import org.product_delivery_backend.entity.FileMetadata;
import org.product_delivery_backend.repository.FileMetadataRepo;
import org.product_delivery_backend.repository.StoragePool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@NoArgsConstructor
public class FileServiceImpl implements FileService, UseProps {
@Autowired
private StoragePool localStoragePool;

@Autowired
private FileMetadataRepo repo;

@Override
public UUID store(MultipartFile file)
{
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

        try {
                long size = localStoragePool.store(file.getInputStream(), id.toString());

                log.info("file: {}, size: {}k", file.getOriginalFilename(), size / 1024);

                FileMetadata saved = repo.save(fileMetadata);

                return  saved.getId();
        } catch (IOException e) {
                throw new RuntimeException(e);
        }
}

@Override
public Pair<Resource, FileMetadata> load(UUID id)
{
        return null;
}
}

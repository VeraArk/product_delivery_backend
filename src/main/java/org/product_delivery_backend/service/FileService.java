package org.product_delivery_backend.service;

import org.product_delivery_backend.common.Pair;
import org.product_delivery_backend.entity.FileMetadata;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileService {
        UUID store(MultipartFile file);
        Pair<Resource, FileMetadata> load(UUID id);
}

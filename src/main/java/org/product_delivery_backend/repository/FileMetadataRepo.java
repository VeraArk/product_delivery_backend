package org.product_delivery_backend.repository;

import org.product_delivery_backend.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileMetadataRepo extends JpaRepository<FileMetadata, UUID> {
}

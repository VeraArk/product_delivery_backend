package org.product_delivery_backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "file_metadata")
@Schema(description = "Metadata for a file stored in the system.")
public class FileMetadata {
        @Id
        //@GeneratedValue(strategy = GenerationType.UUID)
        @Schema(description = "Unique identifier of the file.", example = "e32d21ab-22e9-4caa-8c31-6d86ccdc0bb2", required = true)
        private UUID id;
        @Schema(description = "Creation time of the file.", example = "2024-10-17T12:00:00")
        private LocalDateTime ctime;
        @Schema(description = "Size of the file in bytes.", example = "1024")
        private long size;
        @Schema(description = "Name of the file.", example = "example.txt")
        private String fileName;
        @Schema(description = "MIME type of the file.", example = "text/plain")
        private String mimeType;
        @Schema(description = "Keywords associated with the file.", example = "example, test, file")
        private String keyword;
        }

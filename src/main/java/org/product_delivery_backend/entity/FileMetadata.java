package org.product_delivery_backend.entity;

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
public class FileMetadata {
@Id
//@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
private LocalDateTime ctime;
private long size;
private String fileName;
private String mimeType;
private String keyword;
}

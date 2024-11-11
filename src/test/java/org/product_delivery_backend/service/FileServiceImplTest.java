package org.product_delivery_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.product_delivery_backend.common.Pair;
import org.product_delivery_backend.config.Config;
import org.product_delivery_backend.entity.FileMetadata;
import org.product_delivery_backend.repository.FileMetadataRepo;
import org.product_delivery_backend.repository.StoragePool;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileServiceImplTest {

    @Mock
    private StoragePool localStoragePool;

    @Mock
    private FileMetadataRepo repo;

    @Mock
    private Config config;

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков
    }

    @Test
    void store_ValidFile_SuccessfulSave() throws IOException {
        // Мокируем MultipartFile
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L); // Размер файла 1 KB
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getContentType()).thenReturn("text/plain");
        when(config.getMaxFileSize()).thenReturn(1L); // Максимальный размер 1 MB
        when(localStoragePool.store(any(), anyString())).thenReturn(1024L); // Храним файл

        // Мокируем сохранение метаданных файла
        FileMetadata fileMetadata = new FileMetadata(
                UUID.randomUUID(),
                null,
                1024L,
                "test.txt",
                "text/plain",
                null
        );
        when(repo.save(any(FileMetadata.class))).thenReturn(fileMetadata);

        // Вызываем метод store
        UUID result = fileService.store(file);

        // Проверяем результат
        assertNotNull(result);
        verify(localStoragePool).store(any(), anyString());
        verify(repo).save(any(FileMetadata.class));
    }

    @Test
    void store_FileTooBig_ThrowsException() {
        // Мокируем слишком большой файл
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(2048L); // Размер файла 2 MB
        when(config.getMaxFileSize()).thenReturn(1L); // Максимальный размер 1 MB

        // Вызываем метод и проверяем, что он бросает исключение
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileService.store(file);
        });
        assertEquals("File is empty or too big", exception.getMessage());
        verify(localStoragePool, never()).store(any(), anyString());
        verify(repo, never()).save(any(FileMetadata.class));
    }

    @Test
    void store_EmptyFile_ThrowsException() {
        // Мокируем пустой файл
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        // Проверяем, что метод бросает исключение для пустого файла
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileService.store(file);
        });
        assertEquals("File is empty or too big", exception.getMessage());
        verify(localStoragePool, never()).store(any(), anyString());
        verify(repo, never()).save(any(FileMetadata.class));
    }

    @Test
    void load_FileExists_ReturnsFileAndMetadata() throws IOException {
        // Мокируем существующий файл и метаданные
        UUID fileId = UUID.randomUUID();
        FileMetadata metadata = new FileMetadata(
                fileId,
                null,
                1024L,
                "test.txt",
                "text/plain",
                null
        );
        when(repo.findById(fileId)).thenReturn(Optional.of(metadata));

        ByteArrayInputStream inputStream = new ByteArrayInputStream("file content".getBytes());
        when(localStoragePool.load(fileId.toString())).thenReturn(inputStream);

        // Вызываем метод load
        Pair<Resource, FileMetadata> result = fileService.load(fileId);

        // Проверяем результат
        assertNotNull(result);
        assertEquals("test.txt", result.getSecond().getFileName());
        assertTrue(result.getFirst() instanceof InputStreamResource);
        verify(repo).findById(fileId);
        verify(localStoragePool).load(fileId.toString());
    }

    @Test
    void load_FileMetadataNotFound_ThrowsException() {
        // Мокируем отсутствие метаданных файла
        UUID fileId = UUID.randomUUID();
        when(repo.findById(fileId)).thenReturn(Optional.empty());

        // Проверяем, что метод бросает исключение
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileService.load(fileId);
        });

        // Проверяем, что сообщение об ошибке содержит ключевую фразу
        assertTrue(exception.getMessage().contains("metadata not found"));

        verify(localStoragePool, never()).load(fileId.toString());
    }

    @Test
    void load_FileNotFound_ThrowsException() throws IOException {
        // Мокируем существующие метаданные, но файл не найден в хранилище
        UUID fileId = UUID.randomUUID();
        FileMetadata metadata = new FileMetadata(
                fileId,
                null,
                1024L,
                "test.txt",
                "text/plain",
                null
        );
        when(repo.findById(fileId)).thenReturn(Optional.of(metadata));

        // Мокируем, что файл не найден и выбрасывается исключение с правильным сообщением
        when(localStoragePool.load(fileId.toString())).thenThrow(new RuntimeException("File " + fileId + " not found"));

        // Проверяем, что метод выбрасывает исключение
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileService.load(fileId);
        });

        // Проверяем точное сообщение исключения
        assertEquals("File " + fileId + " not found", exception.getMessage());

        verify(localStoragePool).load(fileId.toString());
    }
}

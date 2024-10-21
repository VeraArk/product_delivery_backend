package org.product_delivery_backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.product_delivery_backend.common.Pair;
import org.product_delivery_backend.entity.FileMetadata;
import org.product_delivery_backend.exceptions.InvalidDataException;
import org.product_delivery_backend.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

public class FileControllerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void upload_ValidFile_ReturnsFileId() {
        // Мокируем MultipartFile
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.txt");

        UUID fileId = UUID.randomUUID();
        when(fileService.store(file)).thenReturn(fileId);

        // Вызываем метод upload
        ResponseEntity<String> response = fileController.upload(file);

        // Проверяем результат
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(fileId.toString(), response.getBody());
    }

    @Test
    void upload_EmptyFile_ThrowsInvalidDataException() {
        // Мокируем пустой файл
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        // Проверяем, что метод выбрасывает исключение
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            fileController.upload(file);
        });

        assertEquals("File cannot be empty.", exception.getMessage());
    }

    @Test
    void download_ValidFileId_ReturnsResource() throws Exception {
        UUID fileId = UUID.randomUUID();
        Resource resource = mock(Resource.class);

        // Создаем мокированные метаданные файла
        FileMetadata metadata = new FileMetadata(
                fileId,
                null,
                1024L,
                "test.txt",
                "text/plain",
                null
        );

        // Мокируем возврат данных из сервиса
        when(fileService.load(fileId)).thenReturn(new Pair<>(resource, metadata));

        // Вызываем метод download
        ResponseEntity<Resource> response = fileController.download(fileId.toString());

        // Проверяем результат
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(resource, response.getBody());

        // Проверка заголовка Content-Disposition
        assertEquals("attachment; filename=\"test.txt\"", response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0));
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
    }

    @Test
    void download_InvalidFileId_ThrowsException() {
        String invalidId = "invalid-id";

        // Проверяем, что метод выбрасывает исключение
        assertThrows(IllegalArgumentException.class, () -> {
            fileController.download(invalidId);
        });
    }

    @Test
    void download_FileNotFound_ThrowsNotFoundException() throws Exception {
        UUID fileId = UUID.randomUUID();
        when(fileService.load(fileId)).thenThrow(new RuntimeException("File " + fileId + " not found"));

        // Проверяем, что метод выбрасывает исключение
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileController.download(fileId.toString());
        });

        assertEquals("File " + fileId + " not found", exception.getMessage());
    }
}

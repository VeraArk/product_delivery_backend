package org.product_delivery_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.product_delivery_backend.entity.FileMetadata;
import org.product_delivery_backend.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@Tag(name = "File controller")
public class FileController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "Upload a file",
            description = "Upload a file to the server.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Invalid file format", content = @Content),
            @ApiResponse(responseCode = "413", description = "File size too large", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        var id = fileService.store(file);

        return ResponseEntity.ok(id.toString());
    }

    @Operation(summary = "Download a file",
            description = "Download the file associated with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "400", description = "Invalid file ID", content = @Content),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") String fileId) throws FileNotFoundException {
        UUID id = UUID.fromString(fileId);
        var got = fileService.load(id);
        Resource resource = got.getFirst();
        FileMetadata media = got.getSecond();

        HttpHeaders headers = new HttpHeaders();

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + media.getFileName() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}

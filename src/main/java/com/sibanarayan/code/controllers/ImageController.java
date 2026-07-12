package com.sibanarayan.code.controllers;


import com.sibanarayan.code.services.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;



    // TODO: gate this behind your admin auth, same as your other admin-only routes
    // (e.g. @PreAuthorize("hasRole('ADMIN')") if you're on Spring Security method security).
    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, String> result = imageService.uploadImage(file);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to upload image"));
        }
    }
    @DeleteMapping("/image")
    public ResponseEntity<?> deleteImage(@RequestParam("publicId") String publicId) {
        try {
            imageService.deleteImage(publicId);
            return ResponseEntity.ok(Map.of("deleted", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete image"));
        }
    }
}

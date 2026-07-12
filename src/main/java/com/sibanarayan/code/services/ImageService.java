package com.sibanarayan.code.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5 MB

    private final Cloudinary cloudinary;

    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    public Map<String, String> uploadImage(MultipartFile file) throws IOException {
        validate(file);

        Map<String, Object> uploadOptions = ObjectUtils.asMap(
                "folder", "hii-coders/test-cases",
                "resource_type", "image"
        );

        @SuppressWarnings("rawtypes")
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");
        return Map.of("url", url, "publicId", publicId);
    }

    public void deleteImage(String publicId) throws IOException {
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("publicId is required");
        }

        @SuppressWarnings("rawtypes")
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        String status = (String) result.get("result"); // "ok" | "not found"

        if (!"ok".equals(status) && !"not found".equals(status)) {
            throw new IOException("Failed to delete image from Cloudinary: " + status);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File must be smaller than 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }
}
package com.obysoft.faithOS.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.obysoft.faithOS.dto.ChurchFileResponse;
import com.obysoft.faithOS.entity.ChurchFile;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.ChurchFileRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ChurchFileService {
    private final ChurchFileRepository repository;
    private final CurrentChurchService current;
    private final Path storageRoot;

    public ChurchFileService(ChurchFileRepository repository, CurrentChurchService current,
            @Value("${app.files.storage-path:uploads}") String storagePath) {
        this.repository = repository;
        this.current = current;
        this.storageRoot = Path.of(storagePath).toAbsolutePath().normalize();
    }

    @PostConstruct
    void initializeStorage() throws IOException { Files.createDirectories(storageRoot); }

    @Transactional(readOnly = true)
    public List<ChurchFileResponse> all() {
        return repository.findAllByChurchIdOrderByCreatedAtDesc(current.church().getId())
                .stream().map(this::response).toList();
    }

    @Transactional
    public ChurchFileResponse upload(MultipartFile upload) {
        if (upload.isEmpty()) throw new IllegalArgumentException("Select a non-empty file.");
        String originalName = safeOriginalName(upload.getOriginalFilename());
        String storedName = UUID.randomUUID() + extension(originalName);
        Path target = storageRoot.resolve(storedName).normalize();
        if (!target.startsWith(storageRoot)) throw new IllegalArgumentException("Invalid file name.");

        try {
            Files.copy(upload.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            User user = current.user();
            ChurchFile file = new ChurchFile();
            file.setOriginalName(originalName);
            file.setStoredName(storedName);
            file.setContentType(upload.getContentType());
            file.setSize(upload.getSize());
            file.setChurch(user.getChurch());
            file.setUploadedBy(user);
            return response(repository.save(file));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to store the uploaded file.", exception);
        }
    }

    @Transactional(readOnly = true)
    public FileDownload download(Long id) {
        ChurchFile file = find(id);
        try {
            Resource resource = new UrlResource(storageRoot.resolve(file.getStoredName()).toUri());
            if (!resource.exists() || !resource.isReadable()) throw new ResourceNotFoundException("Stored file is unavailable.");
            return new FileDownload(resource, file.getOriginalName(),
                    file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        } catch (IOException exception) {
            throw new ResourceNotFoundException("Stored file is unavailable.");
        }
    }

    @Transactional
    public void delete(Long id) {
        ChurchFile file = find(id);
        repository.delete(file);
        try { Files.deleteIfExists(storageRoot.resolve(file.getStoredName())); }
        catch (IOException exception) { throw new IllegalStateException("Unable to delete the stored file.", exception); }
    }

    private ChurchFile find(Long id) {
        return repository.findByIdAndChurchId(id, current.church().getId())
                .orElseThrow(() -> new ResourceNotFoundException("File not found."));
    }

    private ChurchFileResponse response(ChurchFile file) {
        User uploader = file.getUploadedBy();
        String uploadedBy = uploader == null ? null : uploader.getFirstName() + " " + uploader.getLastName();
        return new ChurchFileResponse(file.getId(), file.getOriginalName(), file.getContentType(),
                file.getSize(), file.getCreatedAt(), uploadedBy);
    }

    private String safeOriginalName(String name) {
        if (name == null || name.isBlank()) return "file";
        String normalized = name.replace('\\', '/');
        String baseName = normalized.substring(normalized.lastIndexOf('/') + 1).trim();
        return baseName.isEmpty() ? "file" : baseName.substring(0, Math.min(baseName.length(), 255));
    }

    private String extension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot < 0 || name.length() - dot > 21) return "";
        return name.substring(dot).replaceAll("[^A-Za-z0-9.]", "");
    }
}

package com.obysoft.faithOS.controller;

import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.obysoft.faithOS.dto.ChurchFileResponse;
import com.obysoft.faithOS.service.ChurchFileService;

@RestController
@RequestMapping("/api/files")
public class ChurchFileController {
    private final ChurchFileService service;
    public ChurchFileController(ChurchFileService service) { this.service = service; }

    @GetMapping public List<ChurchFileResponse> all() { return service.all(); }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN', 'PASTOR', 'LEADER')")
    public ResponseEntity<ChurchFileResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.upload(file));
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<org.springframework.core.io.Resource> download(@PathVariable Long id) {
        var download = service.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(download.originalName()).build().toString())
                .body(download.resource());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN', 'PASTOR', 'LEADER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}

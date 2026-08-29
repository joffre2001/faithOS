package com.obysoft.faithOS.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.Role;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.ChurchRepository;
import com.obysoft.faithOS.repository.UserRepository;

@Service
public class MediaImageService {
    private static final long MAX_BYTES = 2L * 1024 * 1024;
    private static final int MAX_DIMENSION = 4096;
    private final ChurchRepository churches;
    private final UserRepository users;
    private final CurrentChurchService current;

    public MediaImageService(ChurchRepository churches, UserRepository users, CurrentChurchService current) {
        this.churches = churches;
        this.users = users;
        this.current = current;
    }

    @Transactional
    public void saveChurchLogo(MultipartFile upload) {
        User actor = current.user();
        if (actor.getRole() != Role.CHURCH_ADMIN && actor.getRole() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only a church administrator can change the church logo.");
        }
        Church church = current.church();
        StoredImage image = store(upload);
        church.setLogoData(image.data());
        church.setLogoContentType(image.contentType());
        churches.save(church);
    }

    @Transactional(readOnly = true)
    public FileDownload churchLogo() {
        Church church = current.church();
        return load(church.getLogoData(), church.getLogoContentType(), "church-logo");
    }

    @Transactional
    public void saveProfilePicture(Long userId, MultipartFile upload) {
        User actor = current.user();
        User target = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found."));
        boolean sameUser = actor.getId().equals(target.getId());
        boolean sameChurchAdmin = actor.getRole() == Role.CHURCH_ADMIN
                && actor.getChurch().getId().equals(target.getChurch().getId());
        if (target.getRole() == Role.SUPER_ADMIN && actor.getRole() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only a super administrator can change this profile picture.");
        }
        if (actor.getRole() != Role.SUPER_ADMIN && !sameUser && !sameChurchAdmin) {
            throw new AccessDeniedException("You cannot change this profile picture.");
        }
        StoredImage image = store(upload);
        target.setProfilePictureData(image.data());
        target.setProfilePictureContentType(image.contentType());
        users.save(target);
    }

    @Transactional(readOnly = true)
    public FileDownload profilePicture(Long userId) {
        User actor = current.user();
        User target = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found."));
        if (actor.getRole() != Role.SUPER_ADMIN
                && !actor.getChurch().getId().equals(target.getChurch().getId())) {
            throw new AccessDeniedException("You cannot view this profile picture.");
        }
        return load(target.getProfilePictureData(), target.getProfilePictureContentType(), "profile-picture");
    }

    private StoredImage store(MultipartFile upload) {
        if (upload == null || upload.isEmpty()) throw new IllegalArgumentException("Select an image.");
        if (upload.getSize() > MAX_BYTES) throw new IllegalArgumentException("Image must be 2 MB or smaller.");
        try {
            byte[] bytes = upload.getBytes();
            String type = inspect(bytes);
            return new StoredImage(bytes, type);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to store the image.", exception);
        }
    }

    private String inspect(byte[] bytes) throws IOException {
        try (ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            if (stream == null) throw new IllegalArgumentException("Upload a valid PNG or JPEG image.");
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) throw new IllegalArgumentException("Upload a valid PNG or JPEG image.");
            ImageReader reader = readers.next();
            try {
                reader.setInput(stream, true, true);
                String format = reader.getFormatName().toLowerCase();
                if (!format.equals("png") && !format.equals("jpeg") && !format.equals("jpg")) {
                    throw new IllegalArgumentException("Only PNG and JPEG images are supported.");
                }
                if (reader.getWidth(0) > MAX_DIMENSION || reader.getHeight(0) > MAX_DIMENSION) {
                    throw new IllegalArgumentException("Image dimensions must not exceed 4096 × 4096 pixels.");
                }
                return format.equals("png") ? "image/png" : "image/jpeg";
            } finally { reader.dispose(); }
        }
    }

    private FileDownload load(byte[] data, String contentType, String downloadName) {
        if (data == null || data.length == 0) throw new ResourceNotFoundException("Image not found.");
        Resource resource = new ByteArrayResource(data);
        return new FileDownload(resource, downloadName,
                contentType == null ? "application/octet-stream" : contentType);
    }

    private record StoredImage(byte[] data, String contentType) { }
}

package com.obysoft.faithOS.service;

import org.springframework.core.io.Resource;

public record FileDownload(Resource resource, String originalName, String contentType) {}

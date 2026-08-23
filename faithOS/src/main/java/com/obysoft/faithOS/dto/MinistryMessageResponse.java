package com.obysoft.faithOS.dto;
import java.time.LocalDateTime;
public record MinistryMessageResponse(Long id,String senderName,Long senderId,String message,String attachmentName,String attachmentType,Long attachmentSize,LocalDateTime createdAt){}

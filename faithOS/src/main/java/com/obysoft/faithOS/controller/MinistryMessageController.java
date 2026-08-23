package com.obysoft.faithOS.controller;
import java.util.List;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import org.springframework.web.multipart.MultipartFile;import com.obysoft.faithOS.dto.MinistryMessageResponse;import com.obysoft.faithOS.service.MinistryMessageService;
@RestController @RequestMapping("/api/ministries/{ministryId}/messages") public class MinistryMessageController{
 private final MinistryMessageService service;public MinistryMessageController(MinistryMessageService service){this.service=service;}
 @GetMapping public List<MinistryMessageResponse> all(@PathVariable Long ministryId){return service.all(ministryId);}
 @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<MinistryMessageResponse> send(@PathVariable Long ministryId,@RequestParam(required=false) String message,@RequestParam(required=false) MultipartFile file){return ResponseEntity.status(HttpStatus.CREATED).body(service.send(ministryId,message,file));}
 @GetMapping("/{messageId}/attachment") public ResponseEntity<byte[]> attachment(@PathVariable Long ministryId,@PathVariable Long messageId){var value=service.attachment(ministryId,messageId);return ResponseEntity.ok().contentType(MediaType.parseMediaType(value.contentType())).header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.attachment().filename(value.name()).build().toString()).body(value.data());}
}

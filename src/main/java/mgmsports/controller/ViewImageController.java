package mgmsports.controller;

import lombok.extern.slf4j.Slf4j;
import mgmsports.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController
public class ViewImageController {


    @Qualifier("activityImageService")
    @Autowired
    private FileStorageService activityImageStorageService;

    @Qualifier("profileImageService")
    @Autowired
    private FileStorageService profileImageStorageService;

    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<Object> image(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = activityImageStorageService.loadFileAsResource(fileName);
        if (!resource.exists()) {
            resource = profileImageStorageService.loadFileAsResource(fileName);
        }

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            log.info("Could not determine file type");
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    }

}

package mgmsports.service;

import mgmsports.common.exception.InvalidFileException;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, @Nullable String newName);
    Resource loadFileAsResource(String fileName);
    boolean isValidImageExtension(String fileName) throws InvalidFileException;
}

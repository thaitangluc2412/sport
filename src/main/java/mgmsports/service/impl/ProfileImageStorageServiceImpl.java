package mgmsports.service.impl;

import mgmsports.common.exception.FileStorageException;
import mgmsports.common.property.FileStorageProperties;
import mgmsports.service.BaseImageStorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Qualifier("profileImageService")
public class ProfileImageStorageServiceImpl extends BaseImageStorageService {
    public ProfileImageStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        super(fileStorageProperties);
        this.imageStorageLocation = Paths.get(fileStorageProperties.getProfileImageDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(imageStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
}

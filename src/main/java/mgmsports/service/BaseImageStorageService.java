package mgmsports.service;

import mgmsports.common.exception.FileNotFoundException;
import mgmsports.common.exception.FileStorageException;
import mgmsports.common.exception.InvalidFileException;
import mgmsports.common.property.FileStorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Base Service class for upload image
 *
 * @author Chuc Ba Hieu
 */
public abstract class BaseImageStorageService implements FileStorageService {

    private FileStorageProperties fileStorageProperties;
    protected Path imageStorageLocation;

    public BaseImageStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    /**
     * Store file
     *
     * @param file file
     * @param newName new name of file
     * @return fileName
     */
    @Override
    public String storeFile(MultipartFile file, @Nullable String newName) {
        if (file == null) {
            throw new FileStorageException("Sorry! File doesn't exist!");
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            String newFileName;
            if (newName != null)  {
                newFileName = changeFileName(fileName, newName);
            } else {
                newFileName = fileName;
            }
            Path targetLocation = this.imageStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    /**
     * Load file
     *
     * @param fileName filename
     * @return url file
     */
    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.imageStorageLocation.resolve(fileName).normalize();
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("File not found " + fileName, e);
        }
    }

    /**
     * Check extension
     *
     * @param fileName filename
     * @return true or false
     * @throws InvalidFileException InvalidFileException
     */
    @Override
    public boolean isValidImageExtension(String fileName) throws InvalidFileException {

        String fileExtension = getFileExtension(fileName);

        if (fileExtension == null) {
            throw new InvalidFileException("No File Extension");
        }

        fileExtension = fileExtension.toLowerCase();
        List<String> validExtensions = fileStorageProperties.getExtensions();
        for (String validExtension : validExtensions) {
            if (fileExtension.equals(validExtension)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if(dotIndex < 0) {
            return null;
        }
        return fileName.substring(dotIndex+1);
    }

    private String changeFileName(String fileName, String newName) {
        String extension = getFileExtension(fileName);
        if (extension == null) {
            return newName;
        }
        return newName + "." + extension;
    }
}

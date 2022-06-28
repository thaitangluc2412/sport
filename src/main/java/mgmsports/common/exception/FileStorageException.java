package mgmsports.common.exception;

/**
 * File storage exception
 *
 * @author Chuc Ba Hieu
 */
public class FileStorageException extends RuntimeException{
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

package io.github.tml.mosaic.core.execption;

public class InfoCollectException extends RuntimeException {

    public InfoCollectException(String message) {
        super(message);
    }

    public InfoCollectException(String message,Throwable cause) {
        super(message,cause);
    }

}

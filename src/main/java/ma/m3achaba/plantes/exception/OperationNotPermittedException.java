package ma.m3achaba.plantes.exception;


/**
 * @author Oussama
 **/
public class OperationNotPermittedException extends RuntimeException{
    public OperationNotPermittedException() {}
    public OperationNotPermittedException(String message) {
        super(message);
    }
}

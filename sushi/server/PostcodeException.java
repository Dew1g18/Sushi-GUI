package comp1206.sushi.server;
/**
 * @author Dave Waddington -30091055
 * yes this method is mine, just wanted to see how I'd make my own exception,
 * dont think I used it as I realised the logic I wanted to implement here 
 * was actually better suited to the catch part of the try{}catch(){} I had
 * already put in place to catch this exception.
 */
class PostcodeException extends Exception{
    String message;
    public PostcodeException(String message){
        super(message);
        this.message=message;
    }
}

package dataaccess;

public class DBException extends Exception{
        public DBException(String message) {
            super(message);
        }
        public DBException(String message, Throwable ex) {
            super(message, ex);
        }

}

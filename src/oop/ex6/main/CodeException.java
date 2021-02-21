package oop.ex6.main;

public class CodeException extends Exception {
    private String errorMessage;

    public CodeException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return this.errorMessage;
    }
}

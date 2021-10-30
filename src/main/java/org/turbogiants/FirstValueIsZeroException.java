package org.turbogiants;

public class FirstValueIsZeroException extends Exception{
    @Override
    public String getLocalizedMessage(){
        return this.getMessage();
    }
    @Override
    public String getMessage(){
        return "The first value can't be zero!";
    }
}

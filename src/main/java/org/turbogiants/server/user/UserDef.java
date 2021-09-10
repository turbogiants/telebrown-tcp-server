package org.turbogiants.server.user;

public class UserDef {

    public static final int SPAM_THRESHOLD = 5;


    // for testing purposes we will be using integer
    // todo: support 16 byte long id
    private int UID = 0;

    private int spamCount = 0;

    public boolean isSpamThresholdMet(){
        return spamCount >= SPAM_THRESHOLD;
    }

    public void addSpamCnt(){
        spamCount++;
    }

    public int getSpamCnt(){
        return spamCount;
    }

    public void reduceSpamCnt(){
        if(spamCount > 0){
            spamCount--;
        }
    }

    public void setUID(int uid){
        this.UID = uid;
    }

    public int getUID(){
        return UID;
    }

}

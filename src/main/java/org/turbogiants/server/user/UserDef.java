package org.turbogiants.server.user;

public class UserDef {

    public static final int SPAM_THRESHOLD = 10;

    private String UID = "0XDEADC0DE";

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

    public void setUID(String uid){
        this.UID = uid;
    }

    public String getUID(){
        return UID;
    }

}

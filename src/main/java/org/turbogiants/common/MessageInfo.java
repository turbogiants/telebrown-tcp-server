package org.turbogiants.common;

public class MessageInfo {
    private String strMessage;
    private int iDestID;
    private int iOwnerID;

    private long lUnixTime;

    public void setDestID(int iID) {
        this.iDestID = iID;
    }

    public int getDestID() {
        return iDestID;
    }

    public void setOwnerID(int iID) {
        this.iOwnerID = iID;
    }

    public int getOwnerID() {
        return iOwnerID;
    }

    public void setMessage(String strMessage) {
        this.strMessage = strMessage;
    }

    public String getMessage(){
        return strMessage;
    }

    public void setUnixTime(long lUnixTime) {
        this.lUnixTime = lUnixTime;
    }

    public long getUnixTime() {
        return lUnixTime;
    }
}

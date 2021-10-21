package org.turbogiants.common;

public class MessageInfo {
    private String strMessage;
    private String iDestID;
    private String iOwnerID;

    private long lUnixTime;

    public void setDestID(String iID) {
        this.iDestID = iID;
    }

    public String getDestID() {
        return iDestID;
    }

    public void setOwnerID(String iID) {
        this.iOwnerID = iID;
    }

    public String getOwnerID() {
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

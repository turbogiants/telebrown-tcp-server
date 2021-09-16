package org.turbogiants.common;

public class MessageInfo {
    private String strMessage;
    private int iDestID;
    private int iOwnerID;

    private long lUnixTime;

    public void setiDestID(int iID) {
        this.iDestID = iID;
    }

    public int getiDestID() {
        return iDestID;
    }

    public void setiOwnerID(int iID) {
        this.iOwnerID = iID;
    }

    public int getiOwnerID() {
        return iOwnerID;
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }

    public String getStrMessage(){
        return strMessage;
    }

    public void setlUnixTime(long lUnixTime) {
        this.lUnixTime = lUnixTime / 1000L;
    }

    public long getlUnixTime() {
        return lUnixTime * 1000L;
    }
}

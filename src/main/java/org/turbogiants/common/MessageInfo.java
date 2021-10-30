package org.turbogiants.common;

public class MessageInfo {
    private String strMessage;
    private String iDestID;
    private String iOwnerID;
    private String strSha256;

    // This variable will only be used by the server as server don't have the ECC module
    private byte[] byteArrMessage;

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


    // These two functions is only to use by the server
    public void setByteArrMessage(byte[] byteArrMessage) {
        this.byteArrMessage = byteArrMessage;
    }
    public byte[] getByteArrMessage(){
        return byteArrMessage;
    }

    // These two additional functions must be used by the client and server
    public void setStrSha256(String sha256){
        this.strSha256 = sha256;
    }
    public String getStrSha256(){
        return strSha256;
    }


    public void setUnixTime(long lUnixTime) {
        this.lUnixTime = lUnixTime;
    }

    public long getUnixTime() {
        return lUnixTime;
    }
}

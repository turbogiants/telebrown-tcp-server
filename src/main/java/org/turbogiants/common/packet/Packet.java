package org.turbogiants.common.packet;

public class Packet implements Cloneable {

    private byte[] data;

    public Packet(byte[] data) {
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public static byte[] getByteArrayByString(String s) {
        s = s.replace("|", " ");
        s = s.replace(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String readableByteArray(byte[] arr) {
        StringBuilder res = new StringBuilder();
        for (byte b : arr) {
            res.append(String.format("%02X ", b));
        }
        return res.toString();
    }

    public int getLength() {
        if (data != null) {
            return data.length;
        }
        return 0;
    }

    public int getHeader() {
        if (data.length < 2) {
            return 0xFFFF;
        }
        return (data[0] + (data[1] << 8));
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] nD) {
        data = nD;
    }

    @Override
    public String toString() {
        if (data == null) return "";
        return "[Pck] | " + readableByteArray(data);
    }

    @Override
    public Packet clone() throws CloneNotSupportedException {
        Packet packet = (Packet) super.clone();
        return new Packet(data);
    }

    public void release() {
    }

}


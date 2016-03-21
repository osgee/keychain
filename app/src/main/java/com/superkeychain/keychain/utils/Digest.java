package com.superkeychain.keychain.utils;

/**
 * Created by taofeng on 3/17/16.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Digest {
    private MessageDigest md = null;

    private Digest(MessageDigest md) {
        this.md = md;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }

    public String getDigest(String in) {
        md.update(in.getBytes());
        byte[] digest = md.digest();
//        StringBuffer sb=new StringBuffer();
//        for (byte b : md.digest()) {
//            sb.append(String.format("%02X", b));
//        }
        return bytesToHexString(digest);
    }

    public enum Builder {
        builder;
        private static String[] algorithm = {"MD5", "SHA-1", "SHA-256"};

        public static Digest byMD5() {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance(algorithm[0]);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return new Digest(md);
        }

        public static Digest bySHA1() {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance(algorithm[1]);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return new Digest(md);
        }

        public static Digest bySHA256() {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance(algorithm[2]);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return new Digest(md);
        }

    }
}
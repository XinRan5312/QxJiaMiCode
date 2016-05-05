package com.xinran.testjiami.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 *
 *
 * 几乎不可逆：
 * MD5是message-digest algorithm 5（信息-摘要算法）的缩写，被广泛用于加密和解密技术上，
 * 它可以说是文件的"数字指纹"。任何一个文件，无论是可执行程序、图像文件、临时文件或者其他任何类型的文件，
 * 也不管它体积多大，都有且只有一个独一无二的MD5信息值，并且如果这个文件被修改过，它的MD5值也将随之改变。
 * 因此，我们可以通过对比同一文件的MD5值，来校验这个文件是否被"篡改"过。
 * MD5到底有什么用？
 * 当我们下载了文件后，如果想知道下载的这个文件和网站的原始文件是否一模一样，就可以给自己下载的文件做个MD5校验。
 * 如果得到的MD5值和网站公布的相同，可确认所下载的文件是完整的。如有不同，说明你下载的文件是不完整的：
 * 要么就是在网络下载的过程中出现错误，要么就是此文件已被别人修改。为防止他人更改该文件时放入病毒，最好不要使用。
 * 一般正规的站点，都会提供文件md5校验码，这是为了双方都方便。
 *
 * <<++++++++++>></++++++++++>
 *
 *  * SHA-1和MD5的比较
 * 因为二者均由MD4导出，SHA-1和MD5彼此很相似。相应的，他们的强度和其他特性也是相似，但还有以下几点不同：
 * <p/>
 * 1）对强行攻击的安全性：最显著和最重要的区别是SHA-1摘要比MD5摘要长32 位。使用强行技术，
 * 产生任何一个报文使其摘要等于给定报摘要的难度对MD5是2^128数量级的操作，而对SHA-1则是2^160数量级的操作。
 * 这样，SHA-1对强行攻击有更大的强度。
 * <p/>
 * 2）对密码分析的安全性：由于MD5的设计，易受密码分析的攻击，SHA-1显得不易受这样的攻击。
 * <p/>
 * 3）速度：在相同的硬件上，SHA-1的运行速度比MD5慢。
 * Created by qixinh on 16/5/5.
 */
public class QxMD5JiaMi {
    /**
     * 把字节数组转成16进位制数
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder md5str = new StringBuilder();
        //把数组每一字节换成16进制连成md5字符串
        for (int i = 0; i < bytes.length; i++) {

            int digital = ((int) bytes[i]) & 0xff;
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }

    /**
     * 把字节数组转换成md5
     *
     * @param input
     * @return
     */
    public static String bytesToMD5(byte[] input) {
        String md5str = null;
        try {
            //创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Md5计算后获得字节数组
            byte[] buff = md.digest(input);
            //把数组每一字节换成16进制连成md5字符串
            md5str = bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    /**
     * 把字符串转换成md5
     *
     * @param str
     * @return
     */
    public static String strToMD5(String str) {
        byte[] input = str.getBytes();
        return bytesToMD5(input);
    }

    /**
     * 把文件转成md5字符串
     *
     * @param file
     * @return
     */
    public static String fileToMD5(File file) {
        if (file == null) {
            return null;
        }
        if (file.exists() == false) {
            return null;
        }
        if (file.isFile() == false) {
            return null;
        }
        FileInputStream fis = null;
        try {
            //创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buff = new byte[1024];
            int len = 0;
            while (true) {
                len = fis.read(buff, 0, buff.length);
                if (len == -1) {
                    break;
                }
                //每次循环读取一定的字节都更新
                md.update(buff, 0, len);
            }
            //关闭流
            fis.close();
            //返回md5字符串
            return bytesToHex(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 把文件转换成MD5后再加密和解密
     * 加密和解密算法 执行一次加密，两次解密
     */
    public static String encodeOrDecodeString(String inStr){

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;

    }
}

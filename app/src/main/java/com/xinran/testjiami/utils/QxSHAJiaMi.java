package com.xinran.testjiami.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 *
 *
 * SHA-1是一种数据加密算法，该算法的思想是接收一段明文，然后以一种不可逆的方式将它转换成一段（通常更小）密文，
 * 也可以简单的理解为取一串输入码（称为预映射或信息），并把它们转化为长度较短、位数固定的输出序列即散列值
 * （也称为信息摘要或信息认证代码）的过程。
 * <p/>
 * 单向散列函数的安全性在于其产生散列值的操作过程具有较强的单向性。
 * 如果在输入序列中嵌入密码，那么任何人在不知道密码的情况下都不能产生正确的散列值，从而保证了其安全性。
 * SHA将输入流按照每块512位（64个字节）进行分块，并产生20个字节的被称为信息认证代码或信息摘要的输出。
 * <p/>
 * 该算法输入报文的长度不限，产生的输出是一个160位的报文摘要。输入是按512 位的分组进行处理的。
 * SHA-1是不可逆的、防冲突，并具有良好的雪崩效应。
 * <p/>
 * 通过散列算法可实现数字签名实现，数字签名的原理是将要传送的明文通过一种函数运算（Hash）
 * 转换成报文摘要（不同的明文对应不同的报文摘要），报文摘要加密后与明文一起传送给接受方，
 * 接受方将接受的明文产生新的报文摘要与发送方的发来报文摘要解密比较，比较结果一致表示明文未被改动，如果不一致表示明文已被篡改。
 * <p/>
 * MAC （信息认证代码）就是一个散列结果，其中部分输入信息是密码，只有知道这个密码的参与者才能再次计算和验证MAC码的合法性。




 * <<*********+++++++++++++++++++++++++++++++++++++></*********+++++++++++++++++++++++++++++++++++++>
 * SHA-1和MD5的比较
 * 因为二者均由MD4导出，SHA-1和MD5彼此很相似。相应的，他们的强度和其他特性也是相似，但还有以下几点不同：
 * <p/>
 * 1）对强行攻击的安全性：最显著和最重要的区别是SHA-1摘要比MD5摘要长32 位。使用强行技术，
 * 产生任何一个报文使其摘要等于给定报摘要的难度对MD5是2^128数量级的操作，而对SHA-1则是2^160数量级的操作。
 * 这样，SHA-1对强行攻击有更大的强度。
 * <p/>
 * 2）对密码分析的安全性：由于MD5的设计，易受密码分析的攻击，SHA-1显得不易受这样的攻击。
 * <p/>
 * 3）速度：在相同的硬件上，SHA-1的运行速度比MD5慢。
 * <p/>
 * Created by qixinh on 16/5/5.
 */
public class QxSHAJiaMi {
    /***
     * SHA加密 生成40位SHA码
     *
     * @param
     * @return 返回40位SHA码
     */
    public static String shaEncode(String inStr) throws Exception {
        MessageDigest sha = null;
        try {
            //            MessageDigest md = MessageDigest.getInstance("MD5");
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);

        return bytesToHex(md5Bytes);
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
//            MessageDigest md = MessageDigest.getInstance("MD5");
            MessageDigest md = MessageDigest.getInstance("SHA");
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
}

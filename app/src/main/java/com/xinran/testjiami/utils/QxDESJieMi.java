package com.xinran.testjiami.utils;

import java.security.InvalidKeyException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Random即：java.util.Random，
 * ThreadLocalRandom 即：java.util.concurrent.ThreadLocalRandom
 * SecureRandom即：java.security.SecureRandom
 * <p/>
 * <p/>
 * Q：Random是不是线程安全的？
 * A：Random是线程安全的，但是多线程下可能性能比较低。
 * 参考：
 * http://docs.oracle.com/javase/7/docs/api/java/util/Random.html
 * http://stackoverflow.com/questions/5819638/is-random-class-thread-safe
 * <p/>
 * Q：ThreadLocalRandom为什么这么快？
 * A：其实这个看下源码就知道了。。因为Random用了很多CAS的类，ThreadLocalRandom根本没有用到。
 * <p/>
 * Q：为什么在高强度要求的情况下，不要用Random？
 * A：特别是在生成验证码的情况下，不要使用Random，因为它是线性可预测的。记得有个新闻说的是一个赌博网站，
 * 为了说明其公平，公开的它的源代码，结果因为随机数可预测漏洞被攻击了。所以在安全性要求比较高的场合，应当使用SecureRandom。
 * <p/>
 * update 2014-4-22:  http://news.cnblogs.com/n/206074/
 * <p/>
 * 参考：http://www.inbreak.net/archives/349
 * <p/>
 * Q：从理论上来说计算机产生的随机数都是伪随机数，那么如何产生高强度的随机数？
 * A：产生高强度的随机数，有两个重要的因素：种子和算法。当然算法是可以有很多的，但是如何选择种子是非常关键的因素。
 * 如Random，它的种子是System.currentTimeMillis()，所以它的随机数都是可预测的。那么如何得到一个近似随机的种子？
 * 这里有一个很别致的思路：收集计算机的各种信息，如键盘输入时间，CPU时钟，内存使用状态，硬盘空闲空间，IO延时，进程数量，
 * 线程数量等信息，来得到一个近似随机的种子。这样的话，除了理论上有破解的可能，实际上基本没有被破解的可能。而事实上，
 * 现在的高强度的随机数生成器都是这样实现的。
 * 比如Windows下的随机数生成器：
 * http://blogs.msdn.com/b/michael_howard/archive/2005/01/14/353379.aspx
 * http://msdn.microsoft.com/en-us/library/aa379942%28VS.85%29.aspx
 * Linux下的 /dev/random：
 * http://zh.wikipedia.org/wiki//dev/random
 * 据SecureRandom的Java doc，说到在类unix系统下，有可能是利用 /dev/random，来实现的。
 * <p/>
 * <p/>
 * 其它的一些有意思的东东：
 * 最快的安全性要求不高的生成UUID的方法（注意，强度不高，有可能会重复）：
 * [java] view plain copy 在CODE上查看代码片派生到我的代码片
 * <p/>
 * new UUID(ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong());
 * 在一个网站上看到的，忘记出处了。
 * <p/>
 * Created by qixinh on 16/5/5.
 */
public class QxDESJieMi {
    /**
     * 加密
     *
     * @param datasource 需要加密的数据
     * @param password   加密key字节数一定要大于8
     * @return 返回密文
     */
    public static byte[] desEncode(byte[] datasource, String password) {

        try {
            //创建一个可信任的随机数据源
            SecureRandom random = new SecureRandom();
            /** @throws InvalidKeyException
            password.getBytes()==if the length of the specified key data is less than 8.
             */
            DESKeySpec desKey = new DESKeySpec(password.getBytes());

            //创建一个密匙工厂，然后用它把DESKeySpec转换成SecretKey对象

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            SecretKey securekey = keyFactory.generateSecret(desKey);

            //Cipher对象实际完成加密操作

            Cipher cipher = Cipher.getInstance("DES");

            //用密匙初始化Cipher对象

            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);

            //现在，获取数据并加密

            //正式执行加密操作

            return cipher.doFinal(datasource);

        } catch (Throwable e) {

            e.printStackTrace();

        }
        return null;

    }

    /**
     * 解密
     *
     * @param datasource 需要解密的数据
     * @param password   解密用的key要跟加密时的一样
     * @return 返回解密后的明文
     */
    public static byte[] desDecode(byte[] datasource, String password) {
        try {
            SecureRandom radom = new SecureRandom();//产生一个安全的随机数据源
            DESKeySpec desKeySpec = new DESKeySpec(password.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, radom);
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}

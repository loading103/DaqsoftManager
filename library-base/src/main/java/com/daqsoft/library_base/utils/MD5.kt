package com.daqsoft.library_base.utils

import android.util.Log
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 13/11/2020 上午 9:38
 * @author zp
 * @describe
 */
class MD5 {

    companion object{

        fun hexdigest(source: String): String {
            //定义一个字节数组
            var secretBytes: ByteArray? = null
            try {
                // 生成一个MD5加密计算摘要
                val md = MessageDigest.getInstance("MD5")
                //对字符串进行加密
                md.update(source.toByteArray())
                //获得加密后的数据
                secretBytes = md.digest()
            } catch (e: NoSuchAlgorithmException) {

            }
            //将加密后的数据转换为16进制数字
            val md5code = StringBuilder(BigInteger(1, secretBytes).toString(16)) // 16进制数字
            // 如果生成数字未满32位，需要前面补0
            for (i in 0 until 32 - md5code.length) {
                md5code.insert(0, "0")
            }
            return md5code.toString()
        }

    }

}
package com.zhanghuaming.videoplayer.utlis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	public final static String MD5(String s) {
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();

			return byteArrayToHex(md);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String byteArrayToHex(byte[] byteArray) {

		// 首先初始化一个字符数组，用来存放每个16进制字符

		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A','B','C','D','E','F' };

		// new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））

		char[] resultCharArray = new char[byteArray.length * 2];

		// 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去

		int index = 0;

		for (byte b : byteArray) {

			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];

			resultCharArray[index++] = hexDigits[b & 0xf];

		}

		// 字符数组组合成字符串返回

		return new String(resultCharArray);
	}


	public static String fileMD5(String inputFile) throws IOException {
		// 缓冲区大小（这个可以抽出一个参数）
		int bufferSize = 64 * 1024;
		FileInputStream fileInputStream = null;
		DigestInputStream digestInputStream = null;
		try {
			// 拿到一个MD5转换器（同样，这里可以换成SHA1）
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			// 使用DigestInputStream
			fileInputStream = new FileInputStream(inputFile);
			digestInputStream = new DigestInputStream(fileInputStream,messageDigest);
			// read的过程中进行MD5处理，直到读完文件
			byte[] buffer =new byte[bufferSize];
			while (digestInputStream.read(buffer) > 0);
			// 获取最终的MessageDigest
			messageDigest= digestInputStream.getMessageDigest();
			// 拿到结果，也是字节数组，包含16个元素

			byte[] resultByteArray = messageDigest.digest();
			// 同样，把字节数组转换成字符串
			return byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} finally {
			try {
				digestInputStream.close();
			} catch (Exception e) {
			}

			try {
				fileInputStream.close();
			} catch (Exception e) {

			}

		}

	}


	public static String fileMD5(InputStream is) throws IOException {

		// 缓冲区大小（这个可以抽出一个参数）
		int bufferSize = 64 * 1024;
		DigestInputStream digestInputStream = null;
		try {
			// 拿到一个MD5转换器（同样，这里可以换成SHA1）

			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			digestInputStream = new DigestInputStream(is,messageDigest);
			// read的过程中进行MD5处理，直到读完文件
			byte[] buffer =new byte[bufferSize];
			while (digestInputStream.read(buffer) > 0);

			// 获取最终的MessageDigest
			messageDigest= digestInputStream.getMessageDigest();
			// 拿到结果，也是字节数组，包含16个元素
			byte[] resultByteArray = messageDigest.digest();
			// 同样，把字节数组转换成字符串
			return byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} finally {
			try {
				digestInputStream.close();
			} catch (Exception e) {
			}

			try {
				is.close();
			} catch (Exception e) {

			}

		}

	}



	public static void main(String[] args) {
		System.out.println(MD5Util.MD5("20121221"));
		System.out.println(MD5Util.MD5("加密"));
	}
}
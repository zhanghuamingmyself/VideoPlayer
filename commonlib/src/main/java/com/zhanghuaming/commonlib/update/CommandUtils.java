package com.zhanghuaming.commonlib.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class CommandUtils {

	public static String execCommand(String command) throws IOException {
		// start the ls command running
		// String[] args = new String[]{"sh", "-c", command};
		System.out.println("execCommand:"+command);
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(command); // 这句话就是shell与高级语言间的调用
		// 如果有参数的话可以用另外一个被重载的exec方法
		// 实际上这样执行时启动了一个子进程,它没有父进程的控制台
		// 也就看不到输出,所以我们需要用输出流来得到shell执行后的输出
		InputStream inputstream = proc.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
		// read the ls output
		String line = "";
		StringBuilder sb = new StringBuilder(line);
		while ((line = bufferedreader.readLine()) != null) {
			// System.out.println(line);
			sb.append(line);
			sb.append('\n');
		}
		// tv.setText(sb.toString());
		// 使用exec执行不会等执行成功以后才返回,它会立即返回
		// 所以在某些情况下是很要命的(比如复制文件的时候)
		// 使用wairFor()可以等待命令执行完成以后才返回
		try {
			if (proc.waitFor() != 0) {
				System.err.println("exit value = " + proc.exitValue());
			}
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		return sb.toString();
	}

	public static String execCommand_RetLine(String command,int n) throws IOException {
		// start the ls command running
		// String[] args = new String[]{"sh", "-c", command};
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(command); // 这句话就是shell与高级语言间的调用
		// 如果有参数的话可以用另外一个被重载的exec方法
		// 实际上这样执行时启动了一个子进程,它没有父进程的控制台
		// 也就看不到输出,所以我们需要用输出流来得到shell执行后的输出
		InputStream inputstream = proc.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
		// read the ls output
		String line = "";
		StringBuilder sb = new StringBuilder(line);
		int i = 0;
		while ((line = bufferedreader.readLine()) != null) {
			// System.out.println(line);
			if (line.trim().length() == 0){
				continue;
			}
			sb.append(line);
			sb.append('\n');
			i++;
			if(i > n){
				break;
			}

		}
		// tv.setText(sb.toString());
		// 使用exec执行不会等执行成功以后才返回,它会立即返回
		// 所以在某些情况下是很要命的(比如复制文件的时候)
		// 使用wairFor()可以等待命令执行完成以后才返回
		try {
			if (proc.waitFor() != 0) {
				System.err.println("exit value = " + proc.exitValue());
			}
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		return sb.toString();
	}

	public static String hpcmdExec(String paramString) throws Exception {

		System.out.println("hpcmdExec:"+paramString);
		Process localProcess = getCmdProcess();
		BufferedWriter localBufferedWriter = new BufferedWriter(
				new OutputStreamWriter(new BufferedOutputStream(
						localProcess.getOutputStream())));
		BufferedInputStream localBufferedInputStream = new BufferedInputStream(
				localProcess.getInputStream());
		BufferedReader localBufferedReader = new BufferedReader(
				new InputStreamReader(localBufferedInputStream));
		localBufferedWriter.write(paramString);
		localBufferedWriter.flush();
		localBufferedWriter.close();
		String ret = getExecRusult(localProcess, localBufferedReader);
		localBufferedReader.close();
		localBufferedInputStream.close();
		return ret;
	}

	static Process getCmdProcess() throws IOException {
		Runtime localRuntime = Runtime.getRuntime();
		try {
			Process localProcess = localRuntime.exec("su");
			return localProcess;
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		return localRuntime.exec("su");
	}

	static boolean HasResult = true;

	private static String getExecRusult(Process paramProcess,
										BufferedReader paramBufferedReader) throws IOException,
			InterruptedException {
		String line = "";
		StringBuilder sb = new StringBuilder(line);
		while ((line = paramBufferedReader.readLine()) != null) {
			sb.append(line);
			sb.append('\n');
		}
		if (paramProcess.waitFor() != 0) {
			sb.append("exitValue="+paramProcess.exitValue());
		}
		return sb.toString();
	}

	private static int getExecRusult2(Process paramProcess,
									  BufferedReader paramBufferedReader) throws IOException,
			InterruptedException {
		while (paramBufferedReader.readLine() != null)
			;
		if (paramProcess.waitFor() != 0) {
			if (paramProcess.exitValue() == 1)
				return 1;
			return 2;
		}
		return 2;
	}

	public static  boolean checkNetwork(Context mContext) {
		if(mContext == null )return false;
		ConnectivityManager conn = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = conn.getActiveNetworkInfo();
		if (net != null && net.isConnected()) {
			return true;
		}
		return false;
	}



}

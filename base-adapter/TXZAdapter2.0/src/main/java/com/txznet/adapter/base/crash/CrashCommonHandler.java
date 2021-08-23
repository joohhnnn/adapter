package com.txznet.adapter.base.crash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashCommonHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CrashCommonHandler";
	private static final String FILE_NAME = "crash";
	private static final String FILE_NAME_SUFFIX = ".trace";

	// 系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
	private static UncaughtExceptionHandler mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();

	@SuppressLint("StaticFieldLeak")
	private static Context sContext;
	private static String sReportDirectory;
	private static CrashLisener sLisener;

	public static class CrashLisener {
		public CrashLisener(String reportDirectory) {
			sReportDirectory = reportDirectory;
			if (sReportDirectory != null) {
				if (!sReportDirectory.endsWith("/"))
					sReportDirectory += "/";
			}
		}

		@SuppressWarnings({"WeakerAccess", "unused"})
		public void handleException(Thread thread, Throwable ex, PrintWriter pw) {
			if (mOnCrashListener != null) {
				mOnCrashListener.onCaughtException();
			}
		}
	}

	@SuppressWarnings("UnnecessaryInterfaceModifier")
	public static interface OnCrashListener {
		public void onCaughtException();
	}

	private static OnCrashListener mOnCrashListener;

	@SuppressWarnings("unused")
	public static void setOnCrashListener(OnCrashListener onCrashListener) {
		mOnCrashListener = onCrashListener;
	}

	// 构造方法私有，防止外部构造多个实例，即采用单例模式
	private CrashCommonHandler() {
	}

	public static void init(Context context, CrashLisener lisener) {
		sContext = context;
		if (sReportDirectory == null) {
			sReportDirectory = null;
		}

		sLisener = lisener;
		// 将当前实例设为系统默认的异常处理器
		Thread.setDefaultUncaughtExceptionHandler(instance);
	}

	@SuppressLint("StaticFieldLeak")
	private static CrashCommonHandler instance = new CrashCommonHandler();

	public static CrashCommonHandler getInstance() {
		return instance;
	}

	/**
	 * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
	 * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			// 导出异常信息到SD卡中
			dumpExceptionToSDCard(thread, ex);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 打印出当前调用栈信息
		ex.printStackTrace();
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		String s = writer.toString();

		Log.e(TAG, s);

		// 如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
		if (mDefaultCrashHandler != null) {
			// mDefaultCrashHandler.uncaughtException(thread, ex);
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void dumpExceptionToSDCard(Thread thread, Throwable ex) throws IOException {
		if (sReportDirectory == null)
			return;
		File dir = new File(sReportDirectory);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		long current = System.currentTimeMillis();
		String time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINESE).format(new Date(current));
		String timestr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(new Date(current));
		// 以当前时间创建log文件
		File file = new File(sReportDirectory + FILE_NAME + "_" + sContext.getApplicationInfo().packageName + "_" + time
				+ FILE_NAME_SUFFIX);

		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			// 导出发生异常的时间
			pw.println(timestr);

			// 导出其他信息
			if (sLisener != null) {
				sLisener.handleException(thread, ex, pw);
			}

			pw.println();
			// 导出异常的调用栈信息
			ex.printStackTrace(pw);

			pw.close();
		} catch (Exception e) {
			Log.e(TAG, "dump crash info failed, cause: " + e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	public void setAgain() {
		Thread.setDefaultUncaughtExceptionHandler(instance);
	}
}

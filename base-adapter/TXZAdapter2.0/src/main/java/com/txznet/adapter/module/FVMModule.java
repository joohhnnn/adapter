package com.txznet.adapter.module;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.txznet.adapter.AdpApplication;
import com.txznet.adapter.base.BaseModule;
import com.txznet.adapter.base.util.FileUtil;
import com.txznet.adapter.fvm.JNI;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author MarvinYang.2018.09.12
 * FVM类
 * 把sfs和bin文件拷到/assets/fvm/下，切记，要放在fvm目录下
 * 调用方法：先init，然后调用其它的方法
 */
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess", "UnusedReturnValue", "Convert2Lambda"})
public class FVMModule extends BaseModule {

    //	public static final String FVM_VERSION = "TEST_VERSION";
    public static final String FVM_VERSION = "6.26.3.99";

    /**
     * 禁用自动升级
     */
    private static final boolean DEBUG_DISABLE_AUTOMATIC_FVM_UPDATE = false;
    /**
     * 使用UI弹窗升级而不是静默升级
     */
    private static final boolean USE_USER_INTERFACE_FOR_UPDATE = false;
    /**
     * 即使版本是ERROR（获取失败）也执行升级
     */
    private static final boolean STRICT_MODE = true;
    /**
     * 禁用该模块，即是，不向I2C发送任何数据
     */
    public static final boolean DEBUG_TOTALLY_DISABLE_FVM_MODULE = false;
    /**
     * 读模块版本之前先RESET
     */
    public static final boolean RESET_BEFORE_INIT = false;
    /**
     * 读版本号失败则RESET，当设备经常出现无法读取版本号，但RESET可以解决时，请考虑打开此开关
     */
    public static final boolean RESET_IF_ERROR = false;
    /**
     * 最大RESET次数
     */
    public static final int MAX_RESET_COUNT = 2;

    private static FVMModule instance;
    private JNI jni;
    private final Object i2cLock = new Object();

    static {
        System.loadLibrary("txzmarvin");
    }

    /*
     * i2c默认设置号
     */

    //N806: /dev/i2c-2

    private String i2cDevice = "/dev/i2c-2";
    //	private String i2cDevice = "/dev/i2c-1";
    /*
     * sfs文件名
     */
    private String sfsFileName = null;
    /*
     * bin文件名
     */
    private String binFileName = null;
    /*
     * 默认驱动节点
     */
//    private String resetPath = "/sys/devices/platform/unisound/uni_rst";
    private String resetPath = "/sys/devices/platform/txz/txz_rst";
    /**
     * 默认把sfs和bin拷到这里去，方便
     */
    private static final String DEFAULT_FILE_DIR_NAME = Environment.getExternalStorageDirectory().getPath() + "/txz";

    /**
     * 正常工作模式
     */
    public static final String MODE_NORMAL = "ZVS2";
    /**
     * 只输出MIC信号，不做dsp处理
     */
    public static final String MODE_MIC = "ZMP6";
    /**
     * 只输出AEC参考信号
     */
    public static final String MODE_AEC = "ZSH2";

    /**
     * 默认的assets目录
     */
    private static final String ASSETS_DIR = "fvm";

	private FVMModule() {
	}

	public static FVMModule getInstance() {
		if (instance == null) {
			synchronized (FVMModule.class) {
				if (instance == null)
					instance = new FVMModule();
			}
		}
		return instance;
	}

	private int resetCount = 0;

	Runnable checkVersion = new Runnable() {
		public void run() {
			String fvmVersion = getFVMVersion();
			Log.d(TAG, "init: FVM Ver at " + fvmVersion);
			if (!FVM_VERSION.equals(fvmVersion) && !DEBUG_DISABLE_AUTOMATIC_FVM_UPDATE) {
				// 在到达最大RESET次数之前失败则发送RESET
				if ("ERROR".equals(fvmVersion) && RESET_IF_ERROR && (resetCount <= MAX_RESET_COUNT)) {
					Log.e(TAG, "FVM Version ERROR, sending reset...(" + (resetCount + 1) + "/"
							+ MAX_RESET_COUNT + ")");
					sendReset(() -> {
						resetCount++;
						checkVersion.run();
					});
					return;
				}
				// 版本号不正确或者再次读取错误，弹框提示
				if (!"ERROR".equals(fvmVersion) || STRICT_MODE) {
					Log.e(TAG, "init: 降噪模块版本不一致，正在刷入");
					if (USE_USER_INTERFACE_FOR_UPDATE) {
						AdpApplication.getInstance().startFVMUpdate(fvmVersion, FVM_VERSION);
					} else {
						updateFVM(new FVMUpdateListener() {
							long start = System.nanoTime();

							@Override
							public void onSuccess() {
								long finish = System.nanoTime();
								long cost = (finish - start) / 1_000_000;
								Log.d(TAG, "onSuccess: 固件已刷入: " + cost + "ms");
//								setFVMMode(MODE_NORMAL);
							}

							@Override
							public void onFailed() {
								Log.e(TAG, "onSuccess: 固件刷入失败");
							}
						});
					}
				} else {
					Log.e(TAG, "init: 降噪模块版本获取失败！");
				}
			}
		}
	};

	@Override
	public void init() {
		if (DEBUG_TOTALLY_DISABLE_FVM_MODULE) {
			return;
		}
		init(null, null);
		if (RESET_BEFORE_INIT) {
			sendReset(checkVersion);
		} else {
			checkVersion.run();
		}
	}

	/**
	 * 初始化
	 */
	public boolean init(String i2cDevice, String resetPath) {
		if (DEBUG_TOTALLY_DISABLE_FVM_MODULE) {
			return false;
		}
		if (i2cDevice != null) this.i2cDevice = i2cDevice;
		if (resetPath != null) this.resetPath = resetPath;
		jni = new JNI();
		// 初始化JNI
		int result = jni.init(this.i2cDevice, 'A', 81, 64, 16);
		if (result != 0) {
			Log.e(TAG, "init: FVM Init Failed!!");
		} else {
			Log.d(TAG, "init: FVM Init Completed");
		}
		return 0 == result;
	}

	/**
	 * FVM:获取当前版本号
	 *
	 * @return 返回版本号
	 */
	public String getFVMVersion() {
		if (DEBUG_TOTALLY_DISABLE_FVM_MODULE) {
			return "FVM DISABLED";
		}
		String version = null;
		synchronized (i2cLock) {
			try {
				version = jni.readVersion();
			} catch (Exception e) {
				Log.d(TAG, e.toString());
			}
		}
		// 把无用字符全部扔掉
		if (version != null) version = version.trim();
//		if (version != null) version = version.replaceAll("[^0-9]", "");
		return version;
	}

	/**
	 * 设置模块工作模式
	 *
	 * @param mode 模式
	 * @return 结果
	 */
	public boolean setFVMMode(String mode) {
		if (DEBUG_TOTALLY_DISABLE_FVM_MODULE) {
			return false;
		}
		synchronized (i2cLock) {
			try {
				return jni.setMode(3, mode) == 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 偏向角设置
	 *
	 * @param focusAngle 偏向角 0-90度
	 * @return 是否成功
	 */
	public boolean setFVMFocusAngle(int focusAngle) {
		if (DEBUG_TOTALLY_DISABLE_FVM_MODULE) {
			return false;
		}
		synchronized (i2cLock) {
			try {
				return jni.setDirectAngle(focusAngle) == 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	/**
	 * 拾音角设置
	 *
	 * @param widthAngle 偏向角 0-90度
	 * @return 是否成功
	 */
	public boolean setFVMFocusWidthAngle(int widthAngle) {
		synchronized (i2cLock) {
			if (DEBUG_TOTALLY_DISABLE_FVM_MODULE) {
				return false;
			}
			try {
				return jni.setDirectWidth(widthAngle) == 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}


	/**
	 * 设置拾音区偏向角和宽度
	 *
	 * @param angle
	 * @param width
	 * @return
	 */
	public boolean setDirectAngleAndWidth(int angle, int width) {
		if (DEBUG_TOTALLY_DISABLE_FVM_MODULE) {
			return false;
		}
		return setFVMFocusAngle(angle) & setFVMFocusWidthAngle(width);
	}

	private boolean isInUpdate = false;

	/**
	 * 更新模块固件
	 *
	 * @param fvmUpdateListener 回调
	 */
	public void updateFVM(FVMUpdateListener fvmUpdateListener) {
		if (DEBUG_TOTALLY_DISABLE_FVM_MODULE) {
			if (fvmUpdateListener != null)
				fvmUpdateListener.onFailed();
			return;
		}
		try {
			FileUtil.deleteAllFiles(DEFAULT_FILE_DIR_NAME + "/" + ASSETS_DIR);
			// 先找assets目录
			String[] files;
			if (TextUtils.isEmpty(ASSETS_DIR)) {
				files = AdpApplication.getInstance().getAssets().getLocales();
			} else {
				files = AdpApplication.getInstance().getAssets().list(ASSETS_DIR);
			}
			if (files == null) files = new String[0];
			// 开始找sfs和bin
			for (String file : files) {
				if (file.endsWith(".sfs")) {
					sfsFileName = file;
					FileUtil.saveAssetsFileToPath(AdpApplication.getInstance(), ASSETS_DIR + "/" + sfsFileName, DEFAULT_FILE_DIR_NAME);
					continue;
				}
				if (file.endsWith(".bin")) {
					binFileName = file;
					FileUtil.saveAssetsFileToPath(AdpApplication.getInstance(), ASSETS_DIR + "/" + binFileName, DEFAULT_FILE_DIR_NAME);
				}
			}
			// 已在刷机就不要无聊了
			if (isInUpdate) {
				Log.d(TAG, "updateFVM : in update progress");
				return;
			}
			// 子线程刷机，不然是个阻塞的
			new Thread(new Runnable() {
				@Override
				public void run() {
					synchronized (i2cLock) {
						isInUpdate = true;
						boolean result = jni.update(DEFAULT_FILE_DIR_NAME + "/" + ASSETS_DIR + "/" + sfsFileName, DEFAULT_FILE_DIR_NAME + "/" + ASSETS_DIR + "/" + binFileName, resetPath, 0) == 0;
						isInUpdate = false;
						if (result) {
							fvmUpdateListener.onSuccess();
						} else {
							fvmUpdateListener.onFailed();
						}
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			isInUpdate = false;
		}
	}

	public void sendReset(Runnable afterReset) {
		new Thread(() -> {
			Log.d(TAG, "Sending reset...");
			synchronized (i2cLock) {
				try {
					send0Rst();
					Thread.sleep(200);
					send1Rst();
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (afterReset != null)
						afterReset.run();
				}
			}
		}).start();
	}

	public void send0Rst() {
		File rst = new File(resetPath);
		try (FileOutputStream out = new FileOutputStream(rst);) {
			out.write("0".getBytes());
			out.flush();
			Log.d(TAG, "send0Rst: 0 is sent");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send1Rst() {
		File rst = new File(resetPath);
		try (FileOutputStream out = new FileOutputStream(rst);) {
			out.write("1".getBytes());
			out.flush();
			Log.d(TAG, "send1Rst: 1 is sent");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateFVM(String sfsPath, String binPath, FVMUpdateListener fvmUpdateListener) {

		if (DEBUG_TOTALLY_DISABLE_FVM_MODULE) {
			if (fvmUpdateListener != null)
				fvmUpdateListener.onFailed();
			return;
		}
		try {
			// 已在刷机就不要无聊了
			if (isInUpdate) {
				Log.d(TAG, "updateFVM : in update progress");
				return;
			}
			// 子线程刷机，不然是个阻塞的
			new Thread(new Runnable() {
				@Override
				public void run() {
					synchronized (i2cLock) {
						isInUpdate = true;
						boolean result = jni.update(sfsPath, binPath, resetPath, 0) == 0;
						isInUpdate = false;
						if (result) {
							fvmUpdateListener.onSuccess();
						} else {
							fvmUpdateListener.onFailed();
						}
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			isInUpdate = false;
		}
	}

	/**
	 * 获取实时的声音角度  测试用
	 *
	 * @param angle   [0]代表实时的角度
	 * @param doubles [0]目前未知
	 * @return 是否成功
	 */
	public boolean getAngleAndProbTest(double[] angle, double[] doubles) {
		if (DEBUG_TOTALLY_DISABLE_FVM_MODULE) {
			return false;
		}
		synchronized (i2cLock) {
			try {
				// 这里居然1是代表成功
				return jni.getAngleAndProb(angle, doubles) == 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	public interface FVMUpdateListener {

		void onSuccess();

		void onFailed();

	}

}

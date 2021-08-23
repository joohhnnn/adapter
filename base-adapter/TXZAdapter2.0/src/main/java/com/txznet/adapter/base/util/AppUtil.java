package com.txznet.adapter.base.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.txznet.adapter.AdpStatusManager;

import java.util.List;

/**
 * 应用工具
 * Created by MarvinYang on 2018/3/2.
 */

@SuppressWarnings("WeakerAccess")
public class AppUtil {
    /**
     * 获取当前本地apk的版本
     *
     * @param mContext .
     * @return .
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return 0
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 获取已安装应用列表
     * 默认剔除掉没有activity的app了
     *
     * @param context context
     * @return 应用列表
     */
    public static List<PackageInfo> getAppList(Context context) {
        return getAppList(context, false);
    }

    /**
     * 获取已安装应用列表
     *
     * @param context context
     * @return 应用列表
     */
    public static List<PackageInfo> getAppList(Context context, boolean isContainNoLauncherApp) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        if (packages != null) {
            for (PackageInfo info : packages) {
                // 语音
                if ("com.txznet.txz".equals(info.packageName)) {
                    AdpStatusManager.getInstance().setVersionTXZ(info.versionName + "-" + info.versionCode);
                }
                // 同听
                else if ("com.txznet.music".equals(info.packageName)) {
                    AdpStatusManager.getInstance().setVersionMusic(info.versionName + "-" + info.versionCode);
                }
                // 微信
                else if ("com.txznet.webchat".equals(info.packageName)) {
                    AdpStatusManager.getInstance().setVersionWebChat(info.versionName + "-" + info.versionCode);
                }
                // Adapter
                else if (context.getPackageName().equals(info.packageName)) {
                    AdpStatusManager.getInstance().setVersionAdapter(info.versionName + "-" + info.versionCode);
                }
                // 不需要包含没有界面的App
                if (!isContainNoLauncherApp) {
                    //  如果没有launcherActivity就移除掉
                    if (!hasLauncherActivity(context, info.packageName)) {
                        packages.remove(info);
                    }
                }
            }
        }
        return packages;
    }


    /**
     * 判断应用是否有图标
     *
     * @param packageName 包名
     * @return 是否有图标
     */
    private static boolean hasLauncherActivity(Context context, String packageName) {
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
        return resolveInfoList != null && !resolveInfoList.isEmpty();
    }


    //隐藏输入法
    public static void hideSoftKeyboard(FragmentActivity mAct) {
        View view = mAct.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mAct.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

package com.txznet.adapter.base.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

    private static final String SP_FILE_NAME = "TXZAdapter";
    public static final String KEY_UUID = "txz_uuid";
    public static final String KEY_FLOAT_TOOL_TYPE = "float_type";

    public static final String KEY_I2C_PATH = "key_i2c_path";
    public static final String KEY_RESET_PATH = "key_reset_path";

    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.clear();
    }

    public static String getString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, value);
    }

}

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
LOCAL_SHARED_LIBRARIES := liblog
LOCAL_SRC_FILES := libfmrec.c fm_wav.c
LOCAL_MODULE := libfmrec_1388
include $(BUILD_SHARED_LIBRARY)

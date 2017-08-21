LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := SerialPortTest
LOCAL_SRC_FILES := SerialPortTest.cpp
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)

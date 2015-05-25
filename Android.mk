LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := activate support  dspread  gson  javamail  mpandroidchar  pushservice  universal   libarityXml  SmartTerminalLib      

LOCAL_STATIC_LIBRARIES := liba01jni libbdpush_V2_2 lib_serial_port
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_JNI_SHARED_LIBRARIES :=libavcodec \
							 libavcodecnoneon \
							 libavcore \
							 libavutil \
							 liblincrypto \
							 liblinphone \
							 liblinphonenoneon \
							 liblinssl \
							 libsrtp \
							 libswscale	\
							 libbcg729	\
							 libSmartHomeTranComm	\
							 libBaseLib	\
							 libDbgLogCtrl	\
							 libvideomonitor \
							 libSmartHomeAvRecord \
							 liba01jni \
                             libbdpush_V2_2 \
                             libserial_port
                             

LOCAL_PACKAGE_NAME := THTFITPOS

LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED := full
LOCAL_PROGUARD_FLAG_FILES := proguard.flags


include $(BUILD_PACKAGE)
#####################################
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := activate:libs/activation.jar \
                                        support:libs/android-support-v4.jar \
									    dspread:libs/dspread-android-sdk-2.1.4.jar \
									    javamail:libs/javax.mail.jar \
									    mpandroidchar:libs/mpandroidchartlibrary-1-7-4.jar \
									    pushservice:libs/pushservice-4.3.0.4.jar \
									    universal:libs/universal-image-loader-1.8.6-with-sources.jar \
									    gson:libs/gson-2.2.4.jar \
                                        libarityXml:libs/aXMLRPC.jar	

LOCAL_PREBUILT_LIBS :=liba01jni:libs/armeabi/liba01jni.so \
                      libbdpush_V2_2:libs/armeabi/libbdpush_V2_2.so \
                      lib_serial_port:libs/armeabi/lib_serial_port.so 
					   
include $(BUILD_MULTI_PREBUILT)
include $(call all-makefiles-under,$(LOCAL_PATH))

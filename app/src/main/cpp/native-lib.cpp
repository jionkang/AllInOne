#include <jni.h>
#include <string>
#include <android/log.h>

#include "std_handler/std_handler.h"

#define  LOG(...)  __android_log_print(ANDROID_LOG_ERROR,"all-native",__VA_ARGS__)


extern "C" JNIEXPORT jstring JNICALL
Java_com_anc_allinone_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    Std_handler stdh;
    stdh.main();

    return env->NewStringUTF(hello.c_str());
}

#include <jni.h>
#include <string>
#include <stdio.h>

#include <android/log.h>
#include <iostream> //To change the formatting of std::cerr.
#include <signal.h> //For floating point exceptions.
#if defined(__linux__) || (defined(__APPLE__) && defined(__MACH__))
#include <sys/resource.h> //For setpriority.
#endif
#include "src/Application.h"
#include "src/utils/logoutput.h"
#include "src/communication/CommandLine.h"
#include "src/communication/Communication.h"
namespace cura
{
//Signal handler for a "floating point exception", which can also be integer division by zero errors.
    void signal_FPE(int n)
    {
        (void)n;
        logError("Arithmetic exception.\n");
        exit(1);
    }
}//namespace cura
using  namespace cura;

#define LOG_TAG "System.out.c"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_mukesh_drawingview_example_JianBiHua_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "切片成功";
#if defined(__linux__) || (defined(__APPLE__) && defined(__MACH__))
    //Lower the process priority on linux and mac. On windows this is done on process creation from the GUI.
    setpriority(PRIO_PROCESS, 0, 10);
#endif
#ifndef DEBUG
    //Register the exception handling for arithmetic exceptions, this prevents the "something went wrong" dialog on windows to pop up on a division by zero.
    signal(SIGFPE, cura::signal_FPE);
#endif
    LOGI("开始切片");
    std::cerr << std::boolalpha;
    LOGI("正在切片");
    char *myargv[13] = {"a", "slice", "-v", "-j", "/mnt/sdcard/test/fdmprinter.def.json",
                        "-v", "-j",
                        "/mnt/sdcard/test/fdmextruder.def.json","-o",
                        "/mnt/sdcard/test/龚雨晨.gcode","-e1","-l",
                        "/mnt/sdcard/test/诗歌.STL"};
    int myargc = 13;
    cura::Application::getInstance().run((unsigned int)myargc, (char**)myargv);
    return env->NewStringUTF(hello.c_str());
}

char* jstringToChar(JNIEnv* env, jstring jstr) {
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char*) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_mukesh_drawingview_example__04eba_07269_089d2_08272_stringFromJNI2(
        JNIEnv *env,
        jobject /* this */, jstring stl_path, jstring gcode_path) {
    std::string hello = "切片成功";


#if defined(__linux__) || (defined(__APPLE__) && defined(__MACH__))
    //Lower the process priority on linux and mac. On windows this is done on process creation from the GUI.
    setpriority(PRIO_PROCESS, 0, 10);
#endif
#ifndef DEBUG
    //Register the exception handling for arithmetic exceptions, this prevents the "something went wrong" dialog on windows to pop up on a division by zero.
    signal(SIGFPE, cura::signal_FPE);
#endif
    LOGI("开始切片");
    std::cerr << std::boolalpha;
    LOGI("正在切片");
    char *myargv[13] = {"a", "slice", "-v", "-j", "/mnt/sdcard/test/fdmprinter.def.json", "-v", "-j",
                        "/mnt/sdcard/test/fdmextruder.def.json", "-o",
                        jstringToChar(env, gcode_path), "-e1", "-l",
                        jstringToChar(env, stl_path)};
    int myargc = 13;
    cura::Application::getInstance().run((unsigned int)myargc, (char**)myargv);
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_mukesh_drawingview_example_UPicture_stringFromJNI3(
        JNIEnv *env,
        jobject /* this */, jstring stl_path, jstring gcode_path) {
    std::string hello = "切片成功";


#if defined(__linux__) || (defined(__APPLE__) && defined(__MACH__))
    //Lower the process priority on linux and mac. On windows this is done on process creation from the GUI.
    setpriority(PRIO_PROCESS, 0, 10);
#endif
#ifndef DEBUG
    //Register the exception handling for arithmetic exceptions, this prevents the "something went wrong" dialog on windows to pop up on a division by zero.
    signal(SIGFPE, cura::signal_FPE);
#endif
    LOGI("开始切片");
    std::cerr << std::boolalpha;
    LOGI("正在切片");
    char *myargv[13] = {"a", "slice", "-v", "-j", "/mnt/sdcard/test/fdmprinter.def.json", "-v", "-j",
                        "/mnt/sdcard/test/fdmextruder.def.json", "-o",
                        jstringToChar(env, gcode_path), "-e1", "-l",
                        jstringToChar(env, stl_path)};
    int myargc = 13;
    cura::Application::getInstance().run((unsigned int)myargc, (char**)myargv);
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mukesh_drawingview_example__052a8_07269_0690d_07269_stringFromJNI4(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring stl_path,
                                                                            jstring gcode_path) {
    // TODO: implement stringFromJNI4()
    std::string hello = "切片成功";


#if defined(__linux__) || (defined(__APPLE__) && defined(__MACH__))
    //Lower the process priority on linux and mac. On windows this is done on process creation from the GUI.
    setpriority(PRIO_PROCESS, 0, 10);
#endif
#ifndef DEBUG
    //Register the exception handling for arithmetic exceptions, this prevents the "something went wrong" dialog on windows to pop up on a division by zero.
    signal(SIGFPE, cura::signal_FPE);
#endif
    LOGI("开始切片");
    std::cerr << std::boolalpha;
    LOGI("正在切片");
    char *myargv[13] = {"a", "slice", "-v", "-j", "/mnt/sdcard/test/fdmprinter.def.json", "-v", "-j",
                        "/mnt/sdcard/test/fdmextruder.def.json", "-o",
                        jstringToChar(env, gcode_path), "-e1", "-l",
                        jstringToChar(env, stl_path)};
    int myargc = 13;
    cura::Application::getInstance().run((unsigned int)myargc, (char**)myargv);
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_mukesh_drawingview_example__06559_080b2_05b66_04e60_stringFromJNI5(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring stl_path,
                                                                            jstring gcode_path) {
    // TODO: implement stringFromJNI5()
    std::string hello = "切片成功";


#if defined(__linux__) || (defined(__APPLE__) && defined(__MACH__))
    //Lower the process priority on linux and mac. On windows this is done on process creation from the GUI.
    setpriority(PRIO_PROCESS, 0, 10);
#endif
#ifndef DEBUG
    //Register the exception handling for arithmetic exceptions, this prevents the "something went wrong" dialog on windows to pop up on a division by zero.
    signal(SIGFPE, cura::signal_FPE);
#endif
    LOGI("开始切片");
    std::cerr << std::boolalpha;
    LOGI("正在切片");
    char *myargv[13] = {"a", "slice", "-v", "-j", "/mnt/sdcard/test/fdmprinter.def.json", "-v", "-j",
                        "/mnt/sdcard/test/fdmextruder.def.json", "-o",
                        jstringToChar(env, gcode_path), "-e1", "-l",
                        jstringToChar(env, stl_path)};
    int myargc = 13;
    cura::Application::getInstance().run((unsigned int)myargc, (char**)myargv);
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mukesh_drawingview_example__05de5_05177_0914d_04ef6_stringFromJNI6(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring stl_path,
                                                                            jstring gcode_path) {
    // TODO: implement stringFromJNI6()
    std::string hello = "切片成功";


#if defined(__linux__) || (defined(__APPLE__) && defined(__MACH__))
    //Lower the process priority on linux and mac. On windows this is done on process creation from the GUI.
    setpriority(PRIO_PROCESS, 0, 10);
#endif
#ifndef DEBUG
    //Register the exception handling for arithmetic exceptions, this prevents the "something went wrong" dialog on windows to pop up on a division by zero.
    signal(SIGFPE, cura::signal_FPE);
#endif
    LOGI("开始切片");
    std::cerr << std::boolalpha;
    LOGI("正在切片");
    char *myargv[13] = {"a", "slice", "-v", "-j", "/mnt/sdcard/test/fdmprinter.def.json", "-v", "-j",
                        "/mnt/sdcard/test/fdmextruder.def.json", "-o",
                        jstringToChar(env, gcode_path), "-e1", "-l",
                        jstringToChar(env, stl_path)};
    int myargc = 13;
    cura::Application::getInstance().run((unsigned int)myargc, (char**)myargv);
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_mukesh_drawingview_example_JianBiHua_stringFromJNI6(
        JNIEnv *env,
        jobject /* this */, jstring stl_path, jstring gcode_path) {
    std::string hello = "切片成功";


#if defined(__linux__) || (defined(__APPLE__) && defined(__MACH__))
    //Lower the process priority on linux and mac. On windows this is done on process creation from the GUI.
    setpriority(PRIO_PROCESS, 0, 10);
#endif
#ifndef DEBUG
    //Register the exception handling for arithmetic exceptions, this prevents the "something went wrong" dialog on windows to pop up on a division by zero.
    signal(SIGFPE, cura::signal_FPE);
#endif
    LOGI("开始切片");
    std::cerr << std::boolalpha;
    LOGI("正在切片");
    char *myargv[13] = {"a", "slice", "-v", "-j", "/mnt/sdcard/test/fdmprinter.def.json", "-v", "-j",
                        "/mnt/sdcard/test/fdmextruder.def.json", "-o",
                        jstringToChar(env, gcode_path), "-e1", "-l",
                        jstringToChar(env, stl_path)};
    int myargc = 13;
    cura::Application::getInstance().run((unsigned int)myargc, (char**)myargv);
    return env->NewStringUTF(hello.c_str());
}
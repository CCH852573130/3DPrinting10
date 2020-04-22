//Copyright (c) 2018 Ultimaker B.V.
//CuraEngine is released under the terms of the AGPLv3 or higher.

#ifndef LOGOUTPUT_H
#define LOGOUTPUT_H
#include "android/log.h"
//#define LOG_TAG "log_debug"
#define LOG_TAG "魏"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define logError(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define logDebug(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define logWarning(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define logAlways(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define log(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define logProgress(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
namespace cura {
void increaseVerboseLevel();

void enableProgressLogging();

/*
 * \brief Report an error message.
 *
 * This is always reported, regardless of verbosity level.
 */
//void logError(const char* fmt, ...);

/*
 * \brief Report a warning message.
 * 
 * Always reported, regardless of verbosity level.
 */
//void logWarning(const char* fmt, ...);

/*
 * \brief Report a message if the verbosity level is 1 or higher.
 */
//void log(const char* fmt, ...);

/*
 * \brief Log a message, regardless of verbosity level.
 */
//void logAlways(const char* fmt, ...);

/*
 * \brief Log a debugging message.
 *
 * The message is only logged if the verbosity level is 2 or higher.
 */
//void logDebug(const char* fmt, ...);

/*
 * \brief Report the progress in the log.
 *
 * Only works if ``enableProgressLogging()`` has been called.
 */
//void logProgress(const char* type, int value, int maxValue, float percent);
} //namespace cura

#endif //LOGOUTPUT_H

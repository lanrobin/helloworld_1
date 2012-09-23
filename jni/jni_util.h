#ifndef __JNI_UTIL_H__
#define __JNI_UTIL_H__
#include <jni.h>
jstring
charToString(JNIEnv* env, const char* pat);

char*
jstringTochar(JNIEnv* env, jstring jstr);

unsigned int clp2(unsigned int x);
#endif // __JNI_UTIL_H__

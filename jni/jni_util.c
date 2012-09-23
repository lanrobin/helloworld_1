#include <stdio.h>
#include <string.h>

#include "alog.h"
#include "jni_util.h"
jstring
charToString(JNIEnv* env, const char* pat)
{
  LOGD("new jstring with = ", pat);
  jclass strClass = (*env)->FindClass(env, "java/lang/String");
  jmethodID ctorID = (*env)->GetMethodID(env, strClass, "<init>", "([BLjava/lang/String;)V");
  jbyteArray bytes = (*env)->NewByteArray(env, strlen(pat));
  (*env)->SetByteArrayRegion(env, bytes, 0, strlen(pat), (jbyte*) pat);
  jstring encoding = (*env)->NewStringUTF(env, "utf-8");
  return (jstring) (*env)->NewObject(env, strClass, ctorID, bytes, encoding);
}

char*
jstringTochar(JNIEnv* env, jstring jstr)
{
  char* rtn = NULL;
  jclass clsstring = (*env)->FindClass(env,"java/lang/String");
  jstring strencode = (*env)->NewStringUTF(env,"utf-8");
  jmethodID mid = (*env)->GetMethodID(env,clsstring, "getBytes",
      "(Ljava/lang/String;)[B");
  jbyteArray barr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, mid, strencode);
  jsize alen = (*env)->GetArrayLength(env, barr);
  jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
  if (alen > 0)
    {
      rtn = (char*) malloc(alen + 1);

      memcpy(rtn, ba, alen);
      rtn[alen] = 0;
    }
  (*env)->ReleaseByteArrayElements(env, barr, ba, 0);
  return rtn;
}


unsigned int clp2(unsigned int x) {
   x = x - 1;
   x = x | (x >> 1);
   x = x | (x >> 2);
   x = x | (x >> 4);
   x = x | (x >> 8);
   x = x | (x >>16);
   return x + 1;
}

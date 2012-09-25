#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "lame_wrapper.h"
#include "lame.h"
#include "alog.h"
#include "jni_util.h"

static lame_global_flags * gpContext = NULL;
static int gOutBufferSize = 0;
static char * gpOutBuffer = NULL;
/*
 * Class:     com_irefire_mp3_LameWrapper
 * Method:    nativeGetVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL
Java_com_irefire_mp3_LameWrapper_nativeGetVersion(JNIEnv * env, jclass obj)
{
  LOGD("nativeGetVersion");
  return charToString(env, "3.99.5");
}

/*
 * Class:     com_irefire_mp3_LameWrapper
 * Method:    nativeInit
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL
Java_com_irefire_mp3_LameWrapper_nativeInit(JNIEnv * env, jclass obj, jint srate,
    jint chanel, jint outSampleRate, jint quality)
{
  if (gpContext != NULL)
    {
      return 1; // already initialzed.
    }

  gpContext = lame_init();
  if (gpContext == NULL)
    {
      return 2;
    }

  lame_set_in_samplerate(gpContext, srate);
  lame_set_num_channels(gpContext, chanel);
  lame_set_VBR(gpContext,vbr_default);
  lame_set_brate(gpContext,128);     /* 比特率 */
  lame_set_mode(gpContext,3);    /* mode = 0,1,2,3 = stereo,jstereo,dualchannel(not supported),mono default */
  lame_set_out_samplerate(gpContext, outSampleRate);
  lame_set_num_samples(gpContext, 2);
  lame_set_quality(gpContext, quality);

}

/*
 * Class:     com_irefire_mp3_LameWrapper
 * Method:    nativePrepare
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_com_irefire_mp3_LameWrapper_nativePrepare(JNIEnv * env, jclass obj)
{
  return lame_init_params(gpContext);
}

/*
 * Class:     com_irefire_mp3_LameWrapper
 * Method:    nativeEncodeBuffer
 * Signature: ([S[SI)[B
 */
JNIEXPORT jbyteArray JNICALL
Java_com_irefire_mp3_LameWrapper_nativeEncodeBuffer(JNIEnv * env, jclass obj,
    jshortArray left, jshortArray right, jint samples)
{
  /*每次都算最大的缓冲区*/
  int currentSize = clp2((int) (1.25 * samples + 7200));
  if (currentSize > gOutBufferSize)
    {
      free(gpOutBuffer);
      gpOutBuffer = (char *) malloc(currentSize);
      gOutBufferSize = currentSize;
    }
  jshort * dataLeft = (*env)->GetShortArrayElements(env, left, JNI_FALSE);
  jshort * dataRight = (*env)->GetShortArrayElements(env, right, JNI_FALSE);
  int len = lame_encode_buffer(gpContext, dataLeft, dataRight, samples,
      gpOutBuffer, gOutBufferSize);
  if(len < 0 ){

        // 如果出错，返回没有数据的数组
        len  = 0;
    }
  (*env)->ReleaseShortArrayElements(env, left, dataLeft, JNI_FALSE);
  (*env)->ReleaseShortArrayElements(env, right, dataRight, JNI_FALSE);
  jbyteArray result = (*env)->NewByteArray(env, len);
  (*env)->SetByteArrayRegion(env, result, 0, len, gpOutBuffer);
  return result;
}

/*
 * Class:     com_irefire_mp3_LameWrapper
 * Method:    nativeEncoderBufferInterleaved
 * Signature: ([SI)[B
 */
JNIEXPORT jbyteArray JNICALL
Java_com_irefire_mp3_LameWrapper_nativeEncoderBufferInterleaved(JNIEnv * env,
    jclass obj, jshortArray pcm, jint samples)
{
  /*每次都算最大的缓冲区*/
  int orgSize = (int) (1.25 * samples + 7200);
  int currentSize = clp2(orgSize);
  LOGD("orgSize:");
  LOGD(orgSize);
  LOGD("sizes:");
  LOGD(currentSize);
  if (currentSize > gOutBufferSize)
    {
      free(gpOutBuffer);
      gpOutBuffer = (char *) malloc(currentSize);
      gOutBufferSize = currentSize;
    }
  jshort * data = (*env)->GetShortArrayElements(env, pcm, JNI_FALSE);
  int len = lame_encode_buffer_interleaved(gpContext, data, samples,
      gpOutBuffer, gOutBufferSize);
  if(len < 0 ){

        // 如果出错，返回没有数据的数组
        len  = 0;
    }
  (*env)->ReleaseShortArrayElements(env, pcm, data, JNI_FALSE);
  jbyteArray result = (*env)->NewByteArray(env, len);
  (*env)->SetByteArrayRegion(env, result, 0, len, gpOutBuffer);
  return result;
}

/*
 * Class:     com_irefire_mp3_LameWrapper
 * Method:    nativeEncodeFlush
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL
Java_com_irefire_mp3_LameWrapper_nativeEncodeFlush(JNIEnv * env, jclass obj)
{
  int currentSize = 14400;
  /*最少是7200，但是分配2倍*/
  if (gOutBufferSize < currentSize)
    {
      free(gpOutBuffer);
      gpOutBuffer = (char *) malloc(currentSize);
      gOutBufferSize = currentSize;
    }
  int len = lame_encode_flush(gpContext, gpOutBuffer, gOutBufferSize);
  if(len < 0 ){

      // 如果出错，返回没有数据的数组
      len  = 0;
  }
  jbyteArray result = (*env)->NewByteArray(env, len);
    (*env)->SetByteArrayRegion(env, result, 0, len, gpOutBuffer);
    return result;
}

/*
 * Class:     com_irefire_mp3_LameWrapper
 * Method:    nativeRelease
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_irefire_mp3_LameWrapper_nativeRelease
(JNIEnv * env, jclass obj)
  {
    free(gpOutBuffer);
    gpOutBuffer = NULL;
    lame_close(gpContext);
    gpContext = NULL;
  }

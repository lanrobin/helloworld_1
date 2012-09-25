package com.irefire.mp3;

/**
 * 
 * @author lanhuanze
 *
 *这个类是对LAME的包装，只支持一个实例和单线程，所以不要在多线程的环境下用。
 */
public class LameWrapper {

	/**
	 * 获得当前LAME的版本
	 * @return
	 */
	public static String getVersion() {
		return nativeGetVersion();
	}
	
	/**
	 * 初始化
	 * @param sampleRate input sample rate in Hz.  default = 44100hz
	 * @param chanel number of channels in input stream. default=2
	 * @param rate   output sample rate in Hz.  default = 0, which means LAME picks best value
  	 *				 based on the amount of compression.  MPEG only allows:
  	 *				MPEG1    32, 44.1,   48khz
  	 *              MPEG2    16, 22.05,  24
     *              MPEG2.5   8, 11.025, 12 (not used by decoding routines)
	 * @param quality   internal algorithm selection.  True quality is determined by the bitrate
     *but this variable will effect quality by selecting expensive or cheap algorithms.
     * quality=0..9.  0=best (very slow).  9=worst.
     * recommended:  2     near-best quality, not too slow
     *           5     good quality, fast
     *           7     ok quality, really fast
	 * @return 0,success; 1, already initialized; 2, OOM; other, other errors.
	 */
	public static int init(int sampleRate, int chanel, int outSampleRate, int quality) {
		return nativeInit(sampleRate,chanel, outSampleRate, quality);
	}
	
	/**
	 * 准备，在开始编码前作准备
	 * sets more internal configuration based on data provided above.
     *@return -1 if something failed.
	 */
	public static int prepare() {
		return nativePrepare();
	}
	
	/**
	 * input pcm data, output (maybe) mp3 frames.
	 * This routine handles all buffering, resampling and filtering for you.
	 *
	 * return code     number of bytes output in mp3buf. Can be 0
	 *                 -1:  mp3buf was too small
	 *                 -2:  malloc() problem
	 *                 -3:  lame_init_params() not called
	 *                 -4:  psycho acoustic problems
	 *
	 * The required mp3buf_size can be computed from num_samples,
	 * samplerate and encoding rate, but here is a worst case estimate:
	 *
	 * mp3buf_size in bytes = 1.25*num_samples + 7200
	 *
	 * I think a tighter bound could be:  (mt, March 2000)
	 * MPEG1:
	 *    num_samples*(bitrate/8)/samplerate + 4*1152*(bitrate/8)/samplerate + 512
	 * MPEG2:
	 *    num_samples*(bitrate/8)/samplerate + 4*576*(bitrate/8)/samplerate + 256
	 *
	 * but test first if you use that!
	 *
	 * set mp3buf_size = 0 and LAME will not check if mp3buf_size is
	 * large enough.
	 *
	 * NOTE:
	 * if gfp->num_channels=2, but gfp->mode = 3 (mono), the L & R channels
	 * will be averaged into the L channel before encoding only the L channel
	 * This will overwrite the data in buffer_l[] and buffer_r[].
	 *
	*/
	public static byte[] encodeBuffer(short[] leftChanel, short[] rightChanel, int sampleNumber) {
		return nativeEncodeBuffer(leftChanel,rightChanel, sampleNumber);
	}
	
	
	/**
	 * as above, but input has L & R channel data interleaved.
	 * NOTE:
	 * num_samples = number of samples in the L (or R)
	 * channel, not the total number of samples in pcm[]
	 */
	public static byte[] encoderBufferInterleaved(short[] pcm, int sampleNumber) {
		return nativeEncoderBufferInterleaved(pcm, sampleNumber);
	}
	
	
	/**
	 * REQUIRED:
	 * lame_encode_flush will flush the intenal PCM buffers, padding with
	 * 0's to make sure the final frame is complete, and then flush
	 * the internal MP3 buffers, and thus may return a
	 * final few mp3 frames.  'mp3buf' should be at least 7200 bytes long
	 * to hold all possible emitted data.
	 *
	 * will also write id3v1 tags (if any) into the bitstream
	 *
	 * return code = number of bytes output to mp3buf. Can be 0
	 */
	public static byte[] encodeFlush() {
		return nativeEncodeFlush();
	}
	
	/*
	 * REQUIRED:
	 * final call to free all remaining buffers
	 */
	public static void release() {
		nativeRelease();
	}
	// 以下是native方法
	private static native String nativeGetVersion();
	private static native int nativeInit(int sampleRate, int chanel, int outSampleRate, int quality);
	private static native int nativePrepare();
	private static native byte[] nativeEncodeBuffer(short[] leftChanel, short[] rightChanel, int sampleNumber);
	private static native byte[] nativeEncoderBufferInterleaved(short[] pcm, int sampleNumber);
	private static native byte[] nativeEncodeFlush();
	private static native void nativeRelease();
	
	static {
		System.loadLibrary("mp3lame");
	}
}

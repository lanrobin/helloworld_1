package com.irefire.utils;

import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SdcardUtil {

	private static final String TAG = "SdcardUtil";

	private static final int DIR_SEGMENT = 6;

	private static String defaultTimeStampPattern = "yyyyMMdd-HHmmss";

	private static String APP_STORE_FOLDER = "CloudAudioRecord";

	private static String APP_NOMEDIA_FOLDER = ".nomedia";

	public static final String SDCARD_FREE_SPACE_ACTION = "com.clsoftware.spycamera.util.SDCARD_FREE_SPACE_ACTION";
	public static final String EXTRA_FREE_M = "EXTRA_FREE_M";
	public static final String EXTRA_TOTAL_M = "EXTRA_TOTAL_M";

	/**
	 * 文件类型，目前只有图像，jpg;录音，pcm;录像，3pg.
	 */
	public static final int FILE_TYPE_JPG = 0;
	public static final int FILE_TYPE_AMR = 1;
	public static final int FILE_TYPE_3GP = 2;
	public static final int FILE_TYPE_MP4 = 3;
	public static final int FILE_TYPE_MP3 = 4;
	public static final int FILE_TYPE_CAR = 5;  //自己的加密文件格式
	public static final int FILE_TYPE_UNKOWN = 6;

	private static String[] FILE_EXTENSION = { "1.jpg", "2.amr", "3.3gp",
			"4.mp4", "5.raw" , "6.mp3", "7.car"};

	/**
	 * 获取唯一的文件名，用于存放文件。
	 */
	private static String getFileName(int fileType) {
		StringBuilder builder = new StringBuilder(20);
		SimpleDateFormat df = new SimpleDateFormat(defaultTimeStampPattern);
		builder.append(df.format(new Date()));
		builder.append(FILE_EXTENSION[fileType]);
		return builder.toString();
	}

	public static String getStoreFileName(int fileType) {
		// filename: /mnt/sdcard/skyvision/20110917-083534.amr
		// String sdcard = "/sdcard/external_sd" ;
		// String sdcard = Config.getInstance().getStorePath();
		String sdcard = "/mnt/sdcard";
		StringBuilder builder = new StringBuilder(50);
		builder.append(sdcard);
		builder.append(File.separator);
		builder.append(APP_STORE_FOLDER);
		File folder = new File(builder.toString());

		// if this is file, delete it first.
		if (folder.exists()) {
			if (folder.isFile()) {
				folder.delete();

				// we need to create a new file.
				folder = new File(builder.toString());
			}
			folder.mkdir();
		} else {
			folder.mkdir();
		}

		// create a subfolder named .nomedia to hidden media files from scanned.
		File noMedia = new File(builder.toString() + File.separator
				+ APP_NOMEDIA_FOLDER);
		if (noMedia.exists()) {
			if (noMedia.isFile()) {
				noMedia.delete();
				noMedia = new File(builder.toString() + File.separator
						+ APP_NOMEDIA_FOLDER);
			}
			noMedia.mkdir();
		} else {
			noMedia.mkdir();
		}

		builder.append(File.separator);
		builder.append(getFileName(fileType));
		return builder.toString();
	}

	public static List<String> getSDcardsPathes() {
		List<String> list = new ArrayList<String>();
		String root = "/mnt";
		String[] dirs = getRawPaths(root);
		if (dirs != null && dirs.length > 0) {
			for (String file : dirs) {
				if (file.startsWith("d")) {
					String[] seg = split(file, DIR_SEGMENT);
					// 如果是以sdcard开始的为SD卡，在moto手机上为sdcard 和sdcard-ext
					if (!TextUtils.isEmpty(seg[5])) {
						if (seg[5].startsWith("sdcard")
								|| seg[5].startsWith("external_sd")) {
							list.add(root + File.separator + seg[5]);
							L.d(TAG, "Add init path :" + root + File.separator
									+ seg[5]);
						}
					}
					L.d(TAG, "seg[7]:" + Arrays.toString(seg));
				}
			}
			// special for a customer:
			// list.add("/sdcard/external_sd");
		}
		return list;
	}

	/**
	 * Get the specific sdcard total and free space in MB.
	 * 
	 * @param path
	 *            , sdcard path
	 * @return List(total, free).
	 */
	public static List<Long> getSDcardSpace(String path) {
		List<Long> list = new ArrayList<Long>();
		long total = 0;
		long free = 0;
		if (!TextUtils.isEmpty(path)) {
			StatFs fs = new StatFs(path);
			int count = fs.getBlockCount();
			int bs = fs.getBlockSize();
			int ab = fs.getAvailableBlocks();
			// overflow, divide it first
			double mid = count / 1024.0;
			total = (long) (mid * bs);
			mid = ab / 1024.0;
			free = (long) (mid * bs);

			// it is now in byte, we turn it to MB
			total = (total / 1024);
			free = (free / 1024);

			list.add(total);
			list.add(free);
		}
		return list;
	}

	private static String[] getRawPaths(String path) {
		int len = 0;
		String[] files = null;

		if (TextUtils.isEmpty(path)) {
			Log.w(TAG, "Empty path to scan!!!!!");
			for (StackTraceElement e : Thread.getAllStackTraces().get(
					Thread.currentThread())) {
				Log.w(TAG, e.toString());
			}
			throw new NullPointerException("getRawPaths try to scan null dir.");
		}
		try {
			// 带空格
			String[] commands = new String[] { "ls", "-l", " ", path };
			Process p = Runtime.getRuntime().exec(commands);
			p.waitFor(); // 等待程序运行结束。
			InputStream is = p.getInputStream();
			len = is.available();
			if (len > 0) {
				byte[] data = new byte[len];
				is.read(data);
				String s = new String(data);
				L.d(TAG, "ls -l \'" + path + "\' returns:\n" + s);
				if (!TextUtils.isEmpty(s)) {
					files = s.split("\n");
				}
			}
		} catch (IOException ioe) {
			Log.d(TAG, "getRawFile throw IOException:" + ioe.getMessage());
		} catch (InterruptedException ie) {
			Log.d(TAG,
					"getRawFile throw InterruptedException:" + ie.getMessage());
		}
		return files;
	}

	private static String[] split(String src, int number) {
		if (TextUtils.isEmpty(src) || number < 1) {
			Log.w(TAG, "try to split null string or negetive parts.");
			return null;
		}

		String[] segs = new String[number];
		int begin = 0;
		int end = 0;
		int index = 0;
		int i = 0;
		boolean bPreCharIsSpace = false;
		while (i < src.length() && index < number) {

			if (src.charAt(i) == ' ') {
				// 如果前一个字符不是空格，而这个字符是空格，则说明到了拆分的地方了。
				if (!bPreCharIsSpace) {

					end = i;
					segs[index++] = src.substring(begin, end);
				} else { // 如果前一个字符也是空格，这个虽然也是，但是如果到了最后一个串，则不管了，。
					// 如果到达了拆分的个数，则最后的字符串算着一个，不再进行拆分，主要是考虑文件或目录有空格的情况。
					if (index == number - 1) {
						begin = i;
						break;
					}
				}
				bPreCharIsSpace = true;
			} else {
				// 如果前一个空格，这一个不是，则一个字符串拆分的开始。
				if (bPreCharIsSpace) {
					begin = i;
					// 如果到达了拆分的个数，则最后的字符串算着一个，不再进行拆分，主要是考虑文件或目录有空格的情况。
					if (index == number - 1) {
						break;
					}
				}
				bPreCharIsSpace = false;
			}
			i++;
		}
		segs[number - 1] = src.substring(begin);
		return segs;
	}
}

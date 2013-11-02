package codevenger.timera.utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

public class PathTools {

	public static File getNewFile() {
		createDirectory();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.US);
		String filename = format.format(new Date());
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Timera" + File.separator + filename + ".jpg");
		return file;
	}

	public static File getNewMixedFile() {
		createDirectory();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.US);
		String filename = format.format(new Date());
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Timera" + File.separator + "mixed" + File.separator + filename
						+ ".jpg");
		return file;
	}

	public static void createDirectory() {
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Timera" + File.separator + "mixed");
		if (!file.exists()) {
			file.mkdirs();
		}
	}

}

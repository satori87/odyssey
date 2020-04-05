package autoversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

public class AutoVersion {

	public static void main(String[] args) {

		autoVersion();

	}

	static void log(Object o) {
		System.out.println((String) o);
	}

	public static void autoVersion() {
		try {
			File upd = new File("update.dat");
			if (upd.exists()) {
				upd.delete();
			}
			walk(new File("."));
			FileOutputStream fos = new FileOutputStream(upd);
			String line = "";
			for (String s : names) {
				if (!s.equals("av.jar")) {
					line = (s + "," + calcMD5Local(s) + "," + getFileSize(s) + "\n").replace("\\", "/");
					fos.write(line.getBytes());
					log(line);
				}
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static ArrayList<String> names = new ArrayList<String>();

	public static void walk(File dir) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					// System.out.println("directory:" + file.getCanonicalPath());
					walk(file);
				} else {
					names.add(file.getPath().substring(2));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String calcMD5Local(String fileName) {
		InputStream fis;
		String md5 = "";
		try {
			fis = new FileInputStream(new File(fileName));
			boolean success = true;

			do {
				try {
					md5 = getMD5Checksum(fis);
					success = true;
				} catch (Exception e) {
					success = false;
				} finally {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} while (success == false);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return md5;
	}

	public static long getFileSize(String s) {
		File f = new File(s);
		return f.length();
	}

	public static byte[] createChecksum(InputStream fis) {
		byte[] buffer = new byte[1024];
		MessageDigest complete;
		try {
			complete = MessageDigest.getInstance("MD5");
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			fis.close();
			return complete.digest();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	public static String getMD5Checksum(InputStream fis) {
		byte[] b = createChecksum(fis);
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

}

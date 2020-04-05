package com.bg.bearplane.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class BearTool {

	public static double distance(int x1, int y1, int x2, int y2) {
		return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}

	public static String encryptPassword(String password) {
		try {
			MessageDigest messageDigest;
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(password.getBytes());
			return new String(messageDigest.digest());
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		return "";
	}

	public static int rndInt(int min, int max) {
		return randInt(min, max);
	}

	static Random rand = new Random(System.currentTimeMillis() / 2);

	public static int randInt(int min, int max) {

		// NOTE: This will (intentionally) not run as written so that folks
		// copy-pasting have to think about how to initialize their
		// Random instance. Initialization of the Random instance is outside
		// the main scope of the question, but some decent options are to have
		// a field that is initialized once and then re-used as needed or to
		// use ThreadLocalRandom (if using at least Java 1.7).
		//
		// In particular, do NOT do 'Random rand = new Random()' here or you
		// will get not very good / not very random results.

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static String getDate(String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(new Date());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object deserialize(byte[] data, Class c) {
		Object object = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			InflaterInputStream is = new InflaterInputStream(ois);
			Input input = new Input(is);
			Kryo kryo = new Kryo();
			object = kryo.readObject(input, c);
			input.close();
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		return object;
	}

	public static byte[] serialize(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			DeflaterOutputStream os = new DeflaterOutputStream(oos);
			Output output = new Output(os);
			Kryo kryo = new Kryo();
			kryo.writeObject(output, object);
			output.close();
			return baos.toByteArray();
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		return null;
	}

	public static byte[][] divideArray(byte[] source, int chunksize) {
		byte[][] ret = new byte[(int) Math.ceil(source.length / (double) chunksize)][chunksize];
		try {
			int start = 0;
			for (int i = 0; i < ret.length; i++) {
				ret[i] = Arrays.copyOfRange(source, start, start + chunksize);
				start += chunksize;
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		return ret;
	}

	static public boolean inCenteredBox(int x, int y, int centerX, int centerY, int width, int height) {
		int topY = centerY - (height / 2);
		int bottomY = centerY + (height / 2);
		int leftX = centerX - (width / 2);
		int rightX = centerX + (width / 2);
		if (x > leftX && x < rightX && y > topY && y < bottomY) {
			return true;
		}
		return false;
	}

	static public boolean inBox(int x, int y, int lowerX, int upperX, int lowerY, int upperY) {
		return (x >= lowerX && x < upperX && y >= lowerY && y < upperY);
	}

	public static int reverseDir(int d) {
		int n = d;
		if (d == 0) {
			n = 1;
		} else if (d == 1) {
			n = 0;
		} else if (d == 2) {
			n = 3;
		} else if (d == 3) {
			n = 2;
		}
		return n;
	}

	public static void assureDir(String s) {
		File directory = new File(s);
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	public static int setBit(int value, int bit) {
		// Create mask
		int mask = 1 << bit;
		// Set bit
		return value | mask;
	}

	public static int clearBit(int x, int kth) {
		  return (x & ~(1 << kth));
		}

	public static boolean checkBit(int value, int bit) {
		return ((value >> bit) & 1) != 0;
	}
	
	public static int setBit(int value, int bit, boolean to) {
		if(to) {
			return setBit(value,bit);
		} else {
			return clearBit(value,bit);
		}
	}
	

}

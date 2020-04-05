// 
// Decompiled by Procyon v0.5.36
// 

package com.bg.bearplane.furpatch;

import java.security.MessageDigest;
import com.badlogic.gdx.graphics.Color;
import java.util.Iterator;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.Net;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Queue;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Patch {
	Button[] buttons;
	GameScreen screen;
	UpdFile curUpdFile;
	boolean first;
	boolean firstPass;
	long tick;
	String curFile;
	AtomicInteger prog;
	AtomicBoolean downloading;
	InputStream is;
	OutputStream os;
	Queue<UpdFile> files;
	int curTask;
	long stamp;
	AtomicBoolean failed;
	String curName;
	AtomicLong dstamp;
	AtomicInteger lprog;
	Net.HttpRequest request;
	AtomicLong tprog;
	AtomicLong sBytes;
	long sStamp;
	int kbs;
	long totalSize;
	List<String> news;
	int scroll;

	public Patch(final GameScreen screen) {
		this.buttons = new Button[10];
		this.curUpdFile = new UpdFile();
		this.first = true;
		this.firstPass = true;
		this.tick = 0L;
		this.curFile = "";
		this.prog = new AtomicInteger(0);
		this.downloading = new AtomicBoolean(false);
		this.files = new LinkedList<UpdFile>();
		this.curTask = 0;
		this.stamp = 0L;
		this.failed = new AtomicBoolean(false);
		this.curName = "";
		this.dstamp = new AtomicLong(0L);
		this.lprog = new AtomicInteger(0);
		this.request = null;
		this.tprog = new AtomicLong(0L);
		this.sBytes = new AtomicLong(0L);
		this.sStamp = 0L;
		this.kbs = 0;
		this.totalSize = 0L;
		this.news = new ArrayList<String>();
		this.scroll = 0;
		this.screen = screen;
	}

	void buttonPressed(final int id) {
		if (id == 0) {
			this.scroll -= 5;
			if (this.scroll < 0) {
				this.scroll = 0;
			}
		} else if (id == 1) {
			this.scroll += 5;
			if (this.scroll > this.news.size()) {
				this.scroll = this.news.size();
			}
		} else if (id == 2) {
			this.launch();
		}
	}

	void update(final long tick) {
		this.tick = tick;
		if (tick > this.sStamp) {
			this.kbs = (int) (this.sBytes.get() / 1024L);
			this.sStamp = tick + 1000L;
			this.sBytes.set(0L);
		}
		boolean keepGoing = true;
		if (this.first) {
			this.buttons[0] = new Button(this.screen, 0, 600, 164, 32, 32, "/\\", false);
			this.buttons[1] = new Button(this.screen, 1, 600, 388, 32, 32, "\\/", false);
			this.buttons[2] = new Button(this.screen, 2, 320, 440, 512, 48, "Launch", false);
			this.curTask = 1;
			this.first = false;
			this.downloadFile("update.dat", true);
		} else if (this.curTask == 1) {
			if (!this.downloading.get()) {
				this.curTask = 2;
				this.readUpdateFile();
			} else if (this.failed.get()) {
				this.downloadFile("update.dat", true);
			} else if (tick - this.dstamp.get() > 1000L) {
				Gdx.net.cancelHttpRequest(this.request);
				this.downloadFile("update.dat", true);
			}
		} else if (this.curTask == 2) {
			if (this.files.isEmpty()) {
				if (this.firstPass) {
					this.curTask = 1;
					this.firstPass = false;
				} else {
					this.curTask = 3;
				}
			} else if (!this.downloading.get()) {
				do {
					this.curUpdFile = this.files.remove();
					final FileHandle fh = Gdx.files.local(this.curUpdFile.name);
					if (fh.exists()) {
						final String md5 = calcMD5(this.curUpdFile.name);
						if (!md5.equals(this.curUpdFile.version)) {
							this.downloadFile(this.curUpdFile.name, true);
						}
					} else {
						this.downloadFile(this.curUpdFile.name, true);
					}
					if (this.downloading.get() || this.getTick() - tick >= 10L) {
						keepGoing = false;
					}
				} while (!this.files.isEmpty() && keepGoing);
				this.files.isEmpty();
			} else if (this.failed.get()) {
				this.downloadFile(this.curName, true);
			} else if (tick - this.dstamp.get() > 1000L) {
				Gdx.net.cancelHttpRequest(this.request);
				this.downloadFile(this.curName, true);
			}
		} else if (this.curTask == 3) {
			downloadFile("release.html", false);
			this.curTask = 4;
		} else if (this.curTask == 4) {
			if (this.downloading.get()) {
				if (this.failed.get()) {
					this.downloadFile("release.html", false);
				} else if (tick - this.dstamp.get() > 1000L) {
					Gdx.net.cancelHttpRequest(this.request);
					this.downloadFile("release.html", false);
				}
			} else {
				this.curTask = 5;
				

				downloadFile("whatsnew.html", false);
			}
		} else if (this.curTask == 5) {
			if (!this.downloading.get()) {
				this.curTask = 6;
				this.readNewsFile();
				this.readReleaseFile();
			} else if (this.failed.get()) {
				this.downloadFile("whatsnew.html", true);
			} else if (tick - this.dstamp.get() > 1000L) {
				Gdx.net.cancelHttpRequest(this.request);
				this.downloadFile("whatsnew.html", true);
			}
		} else if (this.curTask == 6) {
			for (int i = 0; i < 10; ++i) {
				if (this.buttons[i] != null) {
					this.buttons[i].update(tick);
				}
			}
		} else if (this.curTask == 7 && System.currentTimeMillis() > this.stamp) {
			System.exit(0);
		}
	}

	private void launch() {
		try {
			Runtime.getRuntime().exec("jre/bin/java -jar game.jar");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.stamp = this.tick + 500L;
		this.curTask = 7;
	}

	private void readNewsFile() {
		final FileHandle fh = Gdx.files.local("whatsnew.html");
		String text = fh.readString();
		text = text.replace("<div class=\"news-entry\">", "");
		text = text.replace("</div>", "");
		text = text.replace("<div class=\"date\">", "");
		text = text.replace("<hr>", "");
		text = text.replace("<div>", "");
		text = text.replace("<br>", "");
		final String[] lines = text.split("\\r?\\n");
		String[] array;
		for (int length = (array = lines).length, i = 0; i < length; ++i) {
			final String s = array[i];
			if (s.length() == 0) {
				this.news.add(" ");
			} else {
				this.news.addAll(wrapText(1.0f, 524, s));
			}
		}
	}
	
	private void readReleaseFile() {
		news.add(" ");
		news.add(" ");
		news.add("RELEASE NOTES");
		final FileHandle fh = Gdx.files.local("release.html");
		String text = fh.readString();
		text = text.replace("<div class=\"release-note\">", "");
		text = text.replace("<div class=\"version\">", "");
		text = text.replace("</div>", "");
		text = text.replace("<div class=\"date\">", "");
		text = text.replace("<hr>", "");
		text = text.replace("<div>", "");
		text = text.replace("<br>", "");
		final String[] lines = text.split("\\r?\\n");
		String[] array;
		for (int length = (array = lines).length, i = 0; i < length; ++i) {
			final String s = array[i];
			if (s.length() == 0) {
				this.news.add(" ");
			} else {
				this.news.addAll(wrapText(1.0f, 524, s));
			}
		}
	}

	private void readUpdateFile() {
		final FileHandle fh = Gdx.files.local("update.dat");
		final String text = fh.readString();
		final List<String> lines = Arrays.asList(text.split("\\r?\\n"));
		this.totalSize = 0L;
		this.tprog.set(0L);
		long s = 0L;
		for (final String curLine : lines) {
			final String[] words = curLine.split(",");
			if (words.length == 3) {
				s = Long.parseLong(words[2]);
				this.files.add(new UpdFile(words[0], words[1], s));
			}
		}
		String md5 = "";
		File f = null;
		for (final UpdFile u : this.files) {
			f = new File(u.name);
			if (f.exists()) {
				md5 = calcMD5(u.name);
				if (md5.equals(u.version)) {
					continue;
				}
				this.totalSize += u.size;
			} else {
				this.totalSize += u.size;
			}
		}
	}

	void render() {
		final int barX = (int) (480.0f * (this.prog.get() / 100.0f));
		final float curP = this.prog.get() / 100.0f * this.curUpdFile.size;
		int tX = (int) (480.0f * ((this.tprog.get() + curP) / this.totalSize));
		if (tX > 480) {
			tX = 480;
		}
		if (this.curTask < 6) {
			final String[] words = this.curName.split("/");
			this.screen.drawFont(0, 320, 145, "Downloading " + words[words.length - 1], true, 2.0f, Color.WHITE);
			this.screen.drawRegion(AssetLoader.wall[0], 80, 220, false, 0.0f, 1.0f);
			this.screen.batcher.draw(AssetLoader.wallTex, 80.0f, 220.0f, (float) barX, 64.0f, 0, 64, barX, 64, false,
					true);
			this.screen.drawFont(0, 320, 254, String.valueOf((int) curP) + "/" + this.curUpdFile.size + " bytes", true,
					1.0f, Color.WHITE);
			this.screen.drawRegion(AssetLoader.wall[0], 80, 320, false, 0.0f, 1.0f);
			this.screen.batcher.draw(AssetLoader.wallTex, 80.0f, 320.0f, (float) tX, 64.0f, 0, 64, tX, 64, false, true);
			this.screen.drawFont(0, 320, 354, String.valueOf((int) ((this.tprog.get() + curP) / 1000.0f)) + "/"
					+ this.totalSize / 1000L + " KB (" + this.kbs + " KB/sec)", true, 1.0f, Color.WHITE);
		} else {
			for (int i = 0; i < 10; ++i) {
				if (this.buttons[i] != null) {
					this.buttons[i].render();
				}
			}
			this.screen.batcher.draw(AssetLoader.bannerTex, 32.0f, 0.0f, 576.0f, 128.0f, 0, 0, 576, 128, false, true);
			this.screen.drawFrame(32, 148, 544, 256, true);
			int c = 0;
			int d = 0;
			if (this.news != null) {
				for (final String s : this.news) {
					if (++d >= this.scroll && c < 12) {
						++c;
						if (s == null) {
							continue;
						}
						this.screen.drawFont(0, 46, 24 + c * 20 + 114, s, false, 1.0f, Color.WHITE);
					}
				}
			}
		}
	}

	public long getTick() {
		return System.currentTimeMillis();
	}

	private void downloadFile(final String name, boolean master) {
		this.failed.set(false);
		this.downloading.set(true);
		this.curName = name;
		this.curFile = name;
		(this.request = new Net.HttpRequest("GET")).setTimeOut(200);
		String remoteName = "http://" + Prefs.hostName + "/";
		if (master) {
			remoteName += "master/";
		}
		remoteName += name;
		this.request.setUrl(remoteName);
		this.lprog.set(0);
		this.prog.set(0);
		this.dstamp.set(this.tick);
		Gdx.net.sendHttpRequest(this.request, (Net.HttpResponseListener) new Net.HttpResponseListener() {
			public void handleHttpResponse(final Net.HttpResponse httpResponse) {
				final long length = Long.parseLong(httpResponse.getHeader("Content-Length"));
				Patch.this.is = httpResponse.getResultAsStream();
				Patch.this.os = Gdx.files.local(Patch.this.curFile).write(false);
				final byte[] bytes = new byte[1024];
				int count = -1;
				long read = 0L;
				try {
					while ((count = Patch.this.is.read(bytes, 0, bytes.length)) != -1) {
						Patch.this.os.write(bytes, 0, count);
						read += count;
						Patch.this.sBytes.set(Patch.this.sBytes.get() + count);
						final int p = (int) (read / (double) length * 100.0);
						Patch.this.prog.set(p);
						Gdx.app.postRunnable((Runnable) new Runnable() {
							@Override
							public void run() {
								if (Patch.this.prog.get() == 100) {
									if (Patch.this.downloading.get()) {
										Patch.this.downloading.set(false);
										Patch.this.tprog.set(Patch.this.tprog.get() + length);
									}
								} else if (Patch.this.prog.get() != Patch.this.lprog.get()) {
									Patch.this.lprog.set(Patch.this.prog.get());
									Patch.this.dstamp.set(Patch.this.tick);
								}
							}
						});
					}
				} catch (Exception e) {
					Patch.this.failed.set(true);
				}
			}

			public void failed(final Throwable t) {
				Patch.this.failed.set(true);
			}

			public void cancelled() {
				Patch.this.failed.set(true);
			}
		});
	}

	public static byte[] createChecksum(final InputStream fis) {
		final byte[] buffer = new byte[1024];
		try {
			final MessageDigest complete = MessageDigest.getInstance("MD5");
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

	public static String getMD5Checksum(final InputStream fis) {
		final byte[] b = createChecksum(fis);
		String result = "";
		for (int i = 0; i < b.length; ++i) {
			result = String.valueOf(result) + Integer.toString((b[i] & 0xFF) + 256, 16).substring(1);
		}
		return result;
	}

	public static String calcMD5(final String fileName) {
		final InputStream fis = Gdx.files.local(fileName).read();
		boolean success = true;
		String md5 = "";
		do {
			try {
				md5 = getMD5Checksum(fis);
				success = true;
			} catch (Exception e) {
				success = false;
			}
		} while (!success);
		return md5;
	}

	public static List<String> wrapText(final float scale, final int width, final String text) {
		final List<String> lines = new ArrayList<String>();
		String line = "";
		String word = "";
		for (int c = 0; c < text.length(); ++c) {
			final String p = text.substring(c, c + 1);
			if (p.equals(" ")) {
				if (line.length() > 0) {
					if (AssetLoader.getStringWidth(String.valueOf(line) + " " + word, scale, 0.0f, 1.0f) > width) {
						lines.add(line);
						line = word;
						word = "";
					} else {
						line = String.valueOf(line) + " " + word;
						word = "";
					}
				} else if (AssetLoader.getStringWidth(word, scale, 0.0f, 1.0f) > width) {
					line = String.valueOf(word) + " ";
					word = "";
				} else {
					line = word;
					word = "";
				}
			} else if (line.length() == 0
					&& AssetLoader.getStringWidth(String.valueOf(word) + p, scale, 0.0f, 1.0f) > width) {
				lines.add(word);
				word = "";
				word = p;
			} else if (line.length() > 0
					&& AssetLoader.getStringWidth(String.valueOf(line) + " " + word + p, scale, 0.0f, 1.0f) > width) {
				lines.add(line);
				line = "";
				word = String.valueOf(word) + p;
			} else {
				word = String.valueOf(word) + p;
			}
		}
		if (word.length() > 0) {
			line = String.valueOf(line) + " " + word;
		}
		if (line.length() > 0) {
			lines.add(line);
		}
		return lines;
	}
}

package de.d3web.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

public class FileIO {

	/** Copy a file from one filename to another */
	public static void copyFile(String inName, String outName)
			throws FileNotFoundException, IOException {
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(
				inName));
		BufferedOutputStream os = new BufferedOutputStream(
				new FileOutputStream(outName));
		copyFile(is, os, true);
	}

	/**
	 * Copy a file from an opened InputStream to opened OutputStream
	 */
	public static void copyFile(InputStream is, OutputStream os, boolean close)
			throws IOException {
		int b; // the byte read from the file
		while ((b = is.read()) != -1) {
			os.write(b);
		}
		is.close();
		if (close)
			os.close();
	}

	/**
	 * Copy a file from an opened InputStream to opened OutputStream public
	 * static void copyFile(InputStream is, OutputStream os, boolean close)
	 * throws IOException { int b; // the byte read from the file while ((b =
	 * is.read( )) != -1) { os.write(b); } is.close( ); if (close) os.close( ); }
	 * /** Copy a file from an opened Reader to opened Writer
	 */
	public static void copyFile(Reader is, Writer os, boolean close)
			throws IOException {
		int b; // the byte read from the file
		while ((b = is.read()) != -1) {
			os.write(b);
		}
		is.close();
		if (close)
			os.close();
	}

	/** Copy a file from a filename to a PrintWriter. */
	public static void copyFile(String inName, PrintWriter pw, boolean close)
			throws FileNotFoundException, IOException {
		BufferedReader is = new BufferedReader(new FileReader(inName));
		copyFile(is, pw, close);
	}

	/** Open a file and read the first line from it. */
	public static String readLine(String inName) throws FileNotFoundException,
			IOException {
		BufferedReader is = new BufferedReader(new FileReader(inName));
		String line = null;
		line = is.readLine();
		is.close();
		return line;
	}

	/** The size of blocking to use */
	protected static final int BLKSIZ = 8192;

	/**
	 * Copy a data file from one filename to another, alternate method. As the
	 * name suggests, use my own buffer instead of letting the BufferedReader
	 * allocate and use the buffer.
	 */
	public void copyFileBuffered(String inName, String outName)
			throws FileNotFoundException, IOException {
		InputStream is = new FileInputStream(inName);
		OutputStream os = new FileOutputStream(outName);
		int count = 0; // the byte count
		byte b[] = new byte[BLKSIZ]; // the bytes read from the file
		while ((count = is.read(b)) != -1) {
			os.write(b, 0, count);
		}
		is.close();
		os.close();
	}

	/** Read the entire content of an Reader into a String */
	public static String readerToString(Reader is) throws IOException {
		StringBuffer sb = new StringBuffer();
		char[] b = new char[BLKSIZ];
		int n;
		// Read a block. If it gets any chars, append them.
		while ((n = is.read(b)) > 0) {
			sb.append(b, 0, n);
		}
		// Only construct the String object once, here.
		return sb.toString();
	}

	/** Read the content of a Stream into a String */
	public static String inputStreamToString(InputStream is) throws IOException {
		return readerToString(new InputStreamReader(is));
	}
	
	public static void copy(java.io.File destination, java.io.File source) throws RuntimeException {
		if (source.isDirectory()) {
			if (!destination.isDirectory()) {
				throw new RuntimeException("Destination '"+destination.getName()+"' is not directory.");
			}
			copyDirectory(destination,source);
		} else {
			if (destination.isDirectory()) {
				destination=new java.io.File(destination,source.getName());
			}
			copyFile(destination,source);
		}
	}
	
	protected static void copyDirectory(java.io.File destination, java.io.File source) throws RuntimeException {
		java.io.File[] list=source.listFiles();
		for (int i=0;i<list.length;i++) {
			java.io.File dest=new java.io.File(destination,list[i].getName());
			if (list[i].isDirectory()) {
				dest.mkdir();
				copyDirectory(dest,list[i]);
			} else {
				copyFile(dest,list[i]);
			}
		}
	}
	
	protected static void copyFile(java.io.File destination, java.io.File source) throws RuntimeException {
		try {
			java.io.FileInputStream inStream=new java.io.FileInputStream(source);
			java.io.FileOutputStream outStream=new java.io.FileOutputStream(destination);

			int len;
			byte[] buf=new byte[2048];
			 
			while ((len=inStream.read(buf))!=-1) {
				outStream.write(buf,0,len);
			}
		} catch (Exception e) {
			throw new RuntimeException("Can't copy file "+source+" -> "+destination+".",e);
		}
	}
	
	
}

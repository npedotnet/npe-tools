/**
 * Fill.java
 * 
 * Copyright (c) 2015 Kenji Sasaki
 * Released under the MIT license.
 * https://github.com/npedotnet/npe-tools/blob/master/LICENSE
 * 
 * This file is a part of npe-tools.
 * https://github.com/npedotnet/npe-tools
 *
 * For more details, see npe-tools wiki.
 * https://github.com/npedotnet/npe-tools/wiki
 * 
 */

package net.npe.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.npe.tools.option.Option;

public final class Fill extends ConsoleApplication {

	public static void main(String [] args) {
		new Fill().run(args);
	}
	
	public void run(String [] args) {
		
		if(args.length < 2) exitWithUsage();

		Option [] options = readOptions(args, SINGLE_OPTIONS, SET_OPTIONS);
		
		if(options == null) exitWithUsage();
		
		String noNameValue = searchNoOptionValue(options);
		
		if(noNameValue != null) {
			exitWithError(noNameValue);
		}
		
		String input = null;
		String output = null;
		long size = 0;
		int fill = -1;
		for(Option option : options) {
			String name = option.getName();
			if(name.equals(OPTION_INPUT)) {
				input = option.getValue();
			}
			else if(name.equals(OPTION_OUTPUT)) {
				output = option.getValue();
			}
			else if(name.equals(OPTION_SIZE)) {
				size = getSize(option.getValue());
			}
			else if(name.equals(OPTION_CHARACTER)) {
				fill = getFillCharacter(option.getValue());
			}
		}
		
		// check size
		if(size <= 0 || size > MAX_SIZE) {
			exitWithError("size is 1Byte to 1GByte");
		}
		
		// input
		if(input != null) {
			File file = new File(input);
			if(!file.exists()) exitWithError("no input file found.");
			if(size < file.length()) exitWithError("must be file size < fill size.");
		}
		
		// output
		if(output == null) exitWithError("no output file option: -o output");
		
		byte [] buffer = new byte[1024];
		
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			fos = new FileOutputStream(output);
			if(input != null) {
				fis = new FileInputStream(input);
				int read;
				while(0 < (read = fis.read(buffer))) {
					fos.write(buffer, 0, read);
					size -= read;
				}
				fis.close();
				fis = null;
			}
			
			if(0 <= fill) {
				// fill fixed value
				int length = (int)((size < buffer.length) ? size : buffer.length);
				for(int i=0; i<length; i++) buffer[i] = (byte)fill;
				while(0 < size) {
					length = (int)((size < buffer.length) ? size : buffer.length);
					fos.write(buffer, 0, length);
					size -= length;
				}
			}
			else {
				// fill random value
				while(0 < size) {
					int length = (int)((size < buffer.length) ? size : buffer.length);
					for(int i=0; i<length; i++) {
						buffer[i] = (byte)(int)(255*Math.random()+0.5);
					}
					fos.write(buffer, 0, length);
					size -= length;
				}
			}
			fos.close();
			fos = null;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(fos != null) {
				try {
					fos.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(fis != null) {
				try {
					fis.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public String usage() {
		return "-i input -o output -c fill";
	}
	
	private long getSize(String value) {
		String lower = value.toLowerCase();
		if(lower.endsWith("k")) {
			String number = lower.substring(0, lower.length()-1);
			return (int)(1024 * Double.parseDouble(number));
		}
		else if(lower.endsWith("m")) {
			String number = lower.substring(0, lower.length()-1);
			return (int)(1024 * 1024 * Double.parseDouble(number));
		}
		else if(lower.endsWith("g")) {
			String number = lower.substring(0, lower.length()-1);
			return (int)(1024 * 1024 * 1024 * Double.parseDouble(number));
		}
		return Integer.parseInt(value);
	}
	
	private int getFillCharacter(String value) {
		if(value.startsWith("0x")) {
			String hex = value.substring(2, value.length());
			return Integer.parseInt(hex, 16);
		}
		return Integer.parseInt(value);
	}
	
	private static String OPTION_INPUT = "-i";
	private static String OPTION_OUTPUT = "-o";
	private static String OPTION_SIZE = "-s";
	private static String OPTION_CHARACTER = "-c";
	private static String OPTION_PROGRESS = "-p"; // not yet implemented
	
	private static String [] SINGLE_OPTIONS = {
		OPTION_PROGRESS
	};
	
	private static String [] SET_OPTIONS = {
		OPTION_INPUT, OPTION_OUTPUT, OPTION_SIZE, OPTION_CHARACTER
	};
	
	private static int MAX_SIZE = 1024 * 1024 * 1024; // 1GByte

}

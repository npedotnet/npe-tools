/**
 * ResourceConverter.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import net.npe.tools.option.Option;
import net.npe.util.Base64;

public final class ResourceConverter extends ConsoleApplication {
	
	public static void main(String [] args) {
		new ResourceConverter().run(args);
	}
	
	public void run(String [] args) {
		
		if(args.length < 3) exitWithUsage();
		
		Option [] options = readOptions(args, SINGLE_OPTIONS, SET_OPTIONS);

		if(options == null) exitWithUsage();
		
		String input = null;
		String output = null;
		Type type = null;
		boolean nullTerminated = false;

		for(Option option : options) {
			String name = option.getName();
			if(name.equals(OPTION_INPUT)) {
				input = option.getValue();
			}
			else if(name.equals(OPTION_OUTPUT)) {
				output = option.getValue();
			}
			else if(name.equals(OPTION_CARRAY)) {
				if(type != null) {
					exitWithError("Duplicate convert option: "+name+" and "+getOption(type));
				}
				type = Type.CARRAY;
			}
			else if(name.equals(OPTION_CSTRING)) {
				if(type != null) {
					exitWithError("Duplicate convert option: "+name+" and "+getOption(type));
				}
				type = Type.CSTRING;
			}
			else if(name.equals(OPTION_JAVASTRING)) {
				if(type != null) {
					exitWithError("Duplicate convert option: "+name+" and "+getOption(type));
				}
				type = Type.JAVASTRING;
			}
			else if(name.equals(OPTION_BASE64)) {
				if(type != null) {
					exitWithError("Duplicate convert option: "+name+" and "+getOption(type));
				}
				type = Type.BASE64;
			}
			else if(name.equals(OPTION_0) || name.equals(OPTION_NULL_TERMINATED)) {
				nullTerminated = true;
			}
		}
		
		if(input == null) exitWithError("No input option found: -i input_file");
		
		File file = new File(input);
		if(!file.exists()) exitWithError("No input file found.");
		
		String name = input.replace('.', '_');
		
		StringBuffer buffer = new StringBuffer();
		
		if(type == Type.CSTRING) {
			BufferedReader reader = null;
			buffer.append("const char "+name+"[]=\n");
			try {
				reader = new BufferedReader(new FileReader(input));
				String line = reader.readLine();
				while(line != null) {
					line = line.replace("\\", "\\\\");
					line = line.replace("\"", "\\\"");
					buffer.append("\""+line+"\\n\"\n");
					line = reader.readLine();
				}
				reader.close();
				reader = null;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				if(reader != null) {
					try {
						reader.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			buffer.append(";");
		}
		else if(type == Type.JAVASTRING) {
			BufferedReader reader = null;
			buffer.append("String "+name+"=\n");
			try {
				reader = new BufferedReader(new FileReader(input));
				String line = reader.readLine();
				while(line != null) {
					line = line.replace("\\", "\\\\");
					line = line.replace("\"", "\\\"");
					buffer.append("\""+line+"\\n\"");
					line = reader.readLine();
					if(line != null) buffer.append("+");
					buffer.append("\n");
				}
				reader.close();
				reader = null;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				if(reader != null) {
					try {
						reader.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			buffer.append(";");
		}
		else if(type == Type.CARRAY) {
			FileInputStream fis = null;
			buffer.append("const unsigned char "+name+"[]={");
			try {
				fis = new FileInputStream(input);
				int ch = fis.read();
				while(ch != -1) {
					buffer.append(Integer.toString(ch));
					if((ch = fis.read()) != -1) buffer.append(',');
				}
				fis.close();
				fis = null;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				if(fis != null) {
					try {
						fis.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			if(nullTerminated) buffer.append(",0");
			buffer.append("};");
		}
		else { // Base64
			FileInputStream fis = null;
			byte [] data = null;
			try {
				fis = new FileInputStream(file);
				data = new byte[fis.available()];
				fis.read(data);
				fis.close();
				fis = null;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				if(fis != null) {
					try {
						fis.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			byte [] base64 = Base64.encode(data);
			buffer.append(new String(base64));
		}
			
		if(output == null) {
			System.out.print(buffer.toString());
		}
		else {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(output);
				fos.write(buffer.toString().getBytes());
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
			}
		}
		
	}
	
	public String usage() {
		return "-i input_file [-o output_file] -car/-cstr/-jstr/-b64 [-0/-nt]";
	}
	
	enum Type {
		CARRAY,
		CSTRING,
		JAVASTRING,
		BASE64,
	}
	
	private String getOption(Type type) {
		return SINGLE_OPTIONS[type.ordinal()];
	}
	
	private static final String OPTION_CARRAY = "-car";
	private static final String OPTION_CSTRING = "-cstr";
	private static final String OPTION_JAVASTRING = "-jstr";
	private static final String OPTION_BASE64 = "-b64";
	private static final String OPTION_0 = "-0";
	private static final String OPTION_NULL_TERMINATED = "-nt";
	private static final String OPTION_INPUT = "-i";
	private static final String OPTION_OUTPUT = "-o";
	
	private static String [] SINGLE_OPTIONS = {
		OPTION_CARRAY, OPTION_CSTRING, OPTION_JAVASTRING, OPTION_BASE64,
		OPTION_0, OPTION_NULL_TERMINATED
	};
	
	private static String [] SET_OPTIONS = {
		OPTION_INPUT, OPTION_OUTPUT
	};

}

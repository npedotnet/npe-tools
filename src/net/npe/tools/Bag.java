/**
 * Bag.java
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
import java.util.Vector;

import net.npe.baggage.BaggageWriter;
import net.npe.tools.option.Option;

public class Bag extends ConsoleApplication {
	
	public static void main(String [] args) {
		new Bag().run(args);
	}

	@Override
	public void run(String [] args) {
		
		if(args.length < 2) exitWithUsage();
		
		Option [] options = readOptions(args, null, SET_OPTIONS);
		
		if(options == null || options.length < 2) exitWithUsage();
		
		if(options[0].getName() != null) exitWithUsage();
		
		String output = options[0].getValue();
		
		files = new Vector<String>();
		
		for(int i=1; i<options.length; i++) {
			String name = options[i].getName();
			if(name == null) {
				addFile(options[i].getValue());
			}
			else if(name.equals(OPTION_RECURSIVE)) {
				addDirectory(options[i].getValue());
			}
			else {
				exitWithError("No such option: "+name);
			}
		}
		
		if(files.size() <= 0) exitWithError("No input files.");
		
		for(String file : files) {
			System.out.println(file);
		}

		BaggageWriter.write(output, files.toArray(new String[0]));
		
	}
	
	@Override
	public String usage() {
		return "bag output.bag inputfile [-r inputdir]";
	}
	
	private Vector<String> addDirectory(String directory) {
		File root = new File(directory);
		// exist?
		if(!root.exists()) exitWithError(directory+" is not found.");
		
		String [] list = new File(directory).list();
		for(String name : list) {
			String path = directory + "/" + name;
			File file = new File(path);
			if(file.isDirectory()) {
				addDirectory(path);
			}
			else {
				addFile(path);
			}
		}
		return null;
	}
	
	private void addFile(String path) {
		File file = new File(path);
		// exist?
		if(!file.exists()) exitWithError(path+" is not found.");
		// duplicate?
		for(String a : files) {
			if(a.equals(path)) exitWithError("Duplicate file: "+path);
		}
		files.add(path);
	}
	
	private Vector<String> files;
	
	private static final String OPTION_RECURSIVE = "-r";
	
	private static final String [] SET_OPTIONS = {
		OPTION_RECURSIVE,
	};


}

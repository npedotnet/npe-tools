/**
 * Unbag.java
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
import java.io.FileOutputStream;

import net.npe.baggage.Baggage;
import net.npe.baggage.BaggageItem;
import net.npe.baggage.BaggageReader;
import net.npe.tools.option.Option;

public class Unbag extends ConsoleApplication {
	
	public static void main(String [] args) {
		new Unbag().run(args);
	}
	
	@Override
	public void run(String [] args) {
		
		if(args.length < 1) exitWithUsage();
		
		Option [] options = readOptions(args, SINGLE_OPTIONS, null);
		
		if(options == null) exitWithUsage();

		String input = null;
		boolean showList = false;
		
		for(Option option : options) {
			String name = option.getName();
			if(name == null) {
				if(input == null) {
					input = option.getValue();
				}
				else exitWithUsage();
			}
			else if(name.equals(OPTION_LIST)) {
				showList = true;
			}
			else {
				exitWithUsage();
			}
		}
		
		Baggage baggage = BaggageReader.read(input);
		
		String [] keys = baggage.getKeys();
		
		if(showList) {
			for(String key : keys) {
				BaggageItem item = baggage.get(key);
				System.out.println(key+" "+item.getLength()+" bytes.");
			}
		}
		else for(String key : keys) {
			String parent = new File(key).getParent();
			
			// mkdir
			if(parent != null) {
				File file = new File(parent);
				if(!file.exists() && !file.mkdirs()) {
					exitWithError("CRITICAL: Not create "+parent+" directory!");
				}
			}
			
			BaggageItem item = baggage.get(key);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(key);
				fos.write(item.getData(), item.getOffset(), item.getLength());
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
	
	@Override
	public String usage() {
		return "unbag input.bag [-l]";
	}
	
	private static final String OPTION_LIST = "-l";
	
	private static final String [] SINGLE_OPTIONS = {
		OPTION_LIST,
	};

}

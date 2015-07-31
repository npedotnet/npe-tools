/**
 * OptionReader.java
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

package net.npe.tools.option;

import java.util.Vector;

public final class OptionReader {
	
	public static Option [] read(String [] args, String [] singleOptions, String [] setOptions) throws NoOptionValueException, DuplicateOptionException {
		
		Vector<Option> options = new Vector<Option>();
		
		for(int i=0; i<args.length; i++) {
			String arg = args[i];
			Option option = null;
			
			// single options
			if(singleOptions != null) {
				for(String single : singleOptions) {
					if(arg.equals(single)) {
						if(checkDuplicateOptions(options, arg)) {
							throw new DuplicateOptionException(arg);
						}
						option = new Option(arg, null);
						break;
					}
				}
			}
			
			// set options
			if(setOptions != null && option == null) {
				for(String set : setOptions) {
					if(arg.equals(set)) {
						if(i < args.length-1) {
							if(checkDuplicateOptions(options, arg)) {
								throw new DuplicateOptionException(arg);
							}
							option = new Option(arg, args[++i]);
							break;
						}
						else {
							throw new NoOptionValueException(arg);
						}
					}
				}
			}
			
			// no option value
			if(option == null) option = new Option(null, arg);
			
			options.add(option);
			
		}
		
		return options.toArray(new Option[0]);
	}
	
	private static boolean checkDuplicateOptions(Vector<Option> options, String name) {
		for(Option option : options) {
			if(name.equals(option.getName())) return true;
		}
		return false;
	}
	
	private OptionReader(String [] args) {}

}

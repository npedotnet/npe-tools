/**
 * ConsoleApplication.java
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

import net.npe.tools.option.DuplicateOptionException;
import net.npe.tools.option.NoOptionValueException;
import net.npe.tools.option.Option;
import net.npe.tools.option.OptionReader;

public abstract class ConsoleApplication {
	
	public abstract void run(String [] args);
	
	public abstract String usage();
	
	protected final void exitWithError(String error) {
		System.err.println(error);
		System.exit(-1);
	}
	
	protected final void exitWithMessage(String message) {
		System.out.println(message);
		System.exit(0);
	}
	
	protected final void exitWithUsage() {
		exitWithMessage(usage());
	}

	protected final Option [] readOptions(String [] args, String [] singleOptions, String [] setOptions) {
		Option [] options = null;
		try {
			options = OptionReader.read(args, singleOptions, setOptions);
		}
		catch(NoOptionValueException e) {
			exitWithError("No value for: "+e.getMessage());
		}
		catch(DuplicateOptionException e) {
			exitWithError("Duplicate option: "+e.getMessage());
		}
		return options;
	}
	
	protected final String searchNoOptionValue(Option [] options) {
		for(Option option : options) {
			if(option.getName() == null) return option.getValue();
		}
		return null;
	}
	
}

/**
 * DuplicateOptionException.java
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

public final class DuplicateOptionException extends Exception {

	public DuplicateOptionException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
	
}

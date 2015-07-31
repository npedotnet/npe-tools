/**
 * Base64.java
 * 
 * Copyright (c) 2015 Kenji Sasaki
 * Released under the MIT license.
 * https://github.com/npedotnet/npe-library/blob/master/LICENSE
 * 
 * This file is a part of npe-library.
 * https://github.com/npedotnet/npe-library
 *
 */

package net.npe.util;

public class Base64 {
	
	public static byte [] encode(byte [] data) {
		return encode(data, ENCODE);
	}
	
	public static byte [] encodeURL(byte [] data) {
		return encode(data, ENCODE_URL);
	}
	
	public static byte [] encode(byte [] data, byte [] table) {
		
		int encodeLength = 4 * (data.length / 3) + ENCODE_MOD[data.length % 3];
		int bufferLength = 4 * ((encodeLength + 3) / 4);
		
		byte [] buffer = new byte[bufferLength];
		
		int index=0;
		int dataIndex = 0;
		
		for(int i=0; i<data.length/3; i++) {
			buffer[index++] = table[(data[dataIndex] >> 2) & 0x3F];
			buffer[index++] = table[((data[dataIndex++] << 4) & 0x30) | ((data[dataIndex] >> 4) & 0x0F)];
			buffer[index++] = table[((data[dataIndex++] << 2) & 0x3C) | ((data[dataIndex] >> 6) & 0x03)];
			buffer[index++] = table[data[dataIndex++] & 0x3F];
		}
		
		int mod = data.length % 3;
		if(mod == 1) {
			buffer[index++] = table[(data[dataIndex] >> 2) & 0x3F];
			buffer[index++] = table[((data[dataIndex] << 4) & 0x30)];
		}
		else if(mod == 2) {
			buffer[index++] = table[(data[dataIndex] >> 2) & 0x3F];
			buffer[index++] = table[((data[dataIndex] << 4) & 0x30) | ((data[dataIndex++] >> 4) & 0x0F)];
			buffer[index++] = table[((data[dataIndex] << 2) & 0x3C)];
		}
		
		for(; index<bufferLength; index++) buffer[index] = '=';
		
		return buffer;
		
	}
	
	public static byte [] decode(byte [] data) {
		
		int dataLength = data.length;
		int decodeLength = 3 * data.length / 4;
		int padCount = 0;
		
		if(data[data.length-1] == '=') {
			padCount++;
			if(data[data.length-2] == '=') padCount++;
		}
		
		byte [] buffer = new byte[decodeLength - padCount];
		int index = 0;
		int dataIndex = 0;
		
		dataLength -= padCount;
		for(int i=0; i<dataLength/4; i++) {
			int b0 = DECODE[data[dataIndex++]];
			int b1 = DECODE[data[dataIndex++]];
			int b2 = DECODE[data[dataIndex++]];
			int b3 = DECODE[data[dataIndex++]];
			buffer[index++] = (byte)((b0 << 2) | ((b1 >> 4) & 0x03));
			buffer[index++] = (byte)((b1 << 4) | ((b2 >> 2) & 0x0F));
			buffer[index++] = (byte)((b2 << 6) | b3);
		}
		
		if(padCount == 1) {
			int b0 = DECODE[data[dataIndex++]];
			int b1 = DECODE[data[dataIndex++]];
			int b2 = DECODE[data[dataIndex++]];
			buffer[index++] = (byte)((b0 << 2) | ((b1 >> 4) & 0x03));
			buffer[index++] = (byte)((b1 << 4) | ((b2 >> 2) & 0x0F));
		}
		else if(padCount == 2) {
			int b0 = DECODE[data[dataIndex++]];
			int b1 = DECODE[data[dataIndex++]];
			buffer[index++] = (byte)((b0 << 2) | ((b1 >> 4) & 0x03));
		}
		
		return buffer;

	}
	
	private static final byte [] ENCODE_MOD = {0, 2, 3};
	
	private static final byte [] ENCODE = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
		'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		'w', 'x', 'y', 'z', '0', '1', '2', '3',
		'4', '5', '6', '7', '8', '9', '+', '/'
	};
	
	private static final byte [] ENCODE_URL = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
		'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		'w', 'x', 'y', 'z', '0', '1', '2', '3',
		'4', '5', '6', '7', '8', '9', '-', '_'
	};
	
	private static final byte [] DECODE = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63,
		52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
		-1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63,
		-1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
		41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
	};

}

/**
 * PixelImageConverter.java
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.npe.image.PixelFormat;
import net.npe.image.PixelImage;
//import net.npe.image.pig.PixelImageWriter;
import net.npe.image.util.ImageReader;
import net.npe.image.util.ImageType;
import net.npe.io.StreamWriter;

public final class PixelImageConverter {}/*extends ConsoleApplication {

	public static void main(String [] args) {
		new PixelImageConverter().run(args);
	}
	
	@Override
	public void run(String [] args) {
		
		String input = args[0];
		
		PixelImage pig = null;
		
		if(checkExtensions(input, IMAGEIO_EXTENSIONS) != null) {
			try {
				BufferedImage image = ImageIO.read(new File(input));
				int width = image.getWidth();
				int height = image.getHeight();
				int [] pixels = new int[width*height];
				image.getRGB(0, 0, width, height, pixels, 0, width);
				pig = new PixelImage(pixels, width, height, PixelFormat.ARGB);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		else if(checkExtensions(input, NPEIM_INPUT_EXTENSIONS) != null) {
			ImageType type = ImageReader.getImageType(input);
			if(type != null) {
				try {
					FileInputStream fis = new FileInputStream(input);
					pig = ImageReader.read(type, PixelFormat.ARGB, fis);
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		String output = args[1];
		
		try {
			FileOutputStream fos = new FileOutputStream(output);
			StreamWriter writer = new StreamWriter(fos, StreamWriter.LittleEndian);
			PixelImageWriter.write(pig, writer);
			fos.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	private String checkExtensions(String value, String [] extensions) {
		String lower = value.toLowerCase();
		for(String extension : extensions) {
			if(lower.endsWith(extension)) return extension;
		}
		return null;
	}
	
	private static String [] IMAGEIO_EXTENSIONS = {
		".bmp", ".jpeg", ".jpg", ".png", ".wbmp", ".gif",
	};

	private static String [] NPEIM_INPUT_EXTENSIONS = {
		".dds", ".pig", ".psd", ".tga",
	};
	
}*/

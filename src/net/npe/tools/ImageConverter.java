/**
 * ImageConverter.java
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

import javax.imageio.ImageIO;

import net.npe.image.PixelFormat;
import net.npe.image.PixelImage;
import net.npe.image.util.ImageReader;
import net.npe.image.util.ImageType;
import net.npe.image.util.ImageWriter;
import net.npe.util.Base64;

public final class ImageConverter extends ConsoleApplication {

	public static void main(String [] args) {
		new ImageConverter().run(args);
	}

	public void run(String [] args) {
		
		if(args.length != 2) exitWithUsage();
		
		String input = args[0];
		String output = args[1];
		
		if(input.equals(output)) {
			exitWithError("input == output");
		}
		
		String inputExtension = checkInputExtensions(input);
		if(inputExtension == null) {
			exitWithError("No support input format: "+input);
		}
		
		String outputExtension = checkOutputExtensions(output);
		if(outputExtension == null) {
			exitWithError("No support output format: "+output);
		}
		
		File file = new File(input);
		
		if(!file.exists()) {
			exitWithError("No input file found: "+input);
		}
		
		if(checkExtensions(outputExtension, IMAGEIO_EXTENSIONS) != null) {
			if(checkExtensions(inputExtension, IMAGEIO_EXTENSIONS) != null) {
				try {
					BufferedImage image = ImageIO.read(new File(input));
					ImageIO.write(image, outputExtension.substring(1), new File(output));
					System.out.println(outputExtension.substring(1));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			else if(checkExtensions(inputExtension, NPEIM_INPUT_EXTENSIONS) != null) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(input);
					byte [] buffer = new byte[fis.available()];
					fis.read(buffer);
					fis.close();
					fis = null;
					ImageType type = ImageReader.getImageType(inputExtension);
					PixelImage pig = ImageReader.read(type, PixelFormat.ARGB, buffer, 0);
					int width = pig.getWidth();
					int height = pig.getHeight();
					int [] pixels = pig.getPixels();
					BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					image.setRGB(0, 0, width, height, pixels, 0, width);
					ImageIO.write(image, outputExtension.substring(1), new File(output));
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
			}
		}
		else if(checkExtensions(outputExtension, NPEIM_OUTPUT_EXTENSIONS) != null) {
			if(checkExtensions(inputExtension, IMAGEIO_EXTENSIONS) != null) {
				FileOutputStream fos = null;
				try {
					BufferedImage image = ImageIO.read(new File(input));
					int width = image.getWidth();
					int height = image.getHeight();
					int [] pixels = new int[width*height];
					image.getRGB(0, 0, width, height, pixels, 0, width);
					PixelImage pig = new PixelImage(pixels, width, height, PixelFormat.ARGB);
					ImageType type = ImageReader.getImageType(outputExtension);
					byte [] buffer = ImageWriter.write(type, pig);
					fos = new FileOutputStream(output);
					fos.write(buffer);
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
			else if(checkExtensions(inputExtension, NPEIM_INPUT_EXTENSIONS) != null) {
				FileInputStream fis = null;
				FileOutputStream fos = null;
				try {
					fis = new FileInputStream(input);
					byte [] buffer = new byte[fis.available()];
					fis.read(buffer);
					fis.close();
					fis = null;
					ImageType type = ImageReader.getImageType(inputExtension);
					PixelImage pig = ImageReader.read(type, PixelFormat.ARGB, buffer, 0);
					ImageType outputType = ImageReader.getImageType(outputExtension);
					byte [] writeBuffer = ImageWriter.write(outputType, pig);
					fos = new FileOutputStream(output);
					fos.write(writeBuffer);
					fos.close();
					fos = null;
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
		else {
			// Base64
			FileInputStream fis = null;
			byte [] base64 = null;
			try {
				fis = new FileInputStream(file);
				byte [] data = new byte[fis.available()];
				fis.read(data);
				fis.close();
				fis = null;
				base64 = Base64.encode(data);
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
			
			if(base64 != null) {
				String header = "data:image/"+inputExtension.substring(1)+";base64,";
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(output);
					if(outputExtension.equals(".b64")) {
						fos.write(header.getBytes());
						fos.write(base64);
					}
					else { // .html or .htm
						fos.write("<img src=\"".getBytes());
						fos.write(header.getBytes());
						fos.write(base64);
						fos.write("\">".getBytes());
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
				}
			}
		}
		
	}
	
	public String usage() {
		return "input_file output_file";
	}
	
	private String checkInputExtensions(String value) {
		String extension = checkExtensions(value, IMAGEIO_EXTENSIONS);
		if(extension != null) return extension;
		return checkExtensions(value, NPEIM_INPUT_EXTENSIONS);
	}
	
	private String checkOutputExtensions(String value) {
		String extension = checkExtensions(value, IMAGEIO_EXTENSIONS);
		if(extension != null) return extension;
		extension = checkExtensions(value, NPEIM_OUTPUT_EXTENSIONS);
		if(extension != null) return extension;
		return checkExtensions(value, BASE64_OUTPUT_EXTENSIONS);
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
		".dds", ".psd", ".tga",
	};
	
	private static String [] NPEIM_OUTPUT_EXTENSIONS = {
		".tga",
	};
	
	private static String [] BASE64_OUTPUT_EXTENSIONS = {
		".b64", ".htm", ".html"
	};
	
}

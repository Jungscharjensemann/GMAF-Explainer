package de.swa.fuh.clarifai;

import java.io.FileFilter;
import java.io.File;

public class ImageFilter implements FileFilter {
	public boolean accept(File pathname) {
		if(pathname.getPath().toLowerCase().endsWith(".png")) return true;
		if(pathname.getPath().toLowerCase().endsWith(".jpg")) return true;
		if(pathname.getPath().toLowerCase().endsWith(".tiff")) return true;
		if(pathname.getPath().toLowerCase().endsWith(".bmp")) return true;
		if(pathname.getPath().toLowerCase().endsWith(".webp")) return true;
		return false;
	}
}

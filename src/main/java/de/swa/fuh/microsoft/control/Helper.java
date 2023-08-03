package de.swa.fuh.microsoft.control;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import de.swa.fuh.microsoft.model.Constants;
import de.swa.fuh.microsoft.model.ImageDetectionLogger;

/**
 * main class to serve some reusable methods
 */
public class Helper
{
    /**
     * method to check if file by given path to file exists
     * @param _pathToFile is the filepath, which should be checked
     * @return boolean return statement; true, if file exists, false if not
     */
    public static boolean CheckIfFileExists(String _pathToFile)
    {
        if (_pathToFile == null) {
            return false;
        }
        
        try {
            File   rawImage       = new File(_pathToFile);
            if(rawImage.isFile()){
                return true;
            }
        }
        catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * method to check if file by given path to file exists
     * @param _pathToFile is the filepath, which should be checked
     * @return boolean return statement; true, if file exists, false if not
     */
    public static boolean CheckIfDirectoryExists(String _pathToFile)
    {
        if (_pathToFile == null) {
            return false;
        }

        try {
            File   path       = new File(_pathToFile);
            if(path.isDirectory()){
                return true;
            }
        }
        catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * method to write outputstream to file
     * @param _outputStream stream to write
     * @param _path path, in which the file with text should lay
     * @param _fileName filename, in which text should be written
     * @return true if write action was successful, false if not
     */
    public static boolean WriteStringToFile(String _outputStream, String _path, String _fileName)
    {
        try {
            File output;
    
            if (_path == null || _path.isEmpty()) { output = new File(_fileName); }
            else { output = new File(_path + "/" + _fileName); }
            
            //create parent directories and empty file of main file
            output.getParentFile().mkdirs();
            output.createNewFile();
            
            FileWriter writer = new FileWriter(output);
            writer.write(_outputStream);
            writer.flush();
            writer.close();
        }
        catch (Exception ex) {
            System.out.println("Error while print out String to file");
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
        }
        
        return false;
    }

    /**
     * method to collect all Files in directory
     * @param _directory path in which files stored
     * @return List of File objects, found in directory; null if no files exists in path
     */
    public static List<File> GetFilePathListByDirectory(String _directory){
        if(_directory == null || _directory.isEmpty()){
            return null;
        }
        
        Path path = Paths.get(_directory);
        
        if(!Files.exists(path)){
            return null;
        }
        
        File folder = new File(_directory);
        
        return Arrays.asList(Objects.requireNonNull(folder.listFiles()));
    }

    /**
     * method to create directory, if the directory is not yet created
     * @param _directory path of directory, which should be created
     * @return true, if directory exusts or was created, false if not
     */
    public static boolean CreateDirIfNotExists(String _directory){
        if(_directory == null || _directory.isEmpty()){
            return false;
        }
        
        File dir = new File(_directory);
        
        if(!dir.exists()){
            dir.mkdir();
            
            if(!dir.exists()){
                return false;
            }
        }
        return true;
    }

    /**
     * method to return image size of given file
     * @param _file object to get size of
     * @return long value of image size, zero if file not found
     */
    public static long GetImageSize(File _file){
        if(_file == null){
            return 0;
        }
        
        return _file.length();
    }

    /**
     * feedback method, if size is in given range
     * @param _file file to check for size
     * @return boolean, if file size in range
     */
    public static boolean IsImageSizeOk(File _file){
        long imgSize = Helper.GetImageSize(_file);
        
        if(imgSize > Constants.AZURE_MAX_IMG_SIZE){
            return false;
        }
        
        return true;
    }

    /**
     * method to compress images, focused on jpg images
     * @param _file file to compress
     */
    public static void CompressImage(File _file){
        if(_file == null || !_file.exists()){
            ImageDetectionLogger.LogError("Datei nicht vorhanden.");
            return;
        }
    
        try {
            BufferedImage image = ImageIO.read(_file);

            String newFileName = _file.getAbsolutePath();
            File compressedImageFile = new File(newFileName);
            OutputStream os = new FileOutputStream(compressedImageFile);

            Iterator<ImageWriter>writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = (ImageWriter) writers.next();

            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            // Check if canWriteCompressed is true
            if(param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.9f);
            }
            // End of check
            writer.write(null, new IIOImage(image, null, null), param);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * method to extract filename from given string directory
     * @param _path given path, from which filename should be extracted
     * @return filename as string
     */
    public static String GetFileNameFromPath(String _path){
        if(_path == null || _path.isEmpty()){
            return null;
        }

        File f = new File(_path);

        if(f != null && f.exists() && f.isFile()){
            return f.getName();
        }
        return null;
    }

    /**
     * String to date parser method, to get date object by given string
     * @param _dateString string to analyze
     * @return date object or null if string could not be parsed
     */
    public static Date GetDateFromString(String _dateString) {
        if (_dateString == null || _dateString.isEmpty()) {
            return new Date(System.currentTimeMillis());
        }

        List<String> formatStrings = Arrays.asList(
                "M/y",
                "M/d/y",
                "M-d-y",
                "MM/dd/yyyy",
                "dd-M-yyyy hh:mm:ss",
                "dd MMMM yyyy",
                "dd MMMM yyyy zzzz",
                "E, dd MMM yyyy HH:mm:ss z",
                "EEE MMM dd HH:mm:ss z yyyy");

        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString).parse(_dateString);
            } catch (ParseException e) {
            }
        }

        return null;
    }
}

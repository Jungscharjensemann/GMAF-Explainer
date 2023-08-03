package de.swa.fuh.microsoft.model;

/**
 * class, which contains constants of whole project
 */
public class Constants
{
    /**
     * microsoft azure subscription key to start image analysis
     */
    public static String SUBSCRIPTIONKEY = "e69eb7776eeb45ab89c07115d4f1bcbe";
    
    /**
     * microsoft azure subscription key to start image analysis
     */
    public static String ENDPOINT = "https://imagedetectionfuhagen.cognitiveservices.azure.com/";
    
    /**
     * microsoft azure maximum image size
     */
    public static long AZURE_MAX_IMG_SIZE = 3999999L;
    
    /**
     * Logger Constants
     */
    public static String LOG_ARGUMENTS = "1. Argument: Quellpfad\n" +
                                         "2. Argument: Zielpfad\n" +
                                         "3. Argument: Pfad zu CSV Dateien\n";
}

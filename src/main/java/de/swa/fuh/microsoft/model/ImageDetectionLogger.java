package de.swa.fuh.microsoft.model;


/**
 * Logger class for imageDetectionAzure
 */
public class ImageDetectionLogger
{
    //main instance of Log4J Logger, configured in log4j2.xml file
//    private static final Logger logger = LogManager.getLogger("imageDetectionAzure");
    
    /**
     *  RefinementController logger method to write Info into console and Logger
     * @param _message message to log
     */
    public static void LogInfo(String _message){
        System.out.println(_message);
//        logger.info(_message);
    }
    
    /**
     * RefinementController logger method to log error into logger path
     * @param _message message to log
     */
    public static void LogError(String _message){
        System.out.println("Fehler in der Applikation. Bitte prüfen Sie das Log.");
//        logger.error(_message);
    }
    
    /**
     *  logger method to log error plus exception into logger path
     * @param _message message to log
     * @param ex Exception object with information of Errors
     */
    public static void LogError(String _message, Exception ex){
    	ex.printStackTrace();
//        logger.error(_message);
//        
//        if(ex != null){
//            logger.error(ex.getMessage());
//            logger.error(ex.getStackTrace());
//        }
    }
}

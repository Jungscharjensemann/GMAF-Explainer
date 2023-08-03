package de.swa.fuh.microsoft;

import java.util.List;

import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;

import de.swa.fuh.microsoft.control.Helper;
import de.swa.fuh.microsoft.control.ImageAnalyzer;
import de.swa.fuh.microsoft.model.Constants;
import de.swa.fuh.microsoft.model.ImageDetectionLogger;
import de.swa.fuh.microsoft.model.GMAF.GMAFXml;

/**
 * main and start class for application
 */
public class ComputerVision
{
    public ComputerVision(){
    
    }
    //main method of application
    public static void main(String[] args) {
    
        /**
         * evaluating arguments from console start
         * arguments:
         * 1) source path
         * 2) destination path
         * 3) with evaluation (TRUE/FALSE)
         */

        //microsoft azure subscription key to start image analysis
        String azureComputerVisionKey = null;

        //microsoft azure subscription key to start image analysis
        String azureComputerVisionEndpoint = null;

        //source path of images or xml-files
        String sourcePath = null;
        
        //destination path for XML files or image evaluation txt
        String destinationPath = null;
        
        //type of application
        String objectEvaluationPath = null;
        
        try {
            if(args == null || args.length == 0){
                ImageDetectionLogger.LogError("Keine Argumente gefunden. Rufen Sie die Applikation mit Argumenten auf.\n" + Constants.LOG_ARGUMENTS);
                return;
            }
            
            if(args.length < 2){
                ImageDetectionLogger.LogError("Zu wenige Argumente gefunden. Rufen Sie die Applikation mit allen benötigten Argumenten auf.\n" + Constants.LOG_ARGUMENTS);
                return;
            }
            
            //count of arguments correct
            sourcePath = args[0];
            destinationPath = args[1];

            if(args.length >= 3 && args[2] != null){
                objectEvaluationPath = args[2];
            }

            if(args.length >= 4 && args[3] != null){
                azureComputerVisionKey = args[3];
            }
            if(args.length >= 5 && args[4] != null) {
                azureComputerVisionEndpoint = args[4];
            }

            //check, if sourcePath exists
            if(!Helper.CheckIfDirectoryExists(sourcePath)){
                ImageDetectionLogger.LogError("Quellpfad konnte nicht gefunden werden. Bitte pruefen Sie die Pfadangabe oder die Berechtigungen auf die Pfade!");
                return;
            }

            //check, if destination path exists
            if(!Helper.CheckIfDirectoryExists(destinationPath)){
                boolean destinationPathCreated = Helper.CreateDirIfNotExists(destinationPath);
                
                if(!destinationPathCreated){
                    ImageDetectionLogger.LogError("Zielpfad konnte nicht erstellt werden. Bitte pruefen Sie die Pfadangabe oder die Berechtigungen auf die Pfade!");
                    return;
                }
            }
            
            //1) Image detection working
            List<GMAFXml> gmafXmlList = ImageAnalyzer.AnalyzeLocalImageWithObjects(sourcePath, destinationPath, azureComputerVisionKey, azureComputerVisionEndpoint);

            //2) object evaluation working
//            if(Helper.CreateDirIfNotExists(objectEvaluationPath)){
//                GmafXmlEvaluation.EvaluateGmafXmlList(gmafXmlList, objectEvaluationPath);
//            }
            
        }catch(Exception ex){
            ImageDetectionLogger.LogError("Fehler beim Aufruf der Hauptmethode.", ex);
        }
        
        //ImageAnalyzer.AnalyzeLocalImagWithObjects(new File(pathToLocalImage));
    }

    //Authentication of ComputerVisionClient
    public static ComputerVisionClient Authenticate(String subscriptionKey, String endpoint)
    {
        return ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
    }
}
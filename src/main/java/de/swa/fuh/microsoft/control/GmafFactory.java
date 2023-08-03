package de.swa.fuh.microsoft.control;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.azure.cognitiveservices.vision.computervision.models.DetectedObject;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;

import de.swa.fuh.microsoft.model.ImageDetectionLogger;
import de.swa.fuh.microsoft.model.GMAF.GMAFObject;
import de.swa.fuh.microsoft.model.GMAF.GMAFXml;

/**
 * GMAF Factory class, to create GMAF object objects, create GMAF objects and GMAF XML files vise versa
 */
public class GmafFactory
{
    /**
     * main method to map GMAF objects from Azure Analysis
     * @param _analysis object, wich is the base for azure Analysis
     * @return List of GMAF objects, wich will be created by Analysis object
     */
    public static List<GMAFObject> GetGMAFObjectFromAzureAnalyzer(ImageAnalysis _analysis)
    {
        if (_analysis == null || _analysis.objects() == null) {
            ImageDetectionLogger.LogError("Azure Client nicht sauber initialisiert. Verarbeitung wird unterbrochen.");
            return null;
        }
        
        List<GMAFObject> gmafObjectList = new ArrayList<>();
        
        for (DetectedObject object : _analysis.objects()) {
            gmafObjectList.add(new GMAFObject(
                    object.objectProperty(),
                    object.rectangle().x(),
                    object.rectangle().y(),
                    object.rectangle().w(),
                    object.rectangle().h(),
                    object.confidence()
            ));
        }
        
        return gmafObjectList;
    }

    /**
     * method to create GMAF XML object by filename and list of GMAF objects
     * @param _fileName file name of GMAF XML
     * @param _gmafObjects GMAF objects
     * @return null, if no file path found or GMAF Objects are empty
     */
    public static GMAFXml GetGmafXMLObject(String _fileName, List<GMAFObject> _gmafObjects){
        if(_fileName == null || _fileName.isEmpty()){
            ImageDetectionLogger.LogError("Kein Dateiname gefunden, Datei wird übersprungen");
            return null;
        }

        return new GMAFXml(_fileName, _gmafObjects);
    }
}

package de.swa.fuh.microsoft.control;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.Category;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.CelebritiesModel;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.DetectedBrand;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.DetectedObject;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.FaceDescription;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageCaption;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageTag;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.LandmarksModel;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.VisualFeatureTypes;

import de.swa.fuh.microsoft.model.Constants;
import de.swa.fuh.microsoft.model.ImageDetectionLogger;
import de.swa.fuh.microsoft.model.GMAF.GMAFObject;
import de.swa.fuh.microsoft.model.GMAF.GMAFXml;

/**
 * object, to analyse object with ms azure library
 */
public class ImageAnalyzer
{
    //list of GMAF XML
    private static List<GMAFXml> gmafXmlList;

    /**
     * authentication method, to authenticate agains ms computer Vision Webservice
     * @param subscriptionKey key from ms account
     * @param endpoint endpoint from ms account
     * @return client, to work against computer vision services
     */
    public static ComputerVisionClient Authenticate(String subscriptionKey, String endpoint)
    {
        return ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
    }


    /**
     * method, to analyze list o files in directory and put GMAF XML files to given directory
     * @param _pathToImageDir directory with images included
     * @param _outputPath directory to put GMAF XML files in
     * @param _azureSubsKey Azure Subsciption Key
     * @param _azureSubsEndpoint Azure Subscription Endpoint
     * @return List of GMAf XML objects
     */
    public static List<GMAFXml> AnalyzeLocalImageWithObjects(String _pathToImageDir, String _outputPath, String _azureSubsKey, String _azureSubsEndpoint){
        List<File> inputFileList = null;
        boolean outputPathCreated = false;
        gmafXmlList = new ArrayList<>();
        ComputerVisionClient compVisClient;

        try {
            outputPathCreated = Helper.CreateDirIfNotExists(_outputPath);

            inputFileList = Helper.GetFilePathListByDirectory(_pathToImageDir);

            if(inputFileList == null || !outputPathCreated){
                ImageDetectionLogger.LogError("Problem beim Einlesen der Dateien oder bei der Anlage des Ausgabepfades.");
                return gmafXmlList;
            }

            // Create an authenticated Computer Vision client.
            if(_azureSubsEndpoint != null && _azureSubsKey != null){
                compVisClient = Authenticate(_azureSubsKey, _azureSubsEndpoint);
            }else{
                compVisClient = Authenticate(Constants.SUBSCRIPTIONKEY, Constants.ENDPOINT);
            }

            //fileList exists and ouputDir created
            for (final File file : inputFileList) {
                //check, if file is directory
                if(file.isDirectory()){
                    continue;
                }

                GMAFXml gmafXml = ImageAnalyzer.AnalyzeLocalImageWithObjects(compVisClient, file);

                if(gmafXml != null){
                    gmafXmlList.add(gmafXml);
                }
            }

            //gmafXmlFileListToPath
            for (final GMAFXml gmafXml:gmafXmlList) {
                Helper.WriteStringToFile(gmafXml.GmafObjToXmlString(), _outputPath, gmafXml.getFileName());
            }

        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return gmafXmlList;
    }

    /**
     * method to analyse single file
     * @param compVisClient client, to work against computer vision services
     * @param _rawImage file, which should be analysed
     * @return GNAF Xml file
     */
    public static GMAFXml AnalyzeLocalImageWithObjects(ComputerVisionClient compVisClient, File _rawImage) {
        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.OBJECTS);
        GMAFXml gmafXml = null;

        try {
            //check file size and compress, 5 Versuche
            for(int i = 0; i < 5; i++){
                if (!Helper.IsImageSizeOk(_rawImage)){
                    Helper.CompressImage(_rawImage);
                }else{
                    break;
                }
            }

            byte[] imageByteArray = Files.readAllBytes(_rawImage.toPath());

            // Call the Computer Vision service and tell it to analyze the loaded image.
            ImageAnalysis analysis = compVisClient.computerVision().analyzeImageInStream().withImage(imageByteArray)
                    .withVisualFeatures(featuresToExtractFromLocalImage).execute();
            
            //generate object list
            List<GMAFObject> gmafObjects = GmafFactory.GetGMAFObjectFromAzureAnalyzer(analysis);
            gmafXml = GmafFactory.GetGmafXMLObject(_rawImage.getName(), gmafObjects);
            
            return gmafXml;
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        return gmafXml;
    }

    /**
     * example implementation to analyse an image and print results to console
     * @param compVisClient client, to work against computer vision services
     * @param _pathToLocalImage image directory
     */
    public static void AnalyzeLocalImageTest(ComputerVisionClient compVisClient, String _pathToLocalImage) {
        /*
         * Analyze a local image:
         *
         * Set a string variable equal to the path of a local image. The image path
         * below is a relative path.
         */
        String pathToLocalImage = _pathToLocalImage;
        
        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.DESCRIPTION);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.CATEGORIES);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.FACES);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.OBJECTS);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.BRANDS);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.ADULT);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.COLOR);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.IMAGE_TYPE);
        
        try {
            // Need a byte array for analyzing a local image.
            File   rawImage       = new File(pathToLocalImage);
            byte[] imageByteArray = Files.readAllBytes(rawImage.toPath());
            
            // Call the Computer Vision service and tell it to analyze the loaded image.
            ImageAnalysis analysis = compVisClient.computerVision().analyzeImageInStream().withImage(imageByteArray)
                                             .withVisualFeatures(featuresToExtractFromLocalImage).execute();

            //get return of analyzed image and write into file
            Helper.WriteStringToFile("", "jsonoutput", "analysis.json");

            // Display image captions and confidence values.
            System.out.println("\nCaptions: ");
            for (ImageCaption caption : analysis.description().captions()) {
                System.out.printf("\'%s\' with confidence %f\n", caption.text(), caption.confidence());
            }
            
            // Display image category names and confidence values.
            System.out.println("\nCategories: ");
            for (Category category : analysis.categories()) {
                System.out.printf("\'%s\' with confidence %f\n", category.name(), category.score());
            }
            
            // Display image tags and confidence values.
            System.out.println("\nTags: ");
            for (ImageTag tag : analysis.tags()) {
                System.out.printf("\'%s\' with confidence %f\n", tag.name(), tag.confidence());
            }
            
            // Display any faces found in the image and their location.
            System.out.println("\nFaces: ");
            for (FaceDescription face : analysis.faces()) {
                System.out.printf("\'%s\' of age %d at location (%d, %d), (%d, %d)\n", face.gender(), face.age(),
                        face.faceRectangle().left(), face.faceRectangle().top(),
                        face.faceRectangle().left() + face.faceRectangle().width(),
                        face.faceRectangle().top() + face.faceRectangle().height());
            }
            
            // Display any objects found in the image.
            System.out.println("\nObjects: ");
            for (DetectedObject object : analysis.objects()) {
                System.out.printf("Object \'%s\' detected at location (%d, %d)\n", object.objectProperty(),
                        object.rectangle().x(), object.rectangle().y());
            }
            
            // Display any brands found in the image.
            System.out.println("\nBrands: ");
            for (DetectedBrand brand : analysis.brands()) {
                System.out.printf("Brand \'%s\' detected at location (%d, %d)\n", brand.name(),
                        brand.rectangle().x(), brand.rectangle().y());
            }
            
            // Display whether any adult/racy/gory content was detected and the confidence
            // values.
            System.out.println("\nAdult: ");
            System.out.printf("Is adult content: %b with confidence %f\n", analysis.adult().isAdultContent(),
                    analysis.adult().adultScore());
            System.out.printf("Has racy content: %b with confidence %f\n", analysis.adult().isRacyContent(),
                    analysis.adult().racyScore());
            System.out.printf("Has gory content: %b with confidence %f\n", analysis.adult().isGoryContent(),
                    analysis.adult().goreScore());
            
            // Display the image color scheme.
            System.out.println("\nColor scheme: ");
            System.out.println("Is black and white: " + analysis.color().isBWImg());
            System.out.println("Accent color: " + analysis.color().accentColor());
            System.out.println("Dominant background color: " + analysis.color().dominantColorBackground());
            System.out.println("Dominant foreground color: " + analysis.color().dominantColorForeground());
            System.out.println("Dominant colors: " + String.join(", ", analysis.color().dominantColors()));
            
            // Display any celebrities detected in the image and their locations.
            System.out.println("\nCelebrities: ");
            for (Category category : analysis.categories()) {
                if (category.detail() != null && category.detail().celebrities() != null) {
                    for (CelebritiesModel celeb : category.detail().celebrities()) {
                        System.out.printf("\'%s\' with confidence %f at location (%d, %d), (%d, %d)\n", celeb.name(),
                                celeb.confidence(), celeb.faceRectangle().left(), celeb.faceRectangle().top(),
                                celeb.faceRectangle().left() + celeb.faceRectangle().width(),
                                celeb.faceRectangle().top() + celeb.faceRectangle().height());
                    }
                }
            }
            
            // Display any landmarks detected in the image and their locations.
            System.out.println("\nLandmarks: ");
            for (Category category : analysis.categories()) {
                if (category.detail() != null && category.detail().landmarks() != null) {
                    for (LandmarksModel landmark : category.detail().landmarks()) {
                        System.out.printf("\'%s\' with confidence %f\n", landmark.name(), landmark.confidence());
                    }
                }
            }
            
            // Display what type of clip art or line drawing the image is.
            System.out.println("\nImage type:");
            System.out.println("Clip art type: " + analysis.imageType().clipArtType());
            System.out.println("Line drawing type: " + analysis.imageType().lineDrawingType());
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

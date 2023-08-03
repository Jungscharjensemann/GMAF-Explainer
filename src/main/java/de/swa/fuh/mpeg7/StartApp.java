package de.swa.fuh.mpeg7;

import java.util.List;

public class StartApp {

	public static void main(String[] args) {
		
		System.out.println("Starting StartApp...");
		
		
		
		Mpeg7Converter Mpeg7Converter = new Mpeg7Converter();
//		Mpeg7Converter.set_pathToMapExport("schema/Map_MMFG.csv"); //Set alternative Map
		
		
		// Testing Import
		String path_to_xml_file = "collection/sw_videos_as_mpeg7.xml";
		List<String> final_file_paths = Mpeg7Converter.import_mpeg7(path_to_xml_file, "temp/mmfg_");
		System.out.println(final_file_paths);
		
		
		//testing Export
//		String path_to_mmfg = "mmfgs/mmfg_No10110433.xml";
//		List<String> final_export_file_paths = Mpeg7Converter.export_mmfg(path_to_mmfg, "dmpeg7/dmpeg7_");
//		System.out.println(final_export_file_paths);
		
		
		
		System.out.println("End of StartApp");

	}

}

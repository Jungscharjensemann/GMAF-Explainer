package de.swa.fuh.mpeg7;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MediaTime Class. Represents MediaTime in MPEG7
 * 
 * @author michaelhermann
 *
 */
public class MediaTime {
	
	private String mediaTimePoint;
	private String mediaDuration;
	
	private String mediaTimePointRegex = "(\\-?\\d+(\\-\\d{2}(\\-\\d{2})?)?)?(T\\d{2}(:\\d{2}(:\\d{2}(:\\d+)?)?)?)?(F\\d+)?";
	private String mediaDurationRegex = "\\-?P(\\d+D)?(T(\\d+H)?(\\d+M)?(\\d+S)?(\\d+N)?)?(\\d+F)?";
	
	public MediaTime() {}
	
	public MediaTime(String mediaTimePoint, String mediaDuration) {
		this.mediaTimePoint = mediaTimePoint;
		this.mediaDuration = mediaDuration;
	}
	
	public String getMediaTimePoint() {
		return mediaTimePoint;
	}
	
	/**
	 * Sets Mediatimepoint and checks for MPEG7 compatibility.
	 * Can be extented to change the mediatimepoint from default format to be mpeg7 valid
	 * @param mediaTimePoint mediatimepoint
	 */
	public void setMediaTimePoint_for_MPEG7(String mediaTimePoint) {
		if (this.check_pattern(mediaTimePointRegex, mediaDuration)){
			this.mediaTimePoint = mediaTimePoint;
		} else {
			System.out.println("MediaTimePoint input is not valid. Need to adjust for MPEG7 Standard");
			this.mediaTimePoint = "T00:00:00:00";
		}
	}
	
	/**
	 * Sets MediaTimepoint without checking for mpeg7 conform regex.
	 * @param mediaTimePoint String representing MediaTimepoint
	 */
	public void setMediaTimePoint(String mediaTimePoint) {
		this.mediaTimePoint = mediaTimePoint;
	}
	
	/**
	 * Get current MediaDuration
	 * @return mediaDuration
	 */
	public String getMediaDuration() {
		return mediaDuration;
	}
	
	/**
	 * Sets Duration for MPEG7
	 * @param mediaDuration Mediaduration
	 */
	public void setMediaDuration_for_MPEG7(String mediaDuration) {
		if (this.check_pattern(mediaDurationRegex, mediaDuration)){
			this.mediaDuration = mediaDuration;
		} else {
			System.out.println("Mediaduration input is not valid. Need to adjust for MPEG7 Standard");
			this.mediaDuration = "PT00H00M00S000N1000F";
		}		
	} 
	
	/**
	 * Checks if input is compatible with pattern
	 * @param regex String representing regular expression
	 * @param input String input
	 * @return true, if is compatible
	 */
	private boolean check_pattern(String regex, String input) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		return m.find();
	}
	
	/**
	 * Return String of  MediaDuration and Mediatimepoint 
	 */
	public String toString() {
		return this.mediaDuration + "; " + this.mediaTimePoint;
	}	
}

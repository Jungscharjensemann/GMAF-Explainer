package de.swa.fuh.mpeg7;

public class Timepoint {
	
	//TODO muss spezifische FOrm haben 
	//<pattern value="(\-?\d+(\-\d{2}(\-\d{2})?)?)?(T\d{2}(:\d{2}(:\d{2}(:\d+)?)?)?)?(F\d+)?((\-|\+)\d{2}:\d{2})?"/>
	
	private String timepoint;

	public String getTimepoint() {
		return timepoint;
	}

	public void setTimepoint(String timepoint) {
		this.timepoint = timepoint;
	}
	

}

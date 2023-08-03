package de.swa.fuh.aws.helperClasses;

public class OperatingSystemClassifier {
	
	private String operatingSystemString;
	private OperatingSystem operatingSystemEnum;
	
	public OperatingSystemClassifier(){
		this.operatingSystemString = System.getProperty("os.name").toLowerCase();
		
        if (isWindows()) {
        	this.operatingSystemEnum = OperatingSystem.WINDOWS;
        } else if (isMac()) {
        	this.operatingSystemEnum = OperatingSystem.MACOSX;
        } else if (isUnix()) {
        	this.operatingSystemEnum = OperatingSystem.WINDOWS;
        } else if (isSolaris()) {
        	this.operatingSystemEnum = OperatingSystem.NotSupportedOs;
        } else {
        	this.operatingSystemEnum = OperatingSystem.NotSupportedOs;
        }
	}
	
	
    private boolean isWindows() {
        return (operatingSystemString.indexOf("win") >= 0);
    }

    private boolean isMac() {
        return (operatingSystemString.indexOf("mac") >= 0);
    }

    private boolean isUnix() {
        return (operatingSystemString.indexOf("nix") >= 0
                || operatingSystemString.indexOf("nux") >= 0
                || operatingSystemString.indexOf("aix") > 0);
    }

    private boolean isSolaris() {
        return (operatingSystemString.indexOf("sunos") >= 0);
    }
	
	public OperatingSystem getOperatingSystem() {
		return operatingSystemEnum;
	}		

}

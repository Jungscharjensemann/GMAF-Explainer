package de.ja.model.audio;

import okhttp3.MediaType;

/**
 * Diese Klasse stellt eine Datenstruktur
 * für die Antwort auf eine Anfrage zum Erzeugen
 * einer auditiven Erklärung dar.
 */
public class SpeechResult {

    private MediaType audioType;

    private byte[] bytes;

    public static SpeechResultBuilder builder() {
        return new SpeechResultBuilder();
    }

    public SpeechResult(MediaType audioType, byte[] bytes) {
        this.audioType = audioType;
        this.bytes = bytes;
    }

    public MediaType getAudioType() {
        return audioType;
    }

    public void setAudioType(MediaType audioType) {
        this.audioType = audioType;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String toString() {
        return String.format("SpeechResult(type=%s)", this.getAudioType());
    }

    public static class SpeechResultBuilder {

        private MediaType audioType;

        private byte[] bytes;

        public SpeechResultBuilder() {}

        public SpeechResultBuilder audioType(MediaType audioType) {
            this.audioType = audioType;
            return this;
        }

        public SpeechResultBuilder bytes(byte[] bytes) {
            this.bytes = bytes;
            return this;
        }

        public SpeechResult build() {
            return new SpeechResult(audioType, bytes);
        }
    }
}

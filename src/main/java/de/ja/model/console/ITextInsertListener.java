package de.ja.model.console;

/**
 * Interface für das Hinzufügen und
 * Entfernen von Text.
 */
public interface ITextInsertListener {

    void onInsert(String text);

    void onClear();
}

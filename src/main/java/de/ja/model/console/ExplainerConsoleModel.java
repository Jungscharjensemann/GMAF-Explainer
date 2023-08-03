package de.ja.model.console;

import java.util.ArrayList;
import java.util.List;

public class ExplainerConsoleModel {

    /**
     * Listener.
     */
    private final List<ITextInsertListener> textInsertList;

    public ExplainerConsoleModel() {
        textInsertList = new ArrayList<>();
    }

    /**
     * Listener registrieren.
     * @param textInsertListener Listener.
     */
    public void addListener(ITextInsertListener textInsertListener) {
        if(textInsertListener != null) {
            textInsertList.add(textInsertListener);
        }
    }

    @SuppressWarnings("unused")
    public void removeListener(ITextInsertListener textInsertListener) {
        if(textInsertListener != null) {
            textInsertList.remove(textInsertListener);
        }
    }

    /**
     * Text hinzufügen.
     * @param text Hinzuzufügender Text.
     */
    public void insertText(String text) {
        textInsertList.forEach(l -> l.onInsert(text));
    }

    /**
     * Löschen.
     */
    public void clear() {
        textInsertList.forEach(ITextInsertListener::onClear);
    }
}

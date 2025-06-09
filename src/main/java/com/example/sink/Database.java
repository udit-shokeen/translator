package com.example.sink;

import io.smallrye.mutiny.Uni;
import java.util.Optional;

public interface Database {
    public Optional<String> getTranslation(String sentence);
    public void addTranslation(String sentence, String translation);
    public void removeLastTranslation(String sentence);
}

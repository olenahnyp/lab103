package ua.edu.ucu.apps;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TimedDocument implements Document {
    public String gcsPath;
    @Override
    public String parse() {
        long startTime = System.currentTimeMillis();
        SmartDocument smartDocument = new SmartDocument(gcsPath);
        smartDocument.parse();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        return Long.toString(elapsedTime);
    }
}

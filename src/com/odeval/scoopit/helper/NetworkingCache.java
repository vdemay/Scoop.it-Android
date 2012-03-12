package com.odeval.scoopit.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import android.content.Context;

import com.odeval.scoopit.image.cache.FileCache;

public class NetworkingCache {
    FileCache fileCache;

    public NetworkingCache(Context context) {
        fileCache = new FileCache(context);
    }
    
    public boolean hasCacheForUrl(String url) {
        File f = fileCache.getFile(url);
        return f.exists();
    }

    public String getCachedForUrl(String url) {
        File f = fileCache.getFile(url);
        if (f.exists()) {
            return getContents(f);
        } else {
            // no cache found
            return null;
        }
    }
    
    public void setCacheForUrl(String content, String url) {
        File f = fileCache.getFile(url);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            setContents(f, content);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getContents(File aFile) {
        StringBuilder contents = new StringBuilder();

        try {
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            return null;
        }

        return contents.toString();
    }

    private void setContents(File aFile, String aContents) throws FileNotFoundException, IOException {
        if (aFile == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!aFile.exists()) {
            throw new FileNotFoundException("File does not exist: " + aFile);
        }
        if (!aFile.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + aFile);
        }
        if (!aFile.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + aFile);
        }

        Writer output = new BufferedWriter(new FileWriter(aFile));
        try {
            output.write(aContents);
        } finally {
            output.close();
        }
    }

}

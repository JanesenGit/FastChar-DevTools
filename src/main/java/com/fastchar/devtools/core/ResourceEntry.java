package com.fastchar.devtools.core;

import java.io.File;

public class ResourceEntry<T> {

    private File resourceFile;
    private String absolutePath;
    private long lastModified;
    private boolean isDirectory;
    private int lastChildrenCount;
    private T resource;

    public void refreshFile(File file) {
        setResourceFile(file);
        setLastModified(file.lastModified());
        setAbsolutePath(file.getAbsolutePath());
        setDirectory(file.isDirectory());
    }

    public boolean isModified() {
        return resourceFile.lastModified() != lastModified && !isDirectory;
    }

    public boolean isDelete() {
        return !resourceFile.exists();
    }


    public String getAbsolutePath() {
        return absolutePath;
    }

    public ResourceEntry<T> setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
        return this;
    }

    public long getLastModified() {
        return lastModified;
    }

    public T setLastModified(long lastModified) {
        this.lastModified = lastModified;
        return (T) this;
    }

    public File getResourceFile() {
        return resourceFile;
    }

    public T setResourceFile(File resourceFile) {
        this.resourceFile = resourceFile;
        return (T) this;
    }

    public int getLastChildrenCount() {
        return lastChildrenCount;
    }

    public T setLastChildrenCount(int lastChildrenCount) {
        this.lastChildrenCount = lastChildrenCount;
        return (T) this;
    }

    public T getResource() {
        return resource;
    }

    public ResourceEntry<T> setResource(T resource) {
        this.resource = resource;
        return this;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public ResourceEntry<T> setDirectory(boolean directory) {
        isDirectory = directory;
        return this;
    }
}

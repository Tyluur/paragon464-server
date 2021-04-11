package com.paragon464.gameserver.cache;

public final class FilesContainer extends Container {

    private int[] filesIndexes;
    private Container[] files;

    public Container[] getFiles() {
        return files;
    }

    public void setFiles(Container[] containers) {
        this.files = containers;
    }

    public int[] getFilesIndexes() {
        return filesIndexes;
    }

    public void setFilesIndexes(int[] containersIndexes) {
        this.filesIndexes = containersIndexes;
    }
}

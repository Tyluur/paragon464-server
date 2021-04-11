package com.paragon464.gameserver.cache;

import com.paragon464.gameserver.cache.stream.InputStream;
import com.paragon464.gameserver.cache.util.Utils;

public final class ContainersInformation {

    private int[] containersIndexes;
    private FilesContainer[] containers;

    public ContainersInformation(byte[] informationContainerPackedData) {
        decodeContainersInformation(Utils.unpackCacheContainer(informationContainerPackedData));
    }

    public void decodeContainersInformation(byte[] data) {
        InputStream stream = new InputStream(data);
        int protocol = stream.readUnsignedByte();
        if (protocol >= 6) {
            stream.readInt();
        }
        int nameHash = stream.readUnsignedByte();
        boolean filesNamed = (0x1 & nameHash) != 0;
        containersIndexes = new int[protocol >= 7 ? stream.readSmart32() : stream.readUnsignedShort()];
        int lastIndex = -1;
        for (int index = 0; index < containersIndexes.length; index++) {
            containersIndexes[index] = protocol >= 7 ? stream.readSmart32()
                : stream.readUnsignedShort() + (index == 0 ? 0 : containersIndexes[index - 1]);
            if (containersIndexes[index] > lastIndex)
                lastIndex = containersIndexes[index];
        }
        containers = new FilesContainer[lastIndex + 1];
        for (int index = 0; index < containersIndexes.length; index++)
            containers[containersIndexes[index]] = new FilesContainer();
        if (filesNamed)
            for (int index = 0; index < containersIndexes.length; index++)
                containers[containersIndexes[index]].setNameHash(stream.readInt());
        for (int index = 0; index < containersIndexes.length; index++) {
            stream.readInt();
        }
        for (int index = 0; index < containersIndexes.length; index++) {
            stream.readInt();
        }
        for (int index = 0; index < containersIndexes.length; index++)
            containers[containersIndexes[index]]
                .setFilesIndexes(new int[protocol >= 7 ? stream.readSmart32() : stream.readUnsignedShort()]);
        for (int index = 0; index < containersIndexes.length; index++) {
            int lastFileIndex = -1;
            for (int fileIndex = 0; fileIndex < containers[containersIndexes[index]]
                .getFilesIndexes().length; fileIndex++) {
                containers[containersIndexes[index]].getFilesIndexes()[fileIndex] = protocol >= 7 ? stream.readSmart32()
                    : stream.readUnsignedShort() + (fileIndex == 0 ? 0
                    : containers[containersIndexes[index]].getFilesIndexes()[fileIndex - 1]);
                if (containers[containersIndexes[index]].getFilesIndexes()[fileIndex] > lastFileIndex)
                    lastFileIndex = containers[containersIndexes[index]].getFilesIndexes()[fileIndex];
            }
            containers[containersIndexes[index]].setFiles(new Container[lastFileIndex + 1]);
            for (int fileIndex = 0; fileIndex < containers[containersIndexes[index]]
                .getFilesIndexes().length; fileIndex++)
                containers[containersIndexes[index]].getFiles()[containers[containersIndexes[index]]
                    .getFilesIndexes()[fileIndex]] = new Container();
        }
        if (filesNamed)
            for (int index = 0; index < containersIndexes.length; index++)
                for (int fileIndex = 0; fileIndex < containers[containersIndexes[index]]
                    .getFilesIndexes().length; fileIndex++)
                    containers[containersIndexes[index]]
                        .getFiles()[containers[containersIndexes[index]].getFilesIndexes()[fileIndex]]
                        .setNameHash(stream.readInt());
    }

    public int[] getContainersIndexes() {
        return containersIndexes;
    }

    public FilesContainer[] getContainers() {
        return containers;
    }
}

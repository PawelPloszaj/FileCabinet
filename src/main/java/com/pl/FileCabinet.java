package com.pl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.*;

public class FileCabinet implements Cabinet {
    private List<Folder> folders;

    private static final Logger logger = LoggerFactory.getLogger(FileCabinet.class);

    public FileCabinet(List<Folder> folders) {
        this.folders = folders;
    }

    @Override
    public Optional<Folder> findFolderByName(String name) {
        for(Folder folder : folders) {
            Folder result = findFolderByName(folder, name);
            if(result != null) {
                logger.info("Folder {} found", result.getName());
                return Optional.of(result);
            }
        }
        logger.info("Folder {} not found", name);
        return Optional.empty();
    }

    @Override
    public List<Folder> findFoldersBySize(String size) {
        if(!size.equals("SMALL") && !size.equals("MEDIUM") && !size.equals("LARGE")) {
            logger.error("Wrong folder size provided : {}", size);
            throw new RuntimeException("Wrong folder size provided for search");
        }
        List<Folder> result = new ArrayList<>();
        for(Folder folder : folders) {
            findFolderBySize(folder, size, result);
        }
        if(!result.isEmpty()) {
            logger.info("Folders with size {} found: {}", size, result.stream().map(Folder::getName).toList());
        } else {
            logger.info("Folders with size {} not found", size);
        }
        return result;
    }

    @Override
    public int count() {
        int result = 0;
        for(Folder folder : folders) {
            result += countMulti(folder);
        }
        logger.info("Count: {}", result);
        return result;
    }

    private Folder findFolderByName(Folder folder, String name) {
        if(folder.getName().equals(name)) {
            return folder;
        } else if(folder instanceof MultiFolder) {
            for(Folder subFolder : ((MultiFolder) folder).getFolders()) {
                Folder result = findFolderByName(subFolder, name);
                if(result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private void findFolderBySize(Folder folder, String size, List<Folder> result) {
        if(folder.getSize().equals(size)) {
            result.add(folder);
        }
        if(folder instanceof MultiFolder) {
            for(Folder subFolder : ((MultiFolder) folder).getFolders()) {
                findFolderBySize(subFolder, size, result);
            }

        }
    }

    private int countMulti(Folder folder) {
        int result = 1;
        if(folder instanceof MultiFolder) {
            for(Folder subFolder : ((MultiFolder) folder).getFolders()) {
                result += countMulti(subFolder);
            }
        }
        return result;
    }
}
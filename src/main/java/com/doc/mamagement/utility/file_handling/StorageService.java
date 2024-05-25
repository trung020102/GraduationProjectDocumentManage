package com.doc.mamagement.utility.file_handling;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();
    void store(MultipartFile file, String folderName, String fileName);
    Stream<Path> loadAll();
    Path load(String fileName, String folderName);
    Resource loadAsResource(String filename, String folderName);
    void deleteAll();
    void deleteByName(String folderName, String fileName) throws IOException;
}

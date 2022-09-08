package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServiceImpls;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import java.io.File;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("ComosFileReader")
public class ComosFileReader implements IFileReader {

    @Override
    public List<String> getFileListFromDirectoryWithSpecificExtension(String directory, String extension) throws IOException {
        return getRespondedFiles(directory, extension);
    }

    @Override
    public String readFile(File file) throws IOException {
        return readFile(file.getAbsolutePath());
    }

    @Override
    public String readFile(String absolutePath) throws IOException {
        return readComosFile(absolutePath, StandardCharsets.UTF_8);
    }

    @Override
    public String readFile(String absolutePath, Charset encoding) throws IOException {
        return readComosFile(absolutePath, encoding);
    }

    private String readComosFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private List<String> getRespondedFiles(String directory, String extension) throws IOException {
        List<String> fileList = Files.walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .filter((Path filePath) -> filePath.toString().endsWith(extension))
                .map(Path::toString)
                .collect(Collectors.toList());
        return fileList;
    }
}

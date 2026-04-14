package com.stripo.example.documents.service;

import com.stripo.example.documents.dto.CloneParamsDto;
import com.stripo.example.documents.dto.ClonedDocumentDto;
import com.stripo.example.documents.dto.DocumentDto;
import com.stripo.example.documents.rest.RestConstants;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@CommonsLog
public class StorageService {
    private final String baseAppUrl;
    private final boolean uniqueNamesEnabled;
    private String storageFolderPath;

    public StorageService(@Value("${documents.store.folder}") String storageFolderPath,
                          @Value("${base.app.url}") String baseAppUrl,
                          @Value("${documents.unique-names.enabled:true}") boolean uniqueNamesEnabled) {
        this.storageFolderPath = storageFolderPath;
        this.baseAppUrl = baseAppUrl;
        this.uniqueNamesEnabled = uniqueNamesEnabled;
    }

    @SneakyThrows
    public DocumentDto upload(MultipartFile file, String guid) {
        File folderToSave = getOrCreateFolderByGuid(guid);
        String fileName = generateUniqueFileName(folderToSave, file.getOriginalFilename());
        File savedFile = new File(folderToSave.getAbsolutePath() + "/" + fileName);
        file.getInputStream().transferTo(new FileOutputStream(savedFile));
        return DocumentDto.builder()
                .url(constructUrl(fileName, guid))
                .originalName(fileName)
                .build();
    }

    private String generateUniqueFileName(File folder, String originalFileName) {
        if (!uniqueNamesEnabled) {
            return originalFileName;
        }
        File candidate = new File(folder, originalFileName);
        if (!candidate.exists()) {
            return originalFileName;
        }
        int dotIndex = originalFileName.lastIndexOf('.');
        String baseName = dotIndex == -1 ? originalFileName : originalFileName.substring(0, dotIndex);
        String extension = dotIndex == -1 ? "" : originalFileName.substring(dotIndex);
        int counter = 1;
        String uniqueName;
        do {
            uniqueName = baseName + " (" + counter + ")" + extension;
            counter++;
        } while (new File(folder, uniqueName).exists());
        return uniqueName;
    }

    public DocumentDto getDocumentInfo(String url) {
        String[] split = url.split("/");
        String fileName = split[split.length - 1];
        String guid = split[split.length - 2];
        return DocumentDto.builder()
                .originalName(fileName)
                .size(new File(storageFolderPath
                        + "/" + guid + "/" + fileName).length())
                .build();
    }


    @SneakyThrows
    public byte[] getFile(String guid, String fileName) {
        FileInputStream in = new FileInputStream(storageFolderPath
                + "/" + guid + "/" + fileName);
        return IOUtils.toByteArray(new BufferedInputStream(in));
    }

    public List<DocumentDto> getDocuments(String guid) {
        File folder = getOrCreateFolderByGuid(guid);
        return Stream.of(folder.list()).sorted().map(f -> DocumentDto.builder()
                .url(constructUrl(f, guid))
                .originalName(f)
                .build()).collect(Collectors.toList());
    }

    public List<ClonedDocumentDto> cloneDocuments(CloneParamsDto cloneParamsDto) {
        File folderFrom = getOrCreateFolderByGuid(cloneParamsDto.getGuidFrom());
        File folderTo = getOrCreateFolderByGuid(cloneParamsDto.getGuidTo());
        return Stream.of(folderFrom.list()).map(origin -> {
            Path originalPath = Paths.get(folderFrom.getAbsolutePath() + "/" + origin);
            String fileName = originalPath.toFile().getName();
            Path copied = Paths.get(folderTo.getAbsolutePath() + "/" + fileName);
            try {
                Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                log.warn("Can not clone document", e);
            }
            return ClonedDocumentDto.builder()
                    .sourceKey(constructUrl(fileName, cloneParamsDto.getGuidFrom()))
                    .targetKey(constructUrl(fileName, cloneParamsDto.getGuidTo()))
                    .build();
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    public void deleteDocuments(String guid) {
        Files.walk(getOrCreateFolderByGuid(guid).toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    private File getOrCreateFolderByGuid(String guid) {
        File folderToSave = new File(storageFolderPath + "/" + guid);
        if (!folderToSave.exists()) {
            folderToSave.mkdir();
        }
        return folderToSave;
    }

    private String constructUrl(String file, String guid) {
        return baseAppUrl + "/" + RestConstants.GET_DOCUMENT + "/" + guid + "/" + file;
    }
}

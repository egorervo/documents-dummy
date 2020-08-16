package com.stripo.example.documents.rest;

import com.stripo.example.documents.dto.CloneParamsDto;
import com.stripo.example.documents.dto.ClonedDocumentDto;
import com.stripo.example.documents.dto.DocumentDto;
import com.stripo.example.documents.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.stripo.example.documents.rest.RestConstants.BASE_URL;
import static com.stripo.example.documents.rest.RestConstants.CLONE;
import static com.stripo.example.documents.rest.RestConstants.INFO;

/*
    Controller for work with Stripo storage api
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentsController {
    private final StorageService storageService;

    @PostMapping
    public DocumentDto upload(@RequestParam("file") MultipartFile file, @RequestParam("guid") String guid) {
        return storageService.upload(file, guid);
    }

    @GetMapping
    public List<DocumentDto> getDocuments(@RequestParam("guid") String guid) {
        return storageService.getDocuments(guid);
    }

    @GetMapping(INFO)
    public DocumentDto getDocumentInfo(@RequestParam("src") String url) {
        return storageService.getDocumentInfo(url);
    }

    @DeleteMapping("/{guid}")
    public void deleteDocuments(@PathVariable String guid) {
        storageService.deleteDocuments(guid);
    }

    @PostMapping(CLONE)
    public List<ClonedDocumentDto> clonedDocuments(@RequestBody CloneParamsDto cloneParamsDto) {
        List<ClonedDocumentDto> clonedDocumentDtos = storageService.cloneDocuments(cloneParamsDto);
        return clonedDocumentDtos;
    }
}

package com.stripo.example.documents.rest;

import com.stripo.example.documents.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.stripo.example.documents.rest.RestConstants.GET_DOCUMENT;

/*
    Unsecured controller to serve images
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ServeDocumentsController {
    private final StorageService storageService;


    @GetMapping(value = GET_DOCUMENT + "/{guid}/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] getFile(@PathVariable String guid, @PathVariable String fileName) {
        return storageService.getFile(guid, fileName);
    }
}

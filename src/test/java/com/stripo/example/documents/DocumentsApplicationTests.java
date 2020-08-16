package com.stripo.example.documents;

import com.stripo.example.documents.dto.DocumentDto;
import com.stripo.example.documents.rest.RestConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DocumentsApplicationTests {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void uploadFileTest() {
        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        Path resourceDirectory = Paths.get("src", "test", "resources", "stripo.png");
        FileSystemResource systemResource = new FileSystemResource(resourceDirectory);
        body.add("file", systemResource);
        body.add("guid", "test-guid");
        ResponseEntity<DocumentDto> stringResponseEntity = this.restTemplate
                .withBasicAuth("user", "secret")
                .postForEntity("http://localhost:" + port + RestConstants.BASE_URL, body, DocumentDto.class);

        assertThat(stringResponseEntity.getBody(), is(DocumentDto.builder().url("http://localhost:8080/documents/file/test-guid/file").build()));

    }

}

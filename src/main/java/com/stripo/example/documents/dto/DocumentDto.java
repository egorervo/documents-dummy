package com.stripo.example.documents.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentDto {
	private String url;
    private String originalName;
	private Long uploadTime;
	private Long size;
    private Integer width;
    private Integer height;
    private String thumbnailUrl;
	private String error;
}

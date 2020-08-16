package com.stripo.example.documents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CloneParamsDto {
    String guidFrom;
    String guidTo;
    List<String> urls;
}

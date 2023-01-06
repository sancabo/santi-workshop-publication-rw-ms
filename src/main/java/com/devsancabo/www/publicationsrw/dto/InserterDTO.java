package com.devsancabo.www.publicationsrw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InserterDTO {
    String inserterClassName;
    Map<String,String> properties;
    String description;

}

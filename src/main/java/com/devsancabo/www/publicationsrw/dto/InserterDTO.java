package com.devsancabo.www.publicationsrw.dto;

import java.util.Map;

public record InserterDTO(String inserterClassName, Map<String,String> properties, String description) { }

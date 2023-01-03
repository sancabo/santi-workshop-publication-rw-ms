package com.devsancabo.www.publicationsrw.dto;

import com.devsancabo.www.publicationsrw.dto.AuthorCreateDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicationCreateResponseDTO {

    private Long id;
    private String content;
    private String datetime;
    private AuthorCreateDTO author;
}

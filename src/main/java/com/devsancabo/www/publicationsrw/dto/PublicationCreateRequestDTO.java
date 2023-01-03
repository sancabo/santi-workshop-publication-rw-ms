package com.devsancabo.www.publicationsrw.dto;

import com.devsancabo.www.publicationsrw.dto.AuthorCreateDTO;
import lombok.Data;
@Data
public class PublicationCreateRequestDTO {

    private String content;
    private AuthorCreateDTO author;

}

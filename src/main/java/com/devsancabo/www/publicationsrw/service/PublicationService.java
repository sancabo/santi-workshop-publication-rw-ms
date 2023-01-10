package com.devsancabo.www.publicationsrw.service;

import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import jakarta.transaction.Transactional;

public interface PublicationService {
    @Transactional
    PublicationCreateResponseDTO create(PublicationCreateRequestDTO dto);


}

package com.devsancabo.www.publicationsrw.service;

import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import com.devsancabo.www.publicationsrw.entity.Publication;
import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.Set;

public interface PublicationService {
    Set<Publication> search(long userId, String date);

    @Transactional
    PublicationCreateResponseDTO create(PublicationCreateRequestDTO dto);

    String populateDB(Integer intensity);

    void stopPopulators();

    Map<String, Object> gerPopulator();
}

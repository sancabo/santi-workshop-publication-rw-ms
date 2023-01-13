package com.devsancabo.www.publicationsrw.service;

import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import com.devsancabo.www.publicationsrw.entity.Publication;
import com.devsancabo.www.publicationsrw.dto.GetPopulatorResponseDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface PublicationService {
    List<Publication> search(String userName , String date, Integer pageNumber, Integer pageSize);

    @Transactional
    PublicationCreateResponseDTO create(PublicationCreateRequestDTO dto);

    GetPopulatorResponseDTO startPopulator(Integer intensity, Boolean runForever);

    void stopPopulators();

    GetPopulatorResponseDTO gerPopulator();
}

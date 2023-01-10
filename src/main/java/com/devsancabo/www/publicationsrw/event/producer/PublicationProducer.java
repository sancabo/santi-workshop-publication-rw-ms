package com.devsancabo.www.publicationsrw.event.producer;

import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;

public interface PublicationProducer {
    void send(PublicationCreateResponseDTO publicationDTO);
}

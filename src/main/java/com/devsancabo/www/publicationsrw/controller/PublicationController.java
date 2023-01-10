package com.devsancabo.www.publicationsrw.controller;

import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import com.devsancabo.www.publicationsrw.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicationController {
    private final PublicationService publicationService;

    @Autowired
    public PublicationController(final PublicationService publicationService) {
        this.publicationService = publicationService;

    }

    @PostMapping("/publicacion")
    public ResponseEntity<PublicationCreateResponseDTO> create(@RequestBody PublicationCreateRequestDTO dto){
        return ResponseEntity.ok(publicationService.create(dto));
    }

}

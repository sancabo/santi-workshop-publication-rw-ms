package com.devsancabo.www.publicationsrw.controller;

import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import com.devsancabo.www.publicationsrw.entity.Publication;
import com.devsancabo.www.publicationsrw.service.PublicationService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PublicationController {
    private final PublicationService publicationService;

    @Autowired
    public PublicationController(final PublicationService publicationService) {
        this.publicationService = publicationService;

    }

    @PostMapping("/publication")
    public ResponseEntity<PublicationCreateResponseDTO> create(@RequestBody PublicationCreateRequestDTO dto){
        return ResponseEntity.ok(publicationService.create(dto));
    }



    @GetMapping("/publication")
    public ResponseEntity<List<Publication>> search(@RequestParam(name = "userName") String userName,
                                                    @RequestParam(required = false, name = "date") String date,
                                                    @RequestParam(name = "pageSize")
                                                        @Min(value = 1 , message = "pageSize has to be greater than 0")
                                                        @NotNull Integer pageSize,
                                                    @RequestParam(name = "pageNumber")
                                                        @Min(value = 1 , message = "pageSize has to be greater than 0")
                                                        @NotNull Integer pageNumber){
       return ResponseEntity.ok(publicationService.search(userName, date, pageNumber, pageSize));

    }
}

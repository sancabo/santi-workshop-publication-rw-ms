package com.devsancabo.www.publicationsrw.controller;

import com.devsancabo.www.publicationsrw.entity.Publication;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import com.devsancabo.www.publicationsrw.dto.GetPopulatorResponseDTO;
import com.devsancabo.www.publicationsrw.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @PostMapping("/publicacion/populator")
    public ResponseEntity<GetPopulatorResponseDTO> populate(@RequestParam(name = "intensity") Integer intensity){
        return ResponseEntity.ok(publicationService.startPopulator(intensity));
    }

    @DeleteMapping("/publicacion/populator")
    public ResponseEntity<Object> stopPopulate(){
        publicationService.stopPopulators();
        return ResponseEntity.ok("Requested stopping of data Population");
    }

    @GetMapping("/publicacion/populator")
    public ResponseEntity<GetPopulatorResponseDTO> getPopulator(){
        return ResponseEntity.ok(publicationService.gerPopulator());
    }

    @GetMapping("/publicacion")
    public ResponseEntity<Set<Publication>> search(@RequestParam(name = "userId") long userId,
                                                   @RequestParam(required = false, name = "date") String date){
       return ResponseEntity.ok(publicationService.search(userId, date));

    }
}

package com.devsancabo.www.publicationsrw.service;

import com.devsancabo.www.LoremIpsum;
import com.devsancabo.www.publicationsrw.dto.GetPopulatorResponseDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import com.devsancabo.www.publicationsrw.entity.Author;
import com.devsancabo.www.publicationsrw.entity.Publication;
import com.devsancabo.www.publicationsrw.populator.Populator;
import com.devsancabo.www.publicationsrw.repository.AuthorRepository;
import com.devsancabo.www.publicationsrw.repository.PublicationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Set;

@Service
public class PublicationServiceImpl implements PublicationService {
    private final Logger logger = LoggerFactory.getLogger(PublicationServiceImpl.class);
    private final PublicationRepository publicationRepository;
    private final AuthorRepository authorRepository;
    private final Populator<PublicationCreateRequestDTO> populator;

    @Autowired
    public PublicationServiceImpl(final PublicationRepository publicationRepository,
                                  final AuthorRepository authorRepository,
                                  final Populator<PublicationCreateRequestDTO> populator){
        this.publicationRepository = publicationRepository;
        this.authorRepository = authorRepository;
        this.populator = populator;
        this.populator.setDataPersister(this::create);
    }

    @Override
    public Set<Publication> search(String username, String date) {
        if(Objects.isNull(date)) {
            return publicationRepository.findByAuthor_Username(username);
        }
        else {
            var datetime = LocalDateTime.parse(date);
            return publicationRepository.findByAuthor_UsernameAndDatetimeGreaterThan(
                    username, Timestamp.from(datetime.toInstant(ZoneOffset.of("-03:00"))));
        }

    }

    @Override
    @Transactional
    public PublicationCreateResponseDTO create(PublicationCreateRequestDTO dto) {
        var detachedAuthors = authorRepository.findByUsername(dto.getAuthor().getUsername());
        Author detachedAuthor;
        if(detachedAuthors.isEmpty()){
            logger.info("Creating new user");
            var author = buildAuthor(dto);
            detachedAuthor = authorRepository.saveAndFlush(author);
        } else {
            logger.info("Using existing User");
            detachedAuthor = detachedAuthors.iterator().next();
        }
        var publication = buildPublication(detachedAuthor);
        var createdPublication = publicationRepository.save(publication);
        return PublicationCreateResponseDTO.builder()
                .content(createdPublication.getContent())
                .datetime(createdPublication.getDatetime().toString())
                .id(createdPublication.getId())
                .author(dto.getAuthor()).build();
    }

    private Author buildAuthor(PublicationCreateRequestDTO dto) {
        var author = new Author();
        String username = dto.getAuthor().getUsername();
        author.setEmail(username + "@gmail.com");
        author.setPassword("password");
        author.setUsername(username);
        return author;
    }

    private Publication buildPublication(Author detachedAuthor) {
        var publication = new Publication();
        publication.setContent(LoremIpsum.getRandomSentence(25));
        publication.setAuthor(detachedAuthor);
        publication.setDatetime(Timestamp.from(Instant.now()));
        return publication;
    }

    @Override
    public GetPopulatorResponseDTO startPopulator(Integer intensity){
        return populator.startPopulator(intensity);
    }

    @Override
    public void stopPopulators() {
        populator.stopPopulator();
    }

    @Override
    public GetPopulatorResponseDTO gerPopulator() {
        return populator.gerPopulatorDTO();
    }

}

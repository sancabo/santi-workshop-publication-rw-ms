package com.devsancabo.www.publicationsrw.service;

import com.devsancabo.www.LoremIpsum;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import com.devsancabo.www.publicationsrw.entity.Author;
import com.devsancabo.www.publicationsrw.entity.Publication;
import com.devsancabo.www.publicationsrw.event.producer.PublicationProducer;
import com.devsancabo.www.publicationsrw.repository.AuthorRepository;
import com.devsancabo.www.publicationsrw.repository.PublicationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class PublicationServiceImpl implements PublicationService {
    private final Logger logger = LoggerFactory.getLogger(PublicationServiceImpl.class);
    private final PublicationRepository publicationRepository;
    private final AuthorRepository authorRepository;
    private final PublicationProducer publicationProducer;

    @Value("${service.populator.insertions:10}")
    private Integer amountPerInserter;

    @Value("${service.populator.thread.timeout:1000}")
    private Integer timeout;

    @Value("${service.populator.inserter.user.ratio:100}")
    private Integer userRatio;


    @Autowired
    public PublicationServiceImpl(final PublicationRepository publicationRepository,
                                  final AuthorRepository authorRepository,
                                  final PublicationProducer publicationProducer){
        this.publicationRepository = publicationRepository;
        this.authorRepository = authorRepository;
        this.publicationProducer = publicationProducer;
    }


    @Override
    @Transactional
    public PublicationCreateResponseDTO create(PublicationCreateRequestDTO dto) {
        var detachedAuthors = authorRepository.findByUsername(dto.getAuthor().getUsername());
        Author detachedAuthor;
        if(detachedAuthors.isEmpty()){
            logger.info("Creating new user");
            var author = buildAuthor(dto);
            //I want to ensure the author is found when I next call findByUsername.
            detachedAuthor = authorRepository.saveAndFlush(author);
        } else {
            logger.info("Using existing User");
            detachedAuthor = detachedAuthors.iterator().next();
        }
        var publication = buildPublication(detachedAuthor);
        var createdPublication = publicationRepository.save(publication);
        var resultDto = PublicationCreateResponseDTO.builder()
                .content(createdPublication.getContent())
                .datetime(createdPublication.getDatetime().toString())
                .id(createdPublication.getId())
                .author(dto.getAuthor()).build();
        publicationProducer.send(resultDto);
        return resultDto;
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

}

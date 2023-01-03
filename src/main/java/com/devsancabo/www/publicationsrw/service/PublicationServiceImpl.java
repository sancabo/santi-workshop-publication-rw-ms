package com.devsancabo.www.publicationsrw.service;

import com.devsancabo.www.LoremIpsum;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import com.devsancabo.www.publicationsrw.entity.Author;
import com.devsancabo.www.publicationsrw.entity.Publication;
import com.devsancabo.www.publicationsrw.repository.AuthorRepository;
import com.devsancabo.www.publicationsrw.repository.PublicationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
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
import java.util.UUID;

@Service
public class PublicationServiceImpl implements PublicationService {
    private final Logger logger = LoggerFactory.getLogger(PublicationServiceImpl.class);
    private final PublicationRepository publicationRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public PublicationServiceImpl(final PublicationRepository publicationRepository, final AuthorRepository authorRepository){
        this.publicationRepository = publicationRepository;
        this.authorRepository = authorRepository;
    }

    //@PostConstruct
    public void populate(){
        long authorId = 1;
        for(int i=0; i<10_000_000; ++i) {
            var publication = new Publication();
            Author detachedAuthor;
            if(i % 100 == 0){
                var author = new Author();
                String username = UUID.randomUUID().toString();
                author.setEmail(username + "@gmail.com");
                author.setPassword("password");
                author.setUsername(username);
                detachedAuthor = authorRepository.save(author);
                authorId = detachedAuthor.getId();
            } else {
                detachedAuthor = authorRepository.getReferenceById(authorId);
            }
            publication.setContent(LoremIpsum.getRandomSentence(25));
            publication.setAuthor(detachedAuthor);
            publicationRepository.save(publication);
        }
    }

    @Override
    public Set<Publication> search(long userId, String date) {
        if(Objects.isNull(date)) {
            return publicationRepository.findByAuthorId(userId);
        }
        else {
            var datetime = LocalDateTime.parse(date);
            return publicationRepository.findByAuthorIdAndDatetimeGreaterThan(
                    userId, Timestamp.from(datetime.toInstant(ZoneOffset.of("-03:00"))));
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
    public void populateDB(){
        for(int i=1;i<10;++i){
            Thread thread = new Thread(new DBPopulator());
            thread.setName("Populator-" + i);
            thread.start();
        }
    }

    private class DBPopulator implements Runnable{

        @SneakyThrows
        @Override
        public void run() {
            var mapper = new ObjectMapper();
            for(int i=0; i<10000; i++){
                var userName = UUID.randomUUID().toString();
                var text = "{\"content\": \"sarasa\",\"author\": {\"username\" : \""+ userName + "\"}}";
                var tPayload = mapper.readValue(text, PublicationCreateRequestDTO.class);

                for(int j=0; j<100; j++){
                    create(tPayload);

                }
            }
        }
    }
}

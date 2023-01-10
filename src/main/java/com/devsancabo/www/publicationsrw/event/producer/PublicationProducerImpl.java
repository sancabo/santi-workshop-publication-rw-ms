package com.devsancabo.www.publicationsrw.event.producer;

import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class PublicationProducerImpl implements PublicationProducer {
    private final Logger logger = LoggerFactory.getLogger(PublicationProducerImpl.class);
    private final KafkaTemplate<String, PublicationCreateResponseDTO> kafkaTemplate;

    @Autowired
    public PublicationProducerImpl(final KafkaTemplate<String, PublicationCreateResponseDTO> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }
    @Override
    public void send(PublicationCreateResponseDTO publicationDTO){
        var eventId = UUID.randomUUID();
        logger.info("Producing Event: {}, id={}", publicationDTO, eventId);
        var future = kafkaTemplate.send("publication-events",publicationDTO);
        future.handle((result, exception) -> {
            if(result != null) {
                logger.info("Event Produced Successfully, id={}", eventId);
                return result;
            } else {
                logger.info("Event Error: {}, id={}", exception.getLocalizedMessage(), eventId);
                return null;
            }});
        logger.info("Produced event successfully");
    }
}

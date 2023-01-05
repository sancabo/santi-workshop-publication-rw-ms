package com.devsancabo.www.publicationsrw.populator;

import com.devsancabo.www.publicationsrw.dto.AuthorCreateDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.populator.inserter.DataInserter;
import com.devsancabo.www.publicationsrw.populator.inserter.UserRatioDataInserter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class PublicationPopulator extends AbstractPopulator<PublicationCreateRequestDTO>{
    @Value("${service.populator.insertions:10}")
    private Integer amountPerInserter;

    @Value("${service.populator.thread.timeout:1000}")
    private Integer timeout;

    @Value("${service.populator.inserter.user.ratio:100}")
    private Integer userRatio;

    @Autowired
    public PublicationPopulator(){
        super.dataProducer = () -> {
            var publicationDto = new PublicationCreateRequestDTO();
            var author = new AuthorCreateDTO();
            author.setUsername(UUID.randomUUID().toString());
            publicationDto.setAuthor(author);
            publicationDto.setContent("");
            return publicationDto;
        };// ;};
        super.status = Status.UNINITIALIZED;
    }
    @PostConstruct
    private void init() {
        super.amountToInsert = amountPerInserter;
        super.timeoutInMillis = timeout;
    }


    @Override
    public DataInserter<PublicationCreateRequestDTO> getInserter(final Integer amountPerInserter,
                                                                 final Supplier<PublicationCreateRequestDTO> dataProducer,
                                                                 final Consumer<PublicationCreateRequestDTO> dataPersister,
                                                                 final CountDownLatch latch) {
        return new UserRatioDataInserter(amountPerInserter, dataProducer,dataPersister, latch, userRatio);
    }
}

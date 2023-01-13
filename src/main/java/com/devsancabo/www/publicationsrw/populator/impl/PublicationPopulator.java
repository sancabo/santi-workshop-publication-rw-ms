package com.devsancabo.www.publicationsrw.populator.impl;

import com.devsancabo.www.publicationsrw.dto.AuthorCreateDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.populator.AbstractPopulator;
import com.devsancabo.www.publicationsrw.populator.inserter.AbstractDataInserter;
import com.devsancabo.www.publicationsrw.populator.inserter.impl.UserRatioDataInserter;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PublicationPopulator extends AbstractPopulator<PublicationCreateRequestDTO> {

    private final Integer userRatio;

    public PublicationPopulator(Consumer<PublicationCreateRequestDTO> consumer,
                                Integer amountToInsert,
                                Integer timeout,
                                Integer userRatio){
        super(() -> {

            var author = new AuthorCreateDTO(UUID.randomUUID().toString());
            return new PublicationCreateRequestDTO("",author);
        },consumer, amountToInsert, timeout);
        this.userRatio = userRatio;
    }

    @Override
    public AbstractDataInserter<PublicationCreateRequestDTO> getInserter(final Integer amountPerInserter,
                                                                         final Supplier<PublicationCreateRequestDTO> dataProducer,
                                                                         final Consumer<PublicationCreateRequestDTO> dataPersister,
                                                                         final CountDownLatch latch,
                                                                         final Boolean runForever) {
        return new UserRatioDataInserter(amountPerInserter, dataProducer,dataPersister, latch, this.userRatio, runForever);
    }

}

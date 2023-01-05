package com.devsancabo.www.publicationsrw.populator.inserter;

import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UserRatioDataInserter extends DataInserter<PublicationCreateRequestDTO> {
    private final Integer userRatio;

    private PublicationCreateRequestDTO author = super.dataProducer.get();

    public UserRatioDataInserter(final Integer dataPerThread,
                                 final Supplier<PublicationCreateRequestDTO> producer,
                                 final Consumer<PublicationCreateRequestDTO> create,
                                 final CountDownLatch latch,
                                 final Integer userRatio) {
        super(dataPerThread, producer, create, latch);
        this.userRatio = userRatio;
    }

    @Override
    public Consumer<Supplier<Boolean>> prepareDataForDataSaver(){
        return dataSaverFunction -> {
            boolean calledWithError = false;
            for(int j=0; j < this.userRatio && !super.finished && !calledWithError; j++){
                calledWithError = dataSaverFunction.get();
            }
        };
    }

    @Override
    public PublicationCreateRequestDTO handleDataForDataSaver() {
        return author;
    }
}
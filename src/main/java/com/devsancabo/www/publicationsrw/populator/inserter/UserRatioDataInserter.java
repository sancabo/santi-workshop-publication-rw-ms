package com.devsancabo.www.publicationsrw.populator;

import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UserRatioDataInserter extends DataInserter<PublicationCreateRequestDTO> {
    private static final Integer USER_RATIO = 100;

    public UserRatioDataInserter(final Integer dataPerThread,
                                 final Supplier<PublicationCreateRequestDTO> producer,
                                 final Consumer<PublicationCreateRequestDTO> create,
                                 final CountDownLatch latch) {
        super(dataPerThread, producer, create, latch);
    }

    @Override
    public Consumer<Supplier<Boolean>> prepareDataForDataSaver(){
        return dataSaverFunction -> {
            boolean calledWithError = false;
            for(int j=0; j < USER_RATIO && !super.finished && !calledWithError; j++){
                calledWithError = dataSaverFunction.get();
            }
        };
    }
}
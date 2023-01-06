package com.devsancabo.www.publicationsrw.populator.inserter.impl;

import com.devsancabo.www.publicationsrw.dto.InserterDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.populator.inserter.DataInserter;

import java.util.HashMap;
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
                                 final Integer userRatio,
                                 final Boolean runForever) {
        super(dataPerThread, producer, create, latch, runForever);
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

    @Override
    public InserterDTO getDTORepresentation() {
        var dto = new InserterDTO();
        dto.setProperties(new HashMap<>());
        dto.getProperties().put("userRatio", userRatio.toString());
        dto.setInserterClassName(this.getClass().getName());
        dto.setDescription(
                "Inserts Publications. userRatio tells how many times to reuse one user before creating a new one.");
        return dto;
    }
}
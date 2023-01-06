package com.devsancabo.www.publicationsrw.populator.inserter.impl;

import com.devsancabo.www.publicationsrw.dto.InserterDTO;
import com.devsancabo.www.publicationsrw.populator.inserter.DataInserter;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OtherInserter extends DataInserter<String> {
    /**
     * Represents one process that takes an object T from a producer, and saves it in an arbitrary destination.
     *
     * @param dataAmount    amount records that will be created at the destination.
     * @param dataProducer  a source for obtaining the data to insert
     * @param dataPersister a function that handles the data saving. For example, a call to a save() on a DB.
     * @param latch         a CountDownLatch that the inserter must call when it finishes or is interrupted
     * @param runForever    Whether this inserter should execute indefinitely
     */
    public OtherInserter(final Integer dataAmount,
                         final Supplier<String> dataProducer,
                         final Consumer<String> dataPersister,
                         final CountDownLatch latch,
                         final Boolean runForever) {
        super(dataAmount, dataProducer, dataPersister, latch, runForever);
    }

    @Override
    public Consumer<Supplier<Boolean>> prepareDataForDataSaver() {
        return Supplier::get;
    }

    @Override
    public String handleDataForDataSaver() {
        return super.dataProducer.get();
    }

    @Override
    public InserterDTO getDTORepresentation() {
        return new InserterDTO();
    }
}

package com.devsancabo.www.publicationsrw.populator;

import com.devsancabo.www.publicationsrw.dto.GetPopulatorResponseDTO;
import com.devsancabo.www.publicationsrw.populator.inserter.AbstractDataInserter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.devsancabo.www.publicationsrw.populator.AbstractPopulator.Status.*;

public abstract non-sealed class AbstractPopulator<T> implements Populator<T> {

    private final Integer timeoutInMillis;
    private final Map<String, Thread> populatorMap = new HashMap<>();
    private final Integer amountToInsert;
    private Integer currentIntensity;
    protected Supplier<T> dataProducer;
    protected Consumer<T> dataPersister;
    protected CountDownLatch latch;
    protected AbstractPopulator.Status status;
    private AbstractDataInserter<T> currentInserter;
    private Boolean runForever = true;

    public enum Status{UNINITIALIZED, RUNNING, FAULTED, STOPPING, STOPPED, STOPPED_WITH_ERRORS}


    //TODO: I want to add cadence to inserters
    //TODO: I want to be able to modify the populator while it's running
    //TODO: I want the populator to stop itself when it finishes
    //TODO: When the threads are stopped I have no control of when they are garbage collected. If many acumulate I get stack overflow:


    protected AbstractPopulator(final Supplier<T> dataProducerParam, final Consumer<T> dataPersisterParam,
                                final Integer amountPerInserter, final Integer timeoutInMillis) {
        this.dataProducer = dataProducerParam;
        this.dataPersister = dataPersisterParam;
        this.amountToInsert = amountPerInserter;
        this.timeoutInMillis = timeoutInMillis;
        this.status = UNINITIALIZED;
    }


    public abstract AbstractDataInserter<T> getInserter(final Integer amountToInsert,
                                                        final Supplier<T> dataProducer,
                                                        final Consumer<T> dataPersister,
                                                        final CountDownLatch latch,
                                                        final Boolean runForever);
    @Override
    public GetPopulatorResponseDTO startPopulator(final Integer intensity, Boolean runForeverParam){
        if(status.equals(FAULTED)) {
            throw new UnsupportedOperationException("Populator is faulty. Please Restart It.");
        }
        if(!status.equals(RUNNING)) {
            this.status = RUNNING;
            this.currentIntensity = intensity == null ? 1 : intensity;
            this.latch = new CountDownLatch(this.currentIntensity);
            this.populatorMap.clear();
            this.runForever = runForeverParam;
            for (int i = 1; i < this.currentIntensity + 1; ++i) {
                var dbInserter = getInserter(amountToInsert, dataProducer, dataPersister, latch, runForever);
                this.currentInserter = dbInserter;
                Thread thread = new Thread(dbInserter);
                thread.setName("DbInserter-" + i);
                thread.start();
                this.populatorMap.put(thread.getName(), thread);
            }
        }
        return this.getPopulatorDTO();
    }

    @Override
    public void stopPopulator() {
        if(status.equals(RUNNING)) {
            this.status = STOPPING;
            new Thread(() -> {
                populatorMap.forEach((k, v) -> v.interrupt());
                boolean countedToZero = false;
                try {
                    countedToZero = latch.await(timeoutInMillis, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    latch = null;
                    status = FAULTED;
                    Thread.currentThread().interrupt();
                }
                populatorMap.clear();
                if (countedToZero) {
                    status = STOPPED;
                } else {
                    status = STOPPED_WITH_ERRORS;
                }
            }, "populator-stopper").start();
        }
    }

    @Override
    public GetPopulatorResponseDTO getPopulatorDTO() {
        return GetPopulatorResponseDTO.builder()
                .activeInserterCount(populatorMap.entrySet().stream().filter(e -> !e.getValue().isInterrupted()).count())
                .inserterCount(populatorMap.size())
                .runForever(this.runForever)
                .insetionsPerThread(this.amountToInsert)
                .status(this.status)
                .intensity(this.currentIntensity)
                .inserterSpec(this.currentInserter == null ? null : this.currentInserter.getDTORepresentation())
                .build();
    }

    @Override
    public GetPopulatorResponseDTO resetPopulator(){
        latch = null;
        this.status = UNINITIALIZED;
        this.currentIntensity = null;
        populatorMap.clear();
        return getPopulatorDTO();
    }
}

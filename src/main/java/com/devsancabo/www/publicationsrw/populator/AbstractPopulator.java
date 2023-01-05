package com.devsancabo.www.publicationsrw.populator;

import com.devsancabo.www.publicationsrw.dto.GetPopulatorResponseDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.populator.inserter.DataInserter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractPopulator<T> implements Populator<T> {

    public Integer timeoutInMillis = 60000;
    private final Map<String, Thread> populatorMap = new HashMap<>();
    protected Integer amountToInsert;
    private Integer currentIntensity;
    protected Supplier<T> dataProducer;
    protected Consumer<T> dataPersister;
    protected CountDownLatch latch;
    protected PublicationPopulator.Status status;

    public enum Status{UNINITIALIZED, RUNNING, STOPPED, STOPPED_WITH_ERRORS}



    //TODO: I want to be able to modify the populator while it's running
    @Override
    public GetPopulatorResponseDTO startPopulator(Integer intensity){
        if(!status.equals(Status.RUNNING)) {
            latch = new CountDownLatch(intensity);
            this.currentIntensity = intensity;
            for (int i = 1; i < intensity + 1; ++i) {
                var dbInserter = getInserter(amountToInsert, dataProducer, dataPersister, latch);
                Thread thread = new Thread(dbInserter);
                thread.setName("DbInserter-" + i);
                thread.start();
                populatorMap.put(thread.getName(), thread);
                status = PublicationPopulator.Status.RUNNING;

            }
        }
        return this.gerPopulatorDTO();
    }

    public abstract DataInserter<T> getInserter(final Integer amountToInsert,
                                                final Supplier<T> dataProducer,
                                                final Consumer<T> dataPersister,
                                                final CountDownLatch latch);
    @Override
    public void stopPopulator() {
        if(!status.equals(Status.STOPPED) && !status.equals(Status.STOPPED_WITH_ERRORS) ) {
            populatorMap.forEach((k, v) -> v.interrupt());
            boolean countedToZero = false;
            try {
                countedToZero = latch.await(timeoutInMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                latch = null;
            }
            populatorMap.clear();
            if (countedToZero) {
                status = PublicationPopulator.Status.STOPPED;
            } else {
                status = PublicationPopulator.Status.STOPPED_WITH_ERRORS;
            }
        }
    }

    public void setDataPErsister(final Consumer<T> dataPersister){
        this.dataPersister = dataPersister;
    }

    @Override
    public GetPopulatorResponseDTO gerPopulatorDTO() {
        return GetPopulatorResponseDTO.builder()
                .activeInserterCount(populatorMap.entrySet().stream().filter(e -> !e.getValue().isInterrupted()).count())
                .inserterCount(populatorMap.size())
                .insetionsPerThread(this.amountToInsert)
                .status(this.status)
                .intensity(this.currentIntensity)
                .build();
    }
}

package com.devsancabo.www.publicationsrw.populator;

import com.devsancabo.www.publicationsrw.dto.GetPopulatorResponseDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PopulatorImpl implements Populator<PublicationCreateRequestDTO, UserRatioDataInserter>{
    public static final int TIMEOUT_IN_MILLIS = 60000;
    private final Map<String, Thread> populatorMap = new HashMap<>();
    private final Integer amountToInsertPerThread;
    private Integer currentIntensity;
    private Supplier<PublicationCreateRequestDTO> dataProducer;
    private Consumer<PublicationCreateRequestDTO> dataPersister;
    private CountDownLatch latch;
    private Status status;
    public enum Status{UNINITIALIZED, RUNNING, FAULTED, STOPPED, STOPPED_WITH_ERRORS}

    private PopulatorImpl(){
        this.amountToInsertPerThread = 10;
        this.currentIntensity = 1;
    }

    public PopulatorImpl(final Integer amountToInsertPerThread,
                         final Supplier<PublicationCreateRequestDTO> dataProducer,
                         final Consumer<PublicationCreateRequestDTO> dataPersister){
        this.amountToInsertPerThread = amountToInsertPerThread;
        this.dataProducer = dataProducer;
        this.dataPersister = dataPersister;
        this.status = Status.UNINITIALIZED;
    }

    @Override
    public GetPopulatorResponseDTO startPopulator(Integer intensity){
        latch = new CountDownLatch(intensity);
        this.currentIntensity = intensity;
        for(int i = 1; i < intensity ; ++i) {
            var dbInserter = new UserRatioDataInserter(this.amountToInsertPerThread, this.dataProducer,this.dataPersister, this.latch);
            Thread thread = new Thread(dbInserter);
            thread.setName("DbInserter-" + i);
            thread.start();
            populatorMap.put(thread.getName(),thread);
            status = Status.RUNNING;
        }
        return this.gerPopulatorDTO();
    }

    @Override
    public void stopPopulator() {
        populatorMap.forEach((k, v) -> v.interrupt());
        boolean countedToZero = false;
        try {
            countedToZero = latch.await(TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            latch = null;
        }
        populatorMap.clear();
        if(countedToZero) {
            status = Status.STOPPED;
        } else {
            status = Status.STOPPED_WITH_ERRORS;
        }
    }

    @Override
    public GetPopulatorResponseDTO gerPopulatorDTO() {
        return GetPopulatorResponseDTO.builder()
                .activeInserterCount(populatorMap.entrySet().stream().filter(e -> !e.getValue().isInterrupted()).count())
                .inserterCount(populatorMap.size())
                .insetionsPerThread(this.amountToInsertPerThread)
                .status(this.status)
                .intensity(this.currentIntensity)
                .build();
    }


}

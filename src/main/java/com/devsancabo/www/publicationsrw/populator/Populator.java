package com.devsancabo.www.publicationsrw.populator;

import com.devsancabo.www.publicationsrw.dto.GetPopulatorResponseDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Populator<T, V extends DataInserter<T>> {
    public static final int TIMEOUT_IN_MILLIS = 60000;
    private final Map<String, Thread> populatorMap = new HashMap<>();
    private final Integer amountToInsertPerThread;
    private Integer currentIntensity;
    private Supplier<T> dataProducer;
    private Consumer<T> dataPersister;
    private CountDownLatch latch;
    private Status status;
    public enum Status{UNINITIALIZED, RUNNING, FAULTED, STOPPED, STOPPED_WITH_ERRORS}

    private Populator(){
        this.amountToInsertPerThread = 10;
        this.currentIntensity = 1;
    }

    public Populator(final Integer amountToInsertPerThread, final Supplier<T> dataProducer, final Consumer<T> dataPersister){
        this.amountToInsertPerThread = amountToInsertPerThread;
        this.dataProducer = dataProducer;
        this.dataPersister = dataPersister;
        this.status = Status.UNINITIALIZED;
    }

    public GetPopulatorResponseDTO startPopulator(Integer intensity){
        latch = new CountDownLatch(intensity);
        this.currentIntensity = intensity;
        for(int i = 1; i < intensity ; ++i) {
            V dbInserter;
            try {
                dbInserter =
                        (V) this.getClass().getTypeParameters()[1].getGenericDeclaration().getConstructors()[0]
                                .newInstance(this.amountToInsertPerThread, this.dataProducer,this.dataPersister, this.latch);
            }catch (Exception e) {
                e.printStackTrace();
                status = Status.FAULTED;
                throw new RuntimeException("Cannot start populators. There was a problem in configuration.", e);
            }

            Thread thread = new Thread(dbInserter);
            thread.setName("DbInserter-" + i);
            thread.start();
            populatorMap.put(thread.getName(),thread);
            status = Status.RUNNING;
        }
        return this.gerPopulatorDTO();
    }

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

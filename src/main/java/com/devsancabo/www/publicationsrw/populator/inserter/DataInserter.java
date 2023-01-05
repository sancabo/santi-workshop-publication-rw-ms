package com.devsancabo.www.publicationsrw.populator;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class DataInserter<T> implements Runnable{
    private final Integer dataAmount;
    private final Supplier<T> dataProducer;
    private final Consumer<T> dataPersister;
    private final CountDownLatch latch;
    private boolean error = false;
    protected boolean finished = false;
    protected DataInserter(final Integer dataAmount,
                           final Supplier<T> dataProducer,
                           final Consumer<T> dataPersister,
                           final CountDownLatch latch){
        this.dataAmount = dataAmount;
        this.dataProducer = dataProducer;
        this.dataPersister = dataPersister;
        this.latch = latch;

    }

    private DataInserter() {
        this.dataAmount = 1;
        this.dataProducer = null;
        this.dataPersister = t -> {};
        this.latch = new CountDownLatch(1);
        throw new UnsupportedOperationException("Default Constructor should never be called");
    }

    private Supplier<Boolean> dataSaver(){
        return () -> {
            if (Thread.interrupted()) {
                latch.countDown();
                finished = true;
            }
            T dto = dataProducer.get();
            error = Objects.isNull(dto);
            if (!error) dataPersister.accept(dto);
            return error;
        };
    }

    //With this we add additional logic to how data is saved
    public abstract Consumer<Supplier<Boolean>> prepareDataForDataSaver();

    @Override
    public void run() {
            for (int i = 0; i < dataAmount && !error && !finished; i++) {
                prepareDataForDataSaver().accept(dataSaver());
            }
    }
}
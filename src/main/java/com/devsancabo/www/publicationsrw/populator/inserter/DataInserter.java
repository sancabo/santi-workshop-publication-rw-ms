package com.devsancabo.www.publicationsrw.populator.inserter;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class DataInserter<T> implements Runnable{
    private final Integer dataAmount;
    protected final Supplier<T> dataProducer;
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
            var dto = handleDataForDataSaver();
            error = Objects.isNull(dto);
            if (!error) dataPersister.accept(dto);
            return error;
        };
    }

    //With this we add additional logic to how data is saved
    public abstract Consumer<Supplier<Boolean>> prepareDataForDataSaver();
    public abstract T handleDataForDataSaver();

    @Override
    public void run() {
            for (int i = 0; i < dataAmount && !error && !finished; i++) {
                prepareDataForDataSaver().accept(dataSaver());
            }
        latch.countDown();
        finished = true;
    }
}
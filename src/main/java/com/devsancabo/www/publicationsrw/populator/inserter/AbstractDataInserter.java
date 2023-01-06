package com.devsancabo.www.publicationsrw.populator.inserter;

import com.devsancabo.www.publicationsrw.dto.InserterDTO;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A class that encapsulates the process of batch saving data to some destination.
 * @param <T> The type of data that's going to serve as input for the process.
 */
public abstract class AbstractDataInserter<T> implements Runnable{
    private final Integer dataAmount;
    protected final Supplier<T> dataProducer;
    private final Consumer<T> dataPersister;
    private final CountDownLatch latch;
    protected boolean runForever;
    private boolean error = false;
    protected boolean finished = false;

    /**
     * Represents one process that takes an object T from a producer, and saves it in an arbitrary destination.
     * @param dataAmount amount records that will be created at the destination.
     * @param dataProducer a source for obtaining the data to insert
     * @param dataPersister a function that handles the data saving. For example, a call to a save() on a DB.
     * @param latch a CountDownLatch that the inserter must call when it finishes or is interrupted
     * @param runForever Whether this inserter should execute indefinitely
     */
    protected AbstractDataInserter(final Integer dataAmount,
                                   final Supplier<T> dataProducer,
                                   final Consumer<T> dataPersister,
                                   final CountDownLatch latch,
                                   final Boolean runForever) {

        this.dataProducer = dataProducer;
        this.dataPersister = dataPersister;
        this.latch = latch;
        this.runForever = runForever;
        this.dataAmount = this.runForever ? 1 : dataAmount;
    }

    private AbstractDataInserter() {
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

    /**
     * What to do before inseting one record
     * @return a consumer, that executes some logic before calling the supplier.
     * The supplier is meant to represent the data propducer.
     */
    public abstract Consumer<Supplier<Boolean>> prepareDataForDataSaver();

    /**
     * Permits control of how data is created.
     * @return an instance of the data to insert.
     */
    public abstract T handleDataForDataSaver();

    /**
     * Gets an object meant to represent the inserter for use in apis.
     * @return a representation of this Inserter.
     */
    public abstract InserterDTO getDTORepresentation();

    @Override
    public void run() {
        if(runForever){
            while (!error && !finished) {
                prepareDataForDataSaver().accept(dataSaver());
            }
        } else {
            for (int i = 0; i < dataAmount ; i++) {
                if(error || finished) break;
                prepareDataForDataSaver().accept(dataSaver());
            }
        }
        latch.countDown();
        finished = true;
    }
}
package com.devsancabo.www.publicationsrw.populator.impl;

import com.devsancabo.www.LoremIpsum;
import com.devsancabo.www.publicationsrw.populator.AbstractPopulator;
import com.devsancabo.www.publicationsrw.populator.inserter.AbstractDataInserter;
import com.devsancabo.www.publicationsrw.populator.inserter.impl.OtherInserter;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OtherPopulator extends AbstractPopulator<String> {

    protected OtherPopulator() {
        super(() -> LoremIpsum.getRandomSentence(1),
                OtherPopulator::saveWord,10,1000);
    }

    private static void saveWord(String word) {
        LoggerFactory.getLogger(OtherPopulator.class).info(word);
    }


    @Override
    public AbstractDataInserter<String> getInserter(Integer amountToInsert,
                                                    Supplier<String> dataProducer,
                                                    Consumer<String> dataPersister,
                                                    CountDownLatch latch,
                                                    Boolean runForever) {
        return new OtherInserter(amountToInsert, dataProducer, dataPersister, latch, runForever);
    }


}

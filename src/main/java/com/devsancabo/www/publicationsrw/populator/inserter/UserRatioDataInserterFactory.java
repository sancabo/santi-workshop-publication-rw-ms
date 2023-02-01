package com.devsancabo.www.publicationsrw.populator.inserter;

import com.devsancabo.www.populator.populator.InserterFactory;
import com.devsancabo.www.publicationsrw.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class UserRatioDataInserterFactory implements InserterFactory<UserRatioDataInserter> {

    private final Integer userRatio;

    private final PublicationService service;

    @Autowired
    public UserRatioDataInserterFactory(final PublicationService service,
                                        @Value("${service.populator.inserter.user.ratio:100}") final Integer userRatio){
        this.service = service;
        this.userRatio = userRatio;

    }

    @Override
    public UserRatioDataInserter createInserter(int amountToInsert, CountDownLatch latch, boolean runForever) {
        return new UserRatioDataInserter(amountToInsert, latch, runForever, service, userRatio);
    }
}

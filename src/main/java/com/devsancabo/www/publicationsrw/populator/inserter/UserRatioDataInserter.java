package com.devsancabo.www.publicationsrw.populator.inserter;

import com.devsancabo.www.LoremIpsum;
import com.devsancabo.www.populator.dto.InserterDTO;
import com.devsancabo.www.populator.populator.inserter.AbstractDataInserter;
import com.devsancabo.www.publicationsrw.dto.AuthorCreateDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.service.PublicationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UserRatioDataInserter extends AbstractDataInserter<PublicationCreateRequestDTO> {
    public static final String DESCRIPTION = "Inserts Publications. userRatio tells how many times to reuse one user before creating a new one.";

    private final PublicationService service;
    private final int ratio;
    private int counter;
    private AuthorCreateDTO currentAuthor;

    UserRatioDataInserter(int amount,
                          final CountDownLatch latch,
                          final Boolean runForever,
                          final PublicationService service,
                          int ratio){
        super(amount, latch, runForever);
        this.service = service;
        this.ratio = ratio;
        this.counter = 0;
    }
    @Override
    public PublicationCreateRequestDTO getRecordForSaving() {
        if(counter == ratio) {
            currentAuthor = new AuthorCreateDTO(UUID.randomUUID().toString());
            counter = 0;
        }
        counter++;
        return new PublicationCreateRequestDTO(LoremIpsum.getRandomSentence(25),currentAuthor);
    }

    @Override
    public void saveRecord(PublicationCreateRequestDTO value) {
        service.create(value);
    }

    @Override
    public InserterDTO getDTORepresentation() {
        var propertiesMap = new HashMap<String, String>();
        propertiesMap.put("userRatio", Integer.toString(ratio));
        return new InserterDTO(this.getClass().getSimpleName(), propertiesMap, DESCRIPTION);
    }
}
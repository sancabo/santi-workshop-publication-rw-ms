package com.devsancabo.www.publicationsrw.dto;

import com.devsancabo.www.publicationsrw.populator.impl.PublicationPopulator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPopulatorResponseDTO {
    private PublicationPopulator.Status status;
    private Integer insetionsPerThread;
    private Boolean runForever;
    private Integer intensity;
    private Integer inserterCount;
    private Long activeInserterCount;

    private InserterDTO inserterSpec;
}

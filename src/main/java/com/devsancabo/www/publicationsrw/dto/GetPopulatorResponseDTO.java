package com.devsancabo.www.publicationsrw.dto;

import com.devsancabo.www.populator.populator.DefaultPopulator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPopulatorResponseDTO {
    private DefaultPopulator.Status status;
    private Integer insetionsPerThread;
    private Boolean runForever;
    private Integer intensity;
    private Integer inserterCount;
    private Long activeInserterCount;

    private InserterDTO inserterSpec;
}

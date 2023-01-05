package com.devsancabo.www.publicationsrw.populator;

import com.devsancabo.www.publicationsrw.dto.GetPopulatorResponseDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;

import java.util.function.Consumer;

public interface Populator<T> {
    GetPopulatorResponseDTO startPopulator(Integer intensity);
    void stopPopulator();
    GetPopulatorResponseDTO gerPopulatorDTO();
    void setDataPErsister(final Consumer<T> dataPersister);
}

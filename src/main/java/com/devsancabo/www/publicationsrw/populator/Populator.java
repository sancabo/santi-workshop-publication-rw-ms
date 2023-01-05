package com.devsancabo.www.publicationsrw.populator;

import com.devsancabo.www.publicationsrw.dto.GetPopulatorResponseDTO;

import java.util.function.Consumer;

public sealed interface Populator<T> permits AbstractPopulator{
    GetPopulatorResponseDTO startPopulator(Integer intensity);
    void stopPopulator();
    GetPopulatorResponseDTO gerPopulatorDTO();
    void setDataPersister(final Consumer<T> dataPersister);
}

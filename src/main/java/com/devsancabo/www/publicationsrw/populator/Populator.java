package com.devsancabo.www.publicationsrw.populator;

import com.devsancabo.www.publicationsrw.dto.GetPopulatorResponseDTO;

public sealed interface Populator<T> permits AbstractPopulator{
    GetPopulatorResponseDTO startPopulator(Integer intensity, Boolean runForever);
    void stopPopulator();
    GetPopulatorResponseDTO getPopulatorDTO();

    GetPopulatorResponseDTO resetPopulator();

}

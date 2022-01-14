package com.example.sampleschool.service;

import com.example.sampleschool.shared.dto.AwardDto;

import java.util.List;

public interface AwardService {

    AwardDto createAward(String regNo, AwardDto awardDto);

    AwardDto getAward(String regNo, String awardId);

    AwardDto updateAward(String regNo, String awardId, AwardDto awardDto);

    List<AwardDto> getAwards(String regNo, int page, int limit);

    void deleteAward(String regNo, String awardId);
}

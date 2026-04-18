package com.hainv.tourbooking.domain.response.tour;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import com.hainv.tourbooking.util.constant.LevelEnum;

@Getter
@Setter
public class ResCreateTourDTO {
    private long id;
    private String name;

    private String location;

    private double salary;

    private int quantity;

    private LevelEnum level;

    private Instant startDate;
    private Instant endDate;
    private boolean isActive;

    private List<String> skills;

    private Instant createdAt;
    private String createdBy;
}

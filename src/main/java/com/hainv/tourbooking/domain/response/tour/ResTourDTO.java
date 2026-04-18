package com.hainv.tourbooking.domain.response.tour;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResTourDTO {
    private long id;
    private String name;
    private double basePrice;
    private int duration;
    private String description;
    private DestinationInfo destination;
    private List<String> categories;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DestinationInfo {
        private long id;
        private String name;
        private String location;
        private String image;
    }

}
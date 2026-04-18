package com.hainv.tourbooking.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import com.hainv.tourbooking.util.constant.GenderEnum;

@Setter
@Getter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;

}

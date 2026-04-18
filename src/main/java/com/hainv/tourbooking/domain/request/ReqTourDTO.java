package com.hainv.tourbooking.domain.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.hainv.tourbooking.domain.Destination;

@Getter
@Setter
public class ReqTourDTO {

    private Long id; // Có thể null khi Create, bắt buộc có khi Update

    @NotBlank(message = "Tên tour không được để trống")
    private String name;

    @Min(value = 0, message = "Giá cơ bản không hợp lệ")
    private double basePrice;

    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    private int duration;

    private String description;

    @NotNull(message = "Địa điểm không được để trống")
    private Destination destination;

    private List<Long> categoryIds; // Ví dụ gửi lên mảng [1, 2, 3]
}
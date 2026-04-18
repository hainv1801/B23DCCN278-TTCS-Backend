package com.hainv.tourbooking.domain.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateBookingDTO {

    @NotNull(message = "ID của lịch trình không được để trống")
    private Long tourScheduleId;

    // Trong thực tế user ID lấy từ Token đăng nhập, nhưng tạm thời cho test Postman
    // thì truyền vào đây
    @NotNull(message = "ID của người dùng không được để trống")
    private Long userId;

    @Min(value = 1, message = "Phải có ít nhất 1 người lớn")
    private int totalAdults;

    @Min(value = 0, message = "Số lượng trẻ em không hợp lệ")
    private int totalChildren;

    private String note;
}
package com.hainv.tourbooking.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import com.hainv.tourbooking.domain.TourSchedule;
import com.hainv.tourbooking.domain.Booking;
import com.hainv.tourbooking.domain.User;
import com.hainv.tourbooking.domain.request.ReqCreateBookingDTO;
import com.hainv.tourbooking.domain.response.ResBookingDTO;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.repository.TourScheduleRepository;
import com.hainv.tourbooking.repository.BookingRepository;
import com.hainv.tourbooking.repository.UserRepository;
import com.hainv.tourbooking.util.SecurityUtil;
import com.hainv.tourbooking.util.constant.BookingStatusEnum;
import com.hainv.tourbooking.util.constant.PaymentStatusEnum;
import com.hainv.tourbooking.util.constant.TourStatusEnum;

@Service
public class BookingService {
    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TourScheduleRepository tourScheduleRepository;

    public BookingService(BookingRepository bookingRepository,
            UserRepository userRepository, TourScheduleRepository tourScheduleRepository) {
        this.bookingRepository = bookingRepository;
        this.tourScheduleRepository = tourScheduleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResBookingDTO handleCreateBooking(ReqCreateBookingDTO reqCreateBookingDTO) {
        Booking newBooking = new Booking();
        TourSchedule schedule = this.tourScheduleRepository.findById(reqCreateBookingDTO.getTourScheduleId())
                .orElse(null);
        double totalPrice = reqCreateBookingDTO.getTotalAdults() * schedule.getPriceAdult()
                + reqCreateBookingDTO.getTotalChildren() * schedule.getPriceChild();
        int totalPassengers = reqCreateBookingDTO.getTotalAdults() + reqCreateBookingDTO.getTotalChildren();
        newBooking.setBookingDate(Instant.now());
        newBooking.setPaymentStatus(PaymentStatusEnum.UNPAID);
        newBooking.setTotalAdults(reqCreateBookingDTO.getTotalAdults());
        newBooking.setTotalChildren(reqCreateBookingDTO.getTotalChildren());
        newBooking.setTotalPrice(totalPrice);
        newBooking.setTourSchedule(schedule);
        newBooking.setUser(this.userRepository.findById(reqCreateBookingDTO.getUserId()).orElse(null));
        newBooking.setStatus(BookingStatusEnum.PENDING);
        newBooking.setNote(reqCreateBookingDTO.getNote());

        schedule.setBookedSeats(schedule.getBookedSeats() + totalPassengers);
        if (schedule.getBookedSeats() == schedule.getCapacity()) {
            schedule.setStatus(TourStatusEnum.FULL);
        }
        tourScheduleRepository.save(schedule);
        newBooking = bookingRepository.save(newBooking);
        return convertToResBookingDTO(newBooking);
    }

    public ResBookingDTO handleUpdateBooking(Booking bookingReq) {
        // 1. Tìm đơn hàng CŨ trong database dựa vào ID gửi lên
        Optional<Booking> optBooking = this.bookingRepository.findById(bookingReq.getId());

        if (optBooking.isPresent()) {
            Booking currentBooking = optBooking.get(); // Đơn hàng gốc, chứa ĐẦY ĐỦ thông tin

            // Kiểm tra xem trạng thái cũ có phải là CANCELLED không để tránh việc trừ ghế 2
            // lần
            boolean isAlreadyCancelled = currentBooking.getStatus().equals(BookingStatusEnum.CANCELLED);

            // 2. Cập nhật trạng thái mới từ request
            currentBooking.setStatus(bookingReq.getStatus());
            currentBooking.setPaymentStatus(bookingReq.getPaymentStatus());

            // 3. Xử lý logic hoàn ghế nếu trạng thái MỚI là CANCELLED và trạng thái CŨ chưa
            // hủy
            if (bookingReq.getStatus().equals(BookingStatusEnum.CANCELLED) && !isAlreadyCancelled) {

                // Lấy schedule từ currentBooking (chắc chắn không bị null)
                TourSchedule schedule = currentBooking.getTourSchedule();

                // Lấy số lượng khách từ currentBooking
                int totalPassengers = currentBooking.getTotalAdults() + currentBooking.getTotalChildren();

                // Hoàn lại ghế
                schedule.setBookedSeats(schedule.getBookedSeats() - totalPassengers);

                // Nếu tour đang FULL thì mở lại thành OPEN
                if (schedule.getStatus().equals(TourStatusEnum.FULL)) {
                    schedule.setStatus(TourStatusEnum.OPEN);
                }

                // Lưu lại cục lịch trình đã được cộng ghế
                this.tourScheduleRepository.save(schedule);
            }

            // 4. Lưu lại đơn hàng và convert sang DTO trả về
            Booking savedBooking = this.bookingRepository.save(currentBooking);
            return convertToResBookingDTO(savedBooking);
        }

        return null; // Hoặc ném ra Exception "Không tìm thấy đơn hàng"
    }

    public Optional<Booking> findBookingById(long id) {
        return this.bookingRepository.findById(id);
    }

    public void deleteBookingById(long id) {
        TourSchedule scheduleInDB = this.tourScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule với ID " + id + " không tồn tại"));

        if (scheduleInDB.getBookings().size() > 0) {
            throw new RuntimeException("Không thể xóa lịch trình này vì đã có khách đặt chỗ!");
        }
        this.bookingRepository.deleteAllByTourSchedule(scheduleInDB);

        // 3. Sau khi "dọn dẹp" xong cấp dưới, mới xóa Schedule
        this.tourScheduleRepository.deleteById(scheduleInDB.getId());
    }
    // public ResFetchBookingDTO getBooking(Booking booking) {
    // ResFetchBookingDTO res = new ResFetchBookingDTO();
    // res.setId(booking.getId());
    // res.setEmail(booking.getEmail());
    // res.setUrl(booking.getUrl());
    // res.setStatus(booking.getStatus());
    // res.setCreatedAt(booking.getCreatedAt());
    // res.setCreatedBy(booking.getCreatedBy());
    // res.setUpdatedAt(booking.getUpdatedAt());
    // res.setUpdatedBy(booking.getUpdatedBy());
    // if (booking.getTourSchedule() != null) {
    // res.setCompanyName(booking.getTourSchedule().getCompany().getName());
    // }
    // res.setUser(new ResFetchBookingDTO.UserBooking(booking.getUser().getId(),
    // booking.getUser().getName()));
    // res.setTourSchedule(new
    // ResFetchBookingDTO.TourScheduleBooking(booking.getTourSchedule().getId(),
    // booking.getTourSchedule().getName()));

    // return res;
    // }

    public ResultPaginationDTO fetchAllBookings(Specification<Booking> spec, Pageable pageable) {
        Page<Booking> pageUser = this.bookingRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResBookingDTO> listBooking = pageUser.getContent()
                .stream().map(item -> convertToResBookingDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listBooking);
        return rs;
    }

    // public ResultPaginationDTO fetchBookingByUser(Pageable pageable) {
    // // query builder
    // String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
    // ? SecurityUtil.getCurrentUserLogin().get()
    // : "";
    // FilterNode node = filterParser.parse("email='" + email + "'");
    // FilterSpecification<Booking> spec =
    // filterSpecificationConverter.convert(node);
    // Page<Booking> pageBooking = this.bookingRepository.findAll(spec, pageable);
    // System.out.println(email);
    // ResultPaginationDTO rs = new ResultPaginationDTO();
    // ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

    // mt.setPage(pageable.getPageNumber() + 1);
    // mt.setPageSize(pageable.getPageSize());

    // mt.setPages(pageBooking.getTotalPages());
    // mt.setTotal(pageBooking.getTotalElements());

    // rs.setMeta(mt);

    // // remove sensitive data
    // List<ResBookingDTO> listBooking = pageBooking.getContent()
    // .stream().map(item -> convertToResBookingDTO(item))
    // .collect(Collectors.toList());

    // rs.setResult(listBooking);

    // return rs;
    // }
    public ResultPaginationDTO fetchBookingByUser(Pageable pageable) {
        // 1. Lấy email user đăng nhập
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        System.out.println("Fetching bookings for: " + email);

        // 2. Lấy data TRỰC TIẾP từ Repository bằng hàm tự viết (Không cần dùng
        // FilterSpecification)
        Page<Booking> pageBooking = this.bookingRepository.findByUserEmail(email, pageable);

        // 3. Set thông tin Meta (Phân trang)
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageBooking.getTotalPages());
        mt.setTotal(pageBooking.getTotalElements());
        rs.setMeta(mt);

        // 4. Map sang DTO để loại bỏ dữ liệu nhạy cảm
        List<ResBookingDTO> listBooking = pageBooking.getContent()
                .stream()
                .map(item -> convertToResBookingDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listBooking);

        return rs;
    }

    public ResBookingDTO convertToResBookingDTO(Booking booking) {
        ResBookingDTO dto = new ResBookingDTO();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setNote(booking.getNote());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setStatus(booking.getStatus());
        dto.setTotalAdults(booking.getTotalAdults());
        dto.setTotalChildren(booking.getTotalChildren());
        dto.setTotalPrice(booking.getTotalPrice());
        ResBookingDTO.ScheduleInfo scheduleInfo = new ResBookingDTO.ScheduleInfo();
        scheduleInfo.setId(booking.getTourSchedule().getId());
        scheduleInfo.setTourName(booking.getTourSchedule().getTour().getName());
        dto.setSchedule(scheduleInfo);
        ResBookingDTO.UserInfo userInfo = new ResBookingDTO.UserInfo();
        userInfo.setId(booking.getUser().getId());
        userInfo.setName(booking.getUser().getName());
        userInfo.setEmail(booking.getUser().getEmail());
        dto.setUser(userInfo);
        return dto;
    }
}

package com.hainv.tourbooking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hainv.tourbooking.domain.Destination;
import com.hainv.tourbooking.domain.Tour;
import com.hainv.tourbooking.domain.TourSchedule;
import com.hainv.tourbooking.domain.request.ReqTourDTO;
import com.hainv.tourbooking.domain.Category;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.domain.response.tour.ResCreateTourDTO;
import com.hainv.tourbooking.domain.response.tour.ResTourDTO;
import com.hainv.tourbooking.domain.response.tour.ResUpdateTourDTO;
import com.hainv.tourbooking.repository.DestinationRepository;
import com.hainv.tourbooking.repository.TourRepository;
import com.hainv.tourbooking.repository.TourScheduleRepository;
import com.hainv.tourbooking.repository.BookingRepository;
import com.hainv.tourbooking.repository.CategoryRepository;

@Service
public class TourService {
    private final TourRepository tourRepository;
    private final CategoryRepository categoryRepository;
    private final DestinationRepository destinationRepository;
    private final TourScheduleRepository tourScheduleRepository;
    private final BookingRepository bookingRepository;

    public TourService(TourRepository tourRepository, CategoryRepository categoryRepository,
            DestinationRepository destinationRepository,
            TourScheduleRepository tourScheduleRepository,
            BookingRepository bookingRepository) {
        this.tourRepository = tourRepository;
        this.categoryRepository = categoryRepository;
        this.destinationRepository = destinationRepository;
        this.tourScheduleRepository = tourScheduleRepository;
        this.bookingRepository = bookingRepository;
    }

    public ResTourDTO handleCreateTour(ReqTourDTO reqTourDTO) {
        Tour tour = new Tour();
        tour.setBasePrice(reqTourDTO.getBasePrice());
        tour.setName(reqTourDTO.getName());
        tour.setDuration(reqTourDTO.getDuration());
        tour.setDescription(reqTourDTO.getDescription());
        List<Category> dbCategories = this.categoryRepository.findByIdIn(reqTourDTO.getCategoryIds());
        tour.setCategories(dbCategories);
        Optional<Destination> dOptional = this.destinationRepository.findById(reqTourDTO.getDestination().getId());
        if (dOptional.isPresent()) {
            tour.setDestination(dOptional.get());
        }
        tour = this.tourRepository.save(tour);
        return convertToResTourDTO(tour);

    }

    public ResTourDTO handleUpdateTour(ReqTourDTO reqTourDTO, Tour tourInDB) {

        // 1. Cập nhật trực tiếp các trường cơ bản từ DTO sang entity DB
        tourInDB.setName(reqTourDTO.getName());
        tourInDB.setBasePrice(reqTourDTO.getBasePrice());
        tourInDB.setDuration(reqTourDTO.getDuration());
        tourInDB.setDescription(reqTourDTO.getDescription());

        // 2. Xử lý Categories (Cần check null để tránh lỗi nếu request không gửi lên)
        if (reqTourDTO.getCategoryIds() != null && !reqTourDTO.getCategoryIds().isEmpty()) {
            List<Category> dbCategories = this.categoryRepository.findByIdIn(reqTourDTO.getCategoryIds());
            tourInDB.setCategories(dbCategories);
        }

        // 3. Xử lý Destination an toàn
        if (reqTourDTO.getDestination() != null && reqTourDTO.getDestination().getId() != null) {
            Destination destination = this.destinationRepository.findById(reqTourDTO.getDestination().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Destination ID không tồn tại")); // Hoặc Exception
                                                                                                      // tự tạo của bạn

            tourInDB.setDestination(destination);
        }

        // 4. Lưu lại vào Database
        Tour currentTour = this.tourRepository.save(tourInDB);

        return convertToResTourDTO(currentTour);
    }

    @Transactional // Bắt buộc phải có để đảm bảo tính toàn vẹn dữ liệu
    public void handleDeleteTour(Long tourId) {
        // 1. Tìm Tour xem có tồn tại không
        Tour tourInDB = this.tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("Tour ID không tồn tại"));

        // 2. Lấy ra danh sách các Schedule của Tour này
        List<TourSchedule> schedules = this.tourScheduleRepository.findByTourId(tourInDB.getId());

        // 3. Nếu Tour có Schedule, tiến hành xóa từ dưới lên
        if (schedules != null && !schedules.isEmpty()) {
            // Xóa toàn bộ Booking nằm trong các Schedule này
            for (TourSchedule schedule : schedules) {
                this.bookingRepository.deleteAllByTourSchedule(schedule);
            }

            // Xóa toàn bộ Schedule
            this.tourScheduleRepository.deleteAllByTour(tourInDB);
        }

        // 4. Cuối cùng, xóa Tour
        this.tourRepository.delete(tourInDB);
    }

    public ResultPaginationDTO fetchAllTours(Specification<Tour> spec, Pageable pageable) {
        Page<Tour> pageTour = this.tourRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageTour.getTotalPages());
        mt.setTotal(pageTour.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageTour.getContent());
        return rs;
    }

    public Optional<Tour> fetchTourById(long id) {
        return this.tourRepository.findById(id);
    }

    public ResTourDTO convertToResTourDTO(Tour tour) {
        ResTourDTO res = new ResTourDTO();
        res.setId(tour.getId());
        res.setName(tour.getName());
        res.setBasePrice(tour.getBasePrice());
        res.setDescription(tour.getDescription());
        res.setDuration(tour.getDuration());
        // Check null an toàn trước khi lấy dữ liệu của Destination
        if (tour.getDestination() != null) {
            ResTourDTO.DestinationInfo destinationInfo = new ResTourDTO.DestinationInfo();
            destinationInfo.setId(tour.getDestination().getId());
            destinationInfo.setName(tour.getDestination().getName());
            destinationInfo.setImage(tour.getDestination().getImage());
            destinationInfo.setLocation(tour.getDestination().getLocation());
            res.setDestination(destinationInfo);
        }
        if (tour.getCategories() != null) {
            List<String> categories = tour.getCategories()
                    .stream().map(x -> x.getName())
                    .collect(Collectors.toList());
            res.setCategories(categories);
        }
        return res;
    }
}

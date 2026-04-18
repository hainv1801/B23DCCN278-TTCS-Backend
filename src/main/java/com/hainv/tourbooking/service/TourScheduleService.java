package com.hainv.tourbooking.service;

import com.hainv.tourbooking.domain.Tour;
import com.hainv.tourbooking.domain.TourSchedule;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.domain.response.tour_schedule.ResTourScheduleDTO;
import com.hainv.tourbooking.repository.TourRepository;
import com.hainv.tourbooking.repository.TourScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TourScheduleService {

    private final TourScheduleRepository tourScheduleRepository;
    private final TourRepository tourRepository;

    public TourScheduleService(TourScheduleRepository tourScheduleRepository, TourRepository tourRepository) {
        this.tourScheduleRepository = tourScheduleRepository;
        this.tourRepository = tourRepository;
    }

    public ResTourScheduleDTO handleCreateSchedule(TourSchedule schedule) {
        // Kiểm tra Tour gốc có tồn tại không
        if (schedule.getTour() != null) {
            Optional<Tour> tour = this.tourRepository.findById(schedule.getTour().getId());
            schedule.setTour(tour.orElse(null));
            this.tourScheduleRepository.save(schedule);
            return convertToResTourScheduleDTO(schedule);
        }
        return null;
    }

    public TourSchedule fetchScheduleById(long id) {
        return this.tourScheduleRepository.findById(id).orElse(null);
    }

    public ResTourScheduleDTO handleUpdateSchedule(TourSchedule scheduleReq) {
        Optional<TourSchedule> scheduleOptional = this.tourScheduleRepository.findById(scheduleReq.getId());
        if (scheduleOptional.isPresent()) {
            TourSchedule currentSchedule = scheduleOptional.get();

            // Cập nhật thông tin động
            currentSchedule.setDepartureDate(scheduleReq.getDepartureDate());
            currentSchedule.setReturnDate(scheduleReq.getReturnDate());
            currentSchedule.setPriceAdult(scheduleReq.getPriceAdult());
            currentSchedule.setPriceChild(scheduleReq.getPriceChild());
            currentSchedule.setCapacity(scheduleReq.getCapacity());
            currentSchedule.setStatus(scheduleReq.getStatus());

            if (scheduleReq.getTour() != null) {
                Tour reqTour = this.tourRepository.findById(scheduleReq.getTour().getId()).orElse(null);
                currentSchedule.setTour(reqTour);
            }

            this.tourScheduleRepository.save(currentSchedule);
            return convertToResTourScheduleDTO(currentSchedule);
        }
        return null;
    }

    public ResultPaginationDTO fetchAllSchedules(Specification<TourSchedule> spec, Pageable pageable) {
        Page<TourSchedule> pageSchedule = this.tourScheduleRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageSchedule.getTotalPages());
        mt.setTotal(pageSchedule.getTotalElements());

        rs.setMeta(mt);
        List<ResTourScheduleDTO> listDto = pageSchedule.getContent().stream().map(item -> {
            ResTourScheduleDTO dto = new ResTourScheduleDTO();
            dto.setId(item.getId());
            dto.setDepartureDate(item.getDepartureDate());
            dto.setReturnDate(item.getReturnDate());
            dto.setPriceAdult(item.getPriceAdult());
            dto.setPriceChild(item.getPriceChild());
            dto.setCapacity(item.getCapacity());
            dto.setBookedSeats(item.getBookedSeats());
            dto.setStatus(item.getStatus());
            if (item.getTour() != null) {
                ResTourScheduleDTO.TourInfo tourInfo = new ResTourScheduleDTO.TourInfo();
                tourInfo.setId(item.getTour().getId());
                tourInfo.setName(item.getTour().getName());
                dto.setTourInfo(tourInfo);
            }
            return dto;
        }).collect(Collectors.toList());

        // Ném List DTO sạch sẽ này ra ngoài thay vì List Entity
        rs.setResult(listDto);
        return rs;
    }

    public void handleDeleteSchedule(long id) {
        this.tourScheduleRepository.deleteById(id);
    }

    public ResTourScheduleDTO convertToResTourScheduleDTO(TourSchedule schedule) {
        ResTourScheduleDTO res = new ResTourScheduleDTO();
        res.setId(schedule.getId());
        res.setBookedSeats(schedule.getBookedSeats());
        res.setCapacity(schedule.getCapacity());
        res.setDepartureDate(schedule.getDepartureDate());
        res.setPriceAdult(schedule.getPriceAdult());
        res.setPriceChild(schedule.getPriceChild());
        res.setReturnDate(schedule.getReturnDate());
        res.setStatus(schedule.getStatus());
        ResTourScheduleDTO.TourInfo tourInfo = new ResTourScheduleDTO.TourInfo();
        tourInfo.setId(schedule.getTour().getId());
        tourInfo.setName(schedule.getTour().getName());
        res.setTourInfo(tourInfo);
        return res;
    }
}
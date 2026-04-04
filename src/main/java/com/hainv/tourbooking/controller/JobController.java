package com.hainv.tourbooking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hainv.tourbooking.domain.Job;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.domain.response.job.ResCreateJobDTO;
import com.hainv.tourbooking.domain.response.job.ResUpdateJobDTO;
import com.hainv.tourbooking.service.JobService;
import com.hainv.tourbooking.util.annotation.ApiMessage;
import com.hainv.tourbooking.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("create a job")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(job));
    }

    @GetMapping("/jobs")
    @ApiMessage("get all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(
            @Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok(this.jobService.fetchAllJobs(spec, pageable));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("get job by id")
    public ResponseEntity<Job> getJobById(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Job> optionalJob = this.jobService.fetchJobById(id);
        if (optionalJob.isPresent() == false) {
            throw new IdInvalidException("Job với id = " + id + "không tồn tại!!");
        }
        return ResponseEntity.ok(optionalJob.get());
    }

    @PutMapping("/jobs")
    @ApiMessage("update a job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> optionalJob = this.jobService.fetchJobById(job.getId());
        if (optionalJob.isPresent() == false) {
            throw new IdInvalidException("Job với id = " + job.getId() + "không tồn tại!!");
        }
        ResUpdateJobDTO res = this.jobService.handleUpdateJob(job, optionalJob.get());
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Job> optionalJob = this.jobService.fetchJobById(id);
        if (optionalJob.isPresent() == false) {
            throw new IdInvalidException("Job với id = " + id + "không tồn tại!!");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok(null);
    }
}

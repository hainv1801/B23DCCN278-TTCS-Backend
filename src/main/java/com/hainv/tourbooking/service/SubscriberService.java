package com.hainv.tourbooking.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hainv.tourbooking.domain.Category;
import com.hainv.tourbooking.domain.Subscriber;
import com.hainv.tourbooking.repository.CategoryRepository;
import com.hainv.tourbooking.repository.SubscriberRepository;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final CategoryRepository categoryRepository;

    public SubscriberService(
            SubscriberRepository subscriberRepository,
            CategoryRepository categoryRepository) {
        this.subscriberRepository = subscriberRepository;
        this.categoryRepository = categoryRepository;
    }

    public boolean isExistsByEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber subs) {
        // check skills
        if (subs.getCategories() != null) {
            List<Long> reqSkills = subs.getCategories()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Category> dbSkills = this.categoryRepository.findByIdIn(reqSkills);
            subs.setCategories(dbSkills);
        }

        return this.subscriberRepository.save(subs);
    }

    public Subscriber update(Subscriber subsDB, Subscriber subsRequest) {
        // check skills
        if (subsRequest.getCategories() != null) {
            List<Long> reqSkills = subsRequest.getCategories()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Category> dbSkills = this.categoryRepository.findByIdIn(reqSkills);
            subsDB.setCategories(dbSkills);
        }
        return this.subscriberRepository.save(subsDB);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> subsOptional = this.subscriberRepository.findById(id);
        if (subsOptional.isPresent())
            return subsOptional.get();
        return null;
    }
}
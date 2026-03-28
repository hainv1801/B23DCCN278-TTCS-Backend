package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

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
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("create a skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        if (skill.getName() != null && this.skillService.existByName(skill.getName())) {
            throw new IdInvalidException("Skill đã tồn tại!!!");
        }
        Skill newSkill = this.skillService.handleCreateSkill(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSkill);
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
            @Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.ok(this.skillService.fetchAllSkills(spec, pageable));
    }

    @PutMapping("skills")
    @ApiMessage("update skill")
    public ResponseEntity<Skill> putMethodName(@Valid @RequestBody Skill skill) throws IdInvalidException {
        Skill currentSkill = this.skillService.findSkillById(skill.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Skill với id = " + skill.getId() + " không tồn tại!");
        }
        if (skill.getName() != null && this.skillService.existByName(skill.getName())) {
            throw new IdInvalidException("Skill đã tồn tại!!!");
        }
        currentSkill.setName(skill.getName());
        return ResponseEntity.ok(this.skillService.handleUpdateSkill(currentSkill));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        Skill currentSkill = this.skillService.findSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id = " + id + " không tồn tại");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }
}

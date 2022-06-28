package mgmsports.controller;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.model.CompetitionDto;
import mgmsports.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Competition Controller
 *
 * @author dntvo
 */
@RestController
@RequestMapping("/api/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    /**
     * Create a new competition
     *
     * @param competitionDto competition Dto
     */
    @PostMapping
    public ResponseEntity<Object> createCompetition(@Valid @RequestBody CompetitionDto competitionDto) {
        competitionService.createCompetition(competitionDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Get list of competitions created by a user
     *
     * @param id account ID of a user
     * @return list of competitions
     */
    @GetMapping
    public ResponseEntity<Object> getCompetition(@Valid @RequestParam("accountId") String id) throws EntityNotFoundException {
        Optional<List<CompetitionDto>> allCompetitionOptional = Optional.ofNullable(competitionService.getCompetition(id));
        if (allCompetitionOptional.isPresent()) {
            return new ResponseEntity<>(allCompetitionOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No competition exists", HttpStatus.OK);
    }

    /**
     * Do soft-delete a competition
     *
     * @param id competition Id
     * @return a competition is removed
     */
    @PutMapping("/remove")
    public ResponseEntity<Object> removeCompetition(@Valid @RequestParam("competitionId") String id) throws EntityNotFoundException {
        competitionService.removeCompetition(id);
        return ResponseEntity.ok().build();
    }

}
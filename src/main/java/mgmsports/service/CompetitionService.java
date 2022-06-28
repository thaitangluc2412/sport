package mgmsports.service;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.model.CompetitionDto;

import javax.persistence.EntityExistsException;
import java.util.List;

/**
 * Competition Service
 *
 * @author dntvo
 */
public interface CompetitionService {

    void createCompetition(CompetitionDto competitionDto) throws EntityExistsException;

    void removeCompetition(String competitionId) throws EntityNotFoundException;

    List<CompetitionDto> getCompetition(String accountId) throws EntityNotFoundException;
}
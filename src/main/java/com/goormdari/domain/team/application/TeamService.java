package com.goormdari.domain.team.application;

import com.amazonaws.services.kms.model.NotFoundException;
import com.goormdari.domain.team.domain.Team;
import com.goormdari.domain.team.domain.repository.TeamRepository;
import com.goormdari.domain.team.dto.request.CreateTeamRequest;
import com.goormdari.domain.team.dto.response.CreateTeamResponse;
import com.goormdari.domain.team.exception.TeamAlreadyExistException;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;


    @Transactional
    public CreateTeamResponse createNewTeam(final Long userId, final CreateTeamRequest createTeamRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getTeam() != null) {
            throw new TeamAlreadyExistException();
        }

        String joinCode = UUID.randomUUID().toString();

        Team team = Team.builder()
                .goal(createTeamRequest.goal())
                .deadLine(createTeamRequest.deadline())
                .routine1(createTeamRequest.routine1())
                .routine2(createTeamRequest.routine2())
                .routine3(createTeamRequest.routine3())
                .routine4(createTeamRequest.routine4())
                .joinCode(joinCode)
                .build();

        teamRepository.save(team);

        user.updateTeam(team);
        user.updateGoal(team.getGoal());
        user.updateDeadLine(team.getDeadLine());

        return CreateTeamResponse
                .builder()
                .joinCode(joinCode)
                .build();
    }
}

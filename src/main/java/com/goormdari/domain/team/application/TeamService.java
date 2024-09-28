package com.goormdari.domain.team.application;

import com.amazonaws.services.kms.model.NotFoundException;
import com.goormdari.domain.team.domain.Team;
import com.goormdari.domain.team.domain.repository.TeamRepository;
import com.goormdari.domain.team.dto.request.CreateTeamRequest;
import com.goormdari.domain.team.dto.response.CreateTeamResponse;
import com.goormdari.domain.team.exception.TeamAlreadyExistException;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.repository.UserRepository;
import com.goormdari.global.config.email.EmailClient;
import com.goormdari.global.payload.Message;
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

    private final EmailClient emailClient;


    @Transactional
    public CreateTeamResponse createNewTeam(final String username, final CreateTeamRequest createTeamRequest) {
        User user = userRepository.findByUsername(username)
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


    public Message sendCode(String username, Long guestId) {

        User hostUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        User guestUser = userRepository.findById(guestId)
                .orElseThrow(() -> new NotFoundException("Guest not found"));

        String joinCode = userRepository.findJoinCodeByUserId(hostUser.getId());

        emailClient.sendOneEmail(hostUser.getNickname(), guestUser.getEmail(), joinCode);

        return Message.builder()
                .message("이메일 전송에 성공했습니다.")
                .build();
    }

    @Transactional
    public Message join(String username, String joinCode) {

        Team team = teamRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new NotFoundException("참여코드에 해당하는 방을 찾을 수 없습니다."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.updateTeam(team);


        return Message
                .builder()
                .message("팀(방)에 참여했습니다.")
                .build();
    }
}

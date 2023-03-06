package com.cricketApplication.service.impl.dataService;

import com.cricketApplication.dao.entities.GameDao;
import com.cricketApplication.dao.entities.TeamStatsDao;
import com.cricketApplication.dao.repositories.GameRepository;
import com.cricketApplication.dataModels.response.GameDetailResponse;
import com.cricketApplication.dataModels.response.GameResponse;
import com.cricketApplication.dataModels.response.GetAllGameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameDetailService {
    @Autowired
    private GameRepository gameRepository;

    public GetAllGameResponse getAllGames() {
        List<GameDao> gameDaoList = gameRepository.findAll();
        List<GameResponse> gameResponseList = convert(gameDaoList);
        return GetAllGameResponse.builder().gameList(gameResponseList)
                .status("success").message("List of games").build();
    }

    //Convert GameDao to Game Response
    private List<GameResponse> convert(List<GameDao> gameDaoList) {
        List<GameResponse> gameResponseList = new ArrayList<>();
        for (GameDao gameDao : gameDaoList) {
            gameResponseList.add(
                    GameResponse.builder().
                            gameId(gameDao.getId()).startTime(gameDao.getStartDate().toString()).
                            firstBattingTeamName(gameDao.getFirstBattingTeamName()).
                            firstBowlingTeamName(gameDao.getFirstBowlingTeamName()).
                            totalOvers(gameDao.getTotalOvers())
                            .build()
            );
        }
        return gameResponseList;
    }

    public GetAllGameResponse getAllActiveGames() {
        List<GameDao> gameDaoList = gameRepository.findByGameActive(true);
        List<GameResponse> gameResponseList = convert(gameDaoList);
        return GetAllGameResponse.builder().gameList(gameResponseList)
                .status("success").message("List of games").build();
    }

    public GameDetailResponse getGameDetails(Long gameId) {
        GameDao gameDao = gameRepository.findById(gameId).get();
        List<TeamStatsDao> teamStatsDaoList = gameDao.getTeamStatsDaos();
        TeamStatsDao team1 = teamStatsDaoList.get(0);
        TeamStatsDao team2 = teamStatsDaoList.get(1);
        return createGameDetailResponse(gameDao, team1, team2);
    }

    //Building detailed game response
    private GameDetailResponse createGameDetailResponse(GameDao gameDao, TeamStatsDao team1, TeamStatsDao team2) {
        return GameDetailResponse.builder().gameId(gameDao.getId())
                .status("success")
                .message("Game results")
                .team1Name(team1.getTeamName())
                .team1battingOvers(team1.getBattingOvers())
                .team1bowlingovers(team1.getBowlingovers())
                .team1wicketsTaken(team1.getWicketsTaken())
                .team1score(team1.getScore())
                .team2Name(team2.getTeamName())
                .team2battingOvers(team2.getBattingOvers())
                .team2bowlingovers(team2.getBowlingovers())
                .team2wicketsTaken(team2.getWicketsTaken())
                .team2score(team2.getScore()).build();
    }
}
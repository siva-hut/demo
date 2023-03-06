package com.cricketApplication.service.impl;

import com.cricketApplication.cricketGame.Game;
import com.cricketApplication.cricketGame.GameBuilder;
import com.cricketApplication.dao.entities.BallDataDao;
import com.cricketApplication.dao.entities.GameDao;
import com.cricketApplication.dao.entities.GamePlayerDetails;
import com.cricketApplication.dao.entities.PlayerDao;
import com.cricketApplication.dao.repositories.BallDataRepository;
import com.cricketApplication.dao.repositories.GamePlayerDetailsRepository;
import com.cricketApplication.dao.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReloadGameService {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerDetailsRepository gamePlayerDetailsRepository;
    @Autowired
    private BallDataRepository ballDataRepository;

    public Game reloadGame(Long gameId) {
        GameDao gameDao = gameRepository.findById(gameId).get();
        return reloadGame(gameDao);
    }

    public Game reloadGame(GameDao gameDao) {
        gameDao.setGameActive(true);
        gameRepository.save(gameDao);
        Game game = buildGame(gameDao);
        game.setId(gameDao.getId());
        game.getBattingTeam().createTeamPlayers(getPlayers(game.getId(), game.getBattingTeam().getTeamName()));
        game.getBowlingTeam().createTeamPlayers(getPlayers(game.getId(), game.getBowlingTeam().getTeamName()));
        List<BallDataDao> ballDataDaoList = ballDataRepository.findByGameIdOrderByInnings(game.getId());
        for (BallDataDao ballDataDao : ballDataDaoList) {
            game.simulateNextBall(ballDataDao.getBallOutCome());
        }
        return game;
    }

    private Game buildGame(GameDao gameDao) {
        GameBuilder gameBuilder = new GameBuilder();
        gameBuilder.setTotalOvers(gameDao.getTotalOvers());
        gameBuilder.setTeam1Name(gameDao.getFirstBattingTeamName());
        gameBuilder.setTeam2Name(gameDao.getFirstBowlingTeamName());
        Game game = gameBuilder.getGame();
        return game;
    }

    private List<PlayerDao> getPlayers(Long gameId, String teamName) {
        List<PlayerDao> playerDaoList = new ArrayList<>();
        List<GamePlayerDetails> gamePlayerDetailsList = gamePlayerDetailsRepository.findByGameIdAndTeamNameOrderByIdAsc(gameId, teamName);
        for (GamePlayerDetails gamePlayerDetails : gamePlayerDetailsList) {
            playerDaoList.add(gamePlayerDetails.getPlayerDao());
        }
        return playerDaoList;
    }
}

package com.cricketApplication.service.impl;

import com.cricketApplication.cricketGame.Game;
import com.cricketApplication.cricketGame.GameBuilder;
import com.cricketApplication.dao.entities.GameDao;
import com.cricketApplication.dao.repositories.BallDataRepository;
import com.cricketApplication.dao.repositories.GameRepository;
import com.cricketApplication.service.interfaces.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {
    public static List<Game> activeGameArray = Collections.synchronizedList(new ArrayList<Game>());
    public static Map<Long, Integer> hashMap = new HashMap<Long, Integer>();
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private BallDataRepository ballDataRepository;
    @Autowired
    private ReloadGameService reloadGameService;

    @Override
    public Long createGame(GameBuilder gameBuilder) {
        Game game = gameBuilder.getGame();
        gameRepository.persistGameCreation(game);
        addGame(game);
        return game.getId();
    }

    @Scheduled(fixedRate = 100)
    public void simulateNextBall() {
        Iterator<Game> itr = activeGameArray.iterator();
        while (itr.hasNext()) {
            Game game = itr.next();
            game.simulateNextBall();
            ballDataRepository.persistBallData(game);
            if (game.isGameOver()) {
                gameRepository.persistGameOnCompletion(game);
                itr.remove();
            }
        }
    }

    @Override
    public void addGame(Game game) {
        hashMap.put(game.getId(), activeGameArray.size());
        activeGameArray.add(game);
    }

    @Override
    public void pauseGame(Long gameId) {
        GameDao gameDao = gameRepository.findById(gameId).get();
        gameRepository.save(gameDao);
        activeGameArray.remove((int) hashMap.get(gameId));
    }

    @Override
    public void resumeGame(Long gameId) {
        Game game = reloadGameService.reloadGame(gameId);
        addGame(game);
    }

    @Override
    public void resumeGame(GameDao gameDao) {
        Game game = reloadGameService.reloadGame(gameDao);
        addGame(game);
    }

    @Override
    public Long scheduleGame(GameBuilder gameBuilder, Date date) {
        Game game = gameBuilder.getGame();
        gameRepository.persistGameCreation(game, date);
        return game.getId();
    }
}

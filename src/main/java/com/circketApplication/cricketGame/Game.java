package com.circketApplication.cricketGame;
import com.circketApplication.cricketGame.util.Overs;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Setter
public class Game {
    private Long id;
    private char run;
    private Team battingTeam;
    private Team bowlingTeam;
    private Overs overs;
    private int innings = 1;
    private GameProperties gameProperties = new GameProperties();
    private boolean gameOver = false;
    public void simulateNextBall(char value)
    {
        run = value;
        perpareGame();
        if(run == 'X')
        run = battingTeam.getBatsmanOnStrike().simulateRun(gameProperties.noBall);
        gameProperties.noBall = false;

        switch (run) {
            case 'w':
                gameProperties.setWicketSimulation(true);
                break;
            case 'W':
                wideSimulation();
                break;
            case 'N':
                gameProperties.setNoBall(true);
                overs.reBall();
                break;
            default:
                runSimulation();
        }

        if (overs.overCompleted()) {
            bowlingTeam.changeBowler();
        }
        checkGameStatus();
    }
    private  void perpareGame()
    {
        if(gameProperties.isSwitchSides()) {
            switchSides();
        }
        if(gameProperties.isWicketSimulation()) {
            simulateWicket();
            gameProperties.setWicketSimulation(false);
        }
        if(gameProperties.isChangeStrike()) {
            battingTeam.changeStrike();
            gameProperties.setChangeStrike(false);
        }
        if (overs.overCompleted()) {
            battingTeam.changeStrike();
        }
        bowlingTeam.getBowler().oversBowled.nextBall();
        overs.nextBall();
    }
    private void switchSides()
    {   gameProperties = new GameProperties();
        innings++;
        battingTeam.setBattingOvers(overs);
        Team duplicate = battingTeam;
        battingTeam = bowlingTeam;
        bowlingTeam = duplicate;
        overs = new Overs(overs.getTotalOvers());
    }
    private void simulateWicket()
    {
        bowlingTeam.getBowler().wicketTaken();
        getBattingTeam().increaseWicketLost();
        battingTeam.nextBatsman();
    }
    private void runSimulation()
    {
        int nRun = run-'0' ;
        battingTeam.getBatsmanOnStrike().updateRun(nRun);
        bowlingTeam.getBowler().addRunsGiven(nRun);
        battingTeam.increaseScore(nRun);
        if(nRun%2 == 1)
        {
            gameProperties.setChangeStrike(true);
        }
    }
    private void wideSimulation()
    {
        bowlingTeam.getBowler().addRunsGiven(1);
        getBattingTeam().increaseScore(1);
        overs.reBall();
    }
    private void checkGameStatus()
    {   Boolean condition1 = getOvers().ballsRemaining() == 0 || getBattingTeam().getWicketsLost()==10;
        if(getInnings() == 1 && condition1)
        {
            gameProperties.setSwitchSides(true);
        }
        else if(getInnings() == 2)
        {
            Boolean condition2 = getBattingTeam().getScore()>getBowlingTeam().getScore();
            if(condition1 || condition2)
            {   battingTeam.setBattingOvers(overs);
                gameOver = true;
            }
        }
    }
}
@Setter
@Getter
class GameProperties{
    private boolean switchSides = false;
    private boolean wicketSimulation = false;
    private boolean changeStrike = false;
    boolean noBall = false;
}
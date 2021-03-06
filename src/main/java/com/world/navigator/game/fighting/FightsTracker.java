package com.world.navigator.game.fighting;

import com.world.navigator.game.entities.Room;
import com.world.navigator.game.exceptions.PlayerIsInAFightException;
import com.world.navigator.game.player.Player;

import java.util.concurrent.ConcurrentHashMap;

public class FightsTracker {
  private final ConcurrentHashMap<Room, Player> latestPlayerToEnterRoom;

  public FightsTracker() {
    latestPlayerToEnterRoom = new ConcurrentHashMap<>();
  }

  public void movePlayerTo(Player movingPlayer, Room nextRoom) {
    latestPlayerToEnterRoom.remove(movingPlayer.navigate().getCurrentRoom(), movingPlayer);
    Player playerInRoom = latestPlayerToEnterRoom.put(nextRoom, movingPlayer);

    if (playerInRoom == null) {
      return;
    }

    boolean isValidFight =
            !playerInRoom.equals(movingPlayer)
                    && !playerInRoom.state().isFinished()
                    && playerInRoom.navigate().getCurrentRoom().equals(nextRoom);

    if (isValidFight) {
      createFightBetween(movingPlayer, playerInRoom);
    }
  }

  private void createFightBetween(Player movingPlayer, Player playerInRoom) {
    Fight newFight = new Fight();

    newFight.setTieBreaker(new RockPaperScissorsTieBreaker());

    Fight checkFight = movingPlayer.fight().setCurrentFightIfAbsent(newFight);
    if (checkFight != newFight) {
      throw new PlayerIsInAFightException();
    }
    newFight.setFirstPlayer(movingPlayer);

    Fight secondPlayerCurrentFight = playerInRoom.fight().setCurrentFightIfAbsent(newFight);
    if (secondPlayerCurrentFight == newFight) {
      newFight.setSecondPlayer(playerInRoom);
    } else {
      newFight.secondPlayerIsWinnerOf(secondPlayerCurrentFight);
    }
  }
}

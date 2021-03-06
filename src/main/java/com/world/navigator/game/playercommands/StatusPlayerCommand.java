package com.world.navigator.game.playercommands;

import com.world.navigator.game.player.Player;
import com.world.navigator.game.player.PlayerResponse;

public class StatusPlayerCommand extends PlayerCommand {

  @Override
  public boolean checkState(Player player) {
      return !player.state().isFinished();
  }

    @Override
    public PlayerResponse execute(Player player, String[] args) {
        return player.getStatus();
    }

    @Override
    public PlayerResponse getInvalidStateResponse() {
        return RESPONSE_FACTORY.createFailedStatusResponse(INVALID_STATE_COMMENT);
    }

    @Override
    public PlayerResponse getInvalidArgsResponse() {
        return RESPONSE_FACTORY.createFailedStatusResponse(INVALID_ARGS_COMMENT);
    }

  @Override
  public String getName() {
    return "status";
  }
}

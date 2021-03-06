package com.world.navigator.game.playercommands;

import com.world.navigator.game.player.Player;
import com.world.navigator.game.player.PlayerResponse;

public class SwitchLightsPlayerCommand extends NavigationPlayerCommand {

  @Override
  public PlayerResponse execute(Player player, String[] args) {
      return player.interact().switchRoomLights();
  }

    @Override
    public PlayerResponse getInvalidStateResponse() {
        return RESPONSE_FACTORY.createFailedSwitchLightsResponse(INVALID_STATE_COMMENT);
    }

    @Override
    public PlayerResponse getInvalidArgsResponse() {
        return RESPONSE_FACTORY.createFailedSwitchLightsResponse(INVALID_ARGS_COMMENT);
    }

  @Override
  public String getName() {
    return "switchLights";
  }
}

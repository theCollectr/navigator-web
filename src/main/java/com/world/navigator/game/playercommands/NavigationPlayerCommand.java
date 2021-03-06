package com.world.navigator.game.playercommands;

import com.world.navigator.game.player.Player;

public abstract class NavigationPlayerCommand extends PlayerCommand {

  @Override
  public boolean checkState(Player player) {
      return player.state().isNavigating();
  }
}

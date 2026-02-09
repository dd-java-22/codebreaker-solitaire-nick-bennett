package edu.cnm.deepdive.codebreaker;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.service.AbstractCodebreakerService;
import java.util.concurrent.CompletableFuture;

public class Main {

  static void main() {

    Game game = new Game()
        .pool("ABCDE")
        .length(2);

    AbstractCodebreakerService service = AbstractCodebreakerService.getInstance();
    CompletableFuture<Game> future = service.startGame(game);
    future
        .thenAccept((startedGame) -> {
          System.out.println("Created a game:" + startedGame);
        })
        .exceptionally((throwable) -> {
          throwable.printStackTrace();
          return null;
        });
  }

}

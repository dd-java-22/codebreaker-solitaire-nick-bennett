package edu.cnm.deepdive.codebreaker;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

  private final BlockingQueue<Game> updateQueue = new LinkedBlockingQueue<>();
  private boolean solved;
  private Game game;

  void main() {
    GameViewModel viewModel = GameViewModel.getInstance();
    viewModel.registerGameObserver(updateQueue::add);
    viewModel.registerSolvedObserver((solved) -> this.solved = Boolean.TRUE.equals(solved));
    viewModel.registerErrorObserver(throwable -> {
      System.err.println(throwable.toString());
      updateQueue.add(game);
    });
    viewModel.startGame("ABCDE", 2);

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    while (!solved) {
      try {
        Game game = updateQueue.take();
        this.game = game;
        System.out.println(game);
        if (solved) {
          System.out.println("You solved it!");
        } else {
          String rawInput;
          while ((rawInput = reader.readLine()) != null) {
            String trimmedInput = rawInput.strip();
            if (!trimmedInput.isEmpty()) {
              viewModel.submitGuess(trimmedInput);
              break;
            }
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

}

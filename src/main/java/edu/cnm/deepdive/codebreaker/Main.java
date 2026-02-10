package edu.cnm.deepdive.codebreaker;

import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;

public class Main {

  static void main() {

    GameViewModel viewModel = new GameViewModel();
    viewModel.registerGameObserver(System.out::println);
    viewModel.startGame("ABCDE", 2);
    System.out.println("Game start requested!");
  }

}

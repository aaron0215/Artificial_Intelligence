/****************************************************************
 * studPlayer.java
 * Implements MiniMax search with A-B pruning and iterative deepening search (IDS). The static board
 * evaluator (SBE) function is simple: the # of stones in studPlayer's
 * mancala minue the # in opponent's mancala.
 * -----------------------------------------------------------------------------------------------------------------
 * Licensing Information: You are free to use or extend these projects for educational purposes provided that
 * (1) you do not distribute or publish solutions, (2) you retain the notice, and (3) you provide clear attribution to UW-Madison
 *
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 *
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his TAs.
 *
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu).
 * Some GUI componets are from Mancala Project in Google code.
 */




//################################################################
// studPlayer class
//################################################################

public class myang236Player extends Player {


  /*Use IDS search to find the best move. The step starts from 1 and keeps incrementing by step 1 until
   * interrupted by the time limit. The best move found in each step should be stored in the
   * protected variable move of class Player.
   */
  public void move(GameState state)
  {
    int maxDepth = 1;
    
    while(true) {
      move =  maxAction (state, maxDepth);
      maxDepth++;

    }
  }

  // Return best move for max player. Note that this is a wrapper function created for ease to use.
  // In this function, you may do one step of search. Thus you can decide the best move by comparing the 
  // sbe values returned by maxSBE. This function should call minAction with 5 parameters.
  public int maxAction(GameState state, int maxDepth)
  {
    int alpha = Integer.MIN_VALUE;
    int beta = Integer.MAX_VALUE;
    int bestMove = -1;
    int maxSBE = Integer.MIN_VALUE;
    
    for (int i = 0; i < 6; i++) {
      // check if the move is legal
      if (! state.illegalMove(i)) {
        GameState currState = new GameState(state);
        int currSBE = 0;
        // if move again
        if (currState.applyMove(i)) {
          currSBE = maxAction(currState, 1, maxDepth, alpha, beta);
        } else {
          currSBE = minAction(currState, 1, maxDepth, alpha, beta);
        }
        
        // compare the currSBE with the maxSBE
        if (currSBE > maxSBE) {
          maxSBE = currSBE;
          bestMove = i;
        }
        alpha = Math.max(alpha, maxSBE);
      }
     
    }
   
    return bestMove;

  }

  //return sbe value related to the best move for max player
  public int maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
  {
    if (state.gameOver() || currentDepth == maxDepth) {
        return sbe(state);
    }
    int sbeMax = Integer.MIN_VALUE;
    
    for (int i = 0; i < 6; i++) {
      if (!state.illegalMove(i)) {
        
        GameState currState = new GameState(state);
        int currSBE = 0;
        // if has next move
        if (currState.applyMove(i)) {
          currSBE = maxAction(currState, currentDepth + 1, maxDepth, alpha, beta);
        } else {
          currSBE = minAction(currState, currentDepth + 1, maxDepth, alpha, beta);
        }
        
        sbeMax = Math.max(currSBE, sbeMax);
        
        if (sbeMax >= beta) {
          return sbeMax;
        }
        
       alpha = Math.max(alpha, sbeMax); 
      }
    }
    
    
    return sbeMax;


  }
  //return sbe value related to the bset move for min player
  public int minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
  {
    
    if (state.gameOver() || currentDepth == maxDepth) {
      return sbe(state);
  }
  int sbeMin = Integer.MAX_VALUE;
  
  for (int i = 0; i < 6; i++) {
    if (!state.illegalMove(i)) {
      
      GameState currState = new GameState(state);
      int currSBE = 0;
      // if has next move
      if (currState.applyMove(i)) {
        currSBE = minAction(currState, currentDepth + 1, maxDepth, alpha, beta);
      } else {
        currSBE = maxAction(currState, currentDepth + 1, maxDepth, alpha, beta);
      }
      
      sbeMin = Math.min(currSBE, sbeMin);
      
      if (alpha >= sbeMin) {
        return sbeMin;
      }
      
     beta = Math.min(beta, sbeMin); 
    }
  }
 
  
  return sbeMin;
    
 
  }

  //the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
  private int sbe(GameState state)
  {
    
    // simple SBE method 
    // return the number of stones in the mancala of the current player minus
    // the number in the mancala of the opponent
    int value = state.stoneCount(6) - state.stoneCount(13);
    
    return value;

  }
}


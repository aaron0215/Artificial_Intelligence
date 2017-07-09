///////////////////////////////////////////////////////////////////////////////
// Main Class File:  (FindPath.java)
// File:             (AStarSearcher.java)
// Semester:         (cs540) Spring 2017
//
// Author:           (Miao Yang miao@cs.wisc.edu)
// CS Login:         (miao)
// WISC NetID:       myang236
// Lecturer's Name:  (Erin Winter)
//////////////////////////// 80 columns wide //////////////////////////////////


import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;


/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

  /**
   * Calls the parent class constructor.
   * 
   * @see Searcher
   * @param maze initial maze.
   */
  public AStarSearcher(Maze maze) {
    super(maze);
  }

  /**
   * Main a-star search algorithm.
   * 
   * @return true if the search finds a solution, false otherwise.
   */
  public boolean search() {

    // FILL THIS METHOD

    // explored list is a Boolean array that indicates if a state associated 
    // with a given position in the maze has already been explored. 
    boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

    PriorityQueue<StateFValuePair> frontier = 
        new PriorityQueue<StateFValuePair>();

    // TODO initialize the root state and add
    // to frontier list
    // ...
    Square initSquare = maze.getPlayerSquare();

    State initState = new State(initSquare, null, 0, 0);

    Square goal = maze.getGoalSquare();


    StateFValuePair sfPair = new StateFValuePair(initState,
        distance (initState.getSquare(), goal));

    frontier.add(sfPair);

    while (!frontier.isEmpty()) {
      // TODO return true if a solution has been found
      // TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
      // maxDepthSearched, maxSizeOfFrontier during
      // the search
      // TODO update the maze if a solution found

      // use frontier.poll() to extract the minimum stateFValuePair.
      // use frontier.add(...) to add stateFValue pairs


      StateFValuePair currSFPair = frontier.poll();
      State currState = currSFPair.getState();

      // add to the explored list set as true;
      explored[currState.getX()][currState.getY()] = true;

      noOfNodesExpanded++;

      // maxDepthSearched
      if (currState.getDepth() > maxDepthSearched) {
        maxDepthSearched = currState.getDepth();
      }

      // if the current state is goal
      if (currState.isGoal(maze)) {

        cost = currState.getGValue();
        currState = currState.getParent();

        // get the path from start to goal, search back from goal to start
        // update the maze
        // change the value of square to '.', except goal and root.
        while (currState.getParent() != null) {

          maze.setOneSquare(currState.getSquare(), '.');
          currState = currState.getParent();
        }

        return true;

      }

      // if not goal

      ArrayList<State> succ = currState.getSuccessors(explored, maze);

      for (int i = succ.size() - 1; i >= 0; i--) {

        State nextState = succ.get(i);


        Iterator <StateFValuePair> itrFrontier = frontier.iterator();

        // flag to check if the state is already in the frontier
        boolean isFound = false;

        // to check if successor already in the frontier list
        while (itrFrontier.hasNext()) {

          StateFValuePair existStateFPair = itrFrontier.next();
          State existState = existStateFPair.getState();

          // state already exist in the frontier
          if (nextState.getX() == existState.getX() 
              && nextState.getY() == existState.getY()) {

            isFound = true;

            // update the state with lower cost
            if (nextState.getGValue() < existState.getGValue()) {

              frontier.remove(existStateFPair);
              double fValue = nextState.getGValue() 
                  + distance (nextState.getSquare(), goal);
              StateFValuePair newPair = new StateFValuePair(nextState, fValue);
              frontier.add(newPair);

            } 

          } 

        }

        // state not in the frontier, add it to the frontier
        if(!isFound) {
          double fValue = nextState.getGValue() 
              + distance (nextState.getSquare(), goal);
          StateFValuePair newPair = new StateFValuePair(nextState, fValue);
          frontier.add(newPair);
        }
      }

      // maxSizeOfFrontier
      if (frontier.size() > maxSizeOfFrontier) {
        maxSizeOfFrontier = frontier.size();
      }

    } // end of while is not empty

    // TODO return false if no solution
    return false;
  }

  /**
   * A method to return the Euclidean distance from the current state to goal
   * 
   * @return the Euclidean distance
   */

  private double distance (Square s, Square goal) {

    double a = (s.X - goal.X) * (s.X - goal.X);
    double b = (s.Y - goal.Y) * (s.Y - goal.Y);

    return Math.sqrt(a + b);

  }

}

///////////////////////////////////////////////////////////////////////////////
// Main Class File:  (FindPath.java)
// File:             (DepthFirstSearcher.java)
// Semester:         (cs540) Spring 2017
//
// Author:           (Miao Yang miao@cs.wisc.edu)
// CS Login:         (miao)
// WISC NetID:       myang236
// Lecturer's Name:  (Erin Winter)
//////////////////////////// 80 columns wide //////////////////////////////////
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Depth-First Search (DFS)
 * 
 * You should fill the search() method of this class.
 */
public class DepthFirstSearcher extends Searcher {

  /**
   * Calls the parent class constructor.
   * 
   * @see Searcher
   * @param maze initial maze.
   */
  public DepthFirstSearcher(Maze maze) {
    super(maze);
  }

  /**
   * Main depth first search algorithm.
   * 
   * @return true if the search finds a solution, false otherwise.
   */
  public boolean search() {
    // FILL THIS METHOD

    // explored list is a 2D Boolean array that indicates if a state associated
    // with a given position in the maze has already been explored.
    boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
    Square initSquare = maze.getPlayerSquare();

    State initState = new State(initSquare, null, 0, 0);

    // Stack implementing the Frontier list open list
    LinkedList<State> stack = new LinkedList<State>();
    // add the initial state to the open stack
    stack.push(initState);

    while (!stack.isEmpty()) {
      // TODO return true if find a solution
      // TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
      // maxDepthSearched, maxSizeOfFrontier during
      // the search
      // TODO update the maze if a solution found

      // use stack.pop() to pop the stack.
      // use stack.push(...) to elements to stack


      // remove the state form the stack
      State currState = stack.pop();

      // add to the explored list set as true;
      explored[currState.getX()][currState.getY()] = true;

      noOfNodesExpanded++;

      // maxDepthSearched
      if (currState.getDepth() > maxDepthSearched) {
        maxDepthSearched = currState.getDepth();
      }

      // if is the current state is goal
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

      // current state is not goal

      ArrayList<State> succ = currState.getSuccessors(explored, maze);

      for (int i = 0; i < succ.size(); i++) {

        State nextState = succ.get(i);

        Iterator<State> itr = stack.iterator();

        // flag to check if the state is already in the frontier
        boolean isFound = false; 

        while (itr.hasNext()) {

          State existState = itr.next();

          // state already exist in the frontier
          if (nextState.getX() == existState.getX() 
              && nextState.getY() == existState.getY()) {

            isFound = true;
          }
        }

        // if not exist in the frontier
        if (!isFound) {
          stack.push(nextState);
        }
      }

      // maxSizeOfFrontier
      if (stack.size() > maxSizeOfFrontier) {
        maxSizeOfFrontier = stack.size();
      }

    } // end of while is not empty

    // TODO return false if no solution
    return false;
  }
}

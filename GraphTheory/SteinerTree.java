/**
 * This file contains an implementation of a Steiner Tree algorithm, which finds
 * the cheapest cost to connect a given subset of nodes (which we will refer to
 * as terminal nodes) in an undirected graph. These nodes may be either directly
 * or indirectly connected, possibly connecting to intermediate nodes which are
 * not terminal nodes.
 *
 * Time Complexity: O(V^3 + V^2 * 2^T + V * 3^T)
 * @author Matt Fontaine, Micah Stairs
 * Source: https://www.youtube.com/watch?v=BG4vAoV5kWw
 **/

import java.util.*;

public class SteinerTree {

  /**
   * Finds the cheapest cost to connect a given subset of nodes (which we will
   * refer to as terminal nodes). These nodes may be either directly or indirectly
   * connected, possibly connecting to intermediate nodes which are not terminal
   * nodes.
   * @param distances - The adjacency matrix for the undirected graph
   * @param subsetToConnect - The 0-based indices of the terminal nodes
   * @return the minimum cost required to connect the terminal nodes
   **/
  public static double minLengthSteinerTree(double[][] distances, int[] subsetToConnect) {

    int v = distances.length;
    int t = subsetToConnect.length;

    // Already implicitly connected
    if (t <= 1) {
      return 0;
    }

    // Find the shortest distance between all pairs of nodes
    floydWarshall(distances);

    // This array is indexed using a mask (which says which terminal nodes are
    // connected so far) and node we are currently at (our root)
    double[][] dp = new double[1 << t][v];
    for (int i = 0; i < dp.length; i++) {
      Arrays.fill(dp[i], Double.POSITIVE_INFINITY);
    }

    // Initialize the dynamic programming array with our base cases (starting with
    // each terminal node and going to all other nodes)
    for (int mask = 0; mask < t; mask++) {
      for (int j = 0; j < v; j++) {
        dp[1 << mask][j] = distances[subsetToConnect[mask]][j];
      }
    }

    // Iterate over all of the sets of terminal nodes
    for (int mask = 1; mask < 1 << t; mask++) {

      // Iterate over all of the nodes
      for (int j = 0; j < v; j++) {

        // Effeciently iterate over all subsets of the mask
        for (int subMask = (mask - 1) & mask; subMask > 0; subMask = (subMask - 1) & mask) {

          // Find the distance between the mask and the submask and see if we
          // can use it to get a better answer
          dp[mask][j] = Math.min(dp[mask][j], dp[subMask][j] + dp[mask ^ subMask][j]);

        }

      }

      // Try moving our roots to see if we can get a better answer
      for (int j = 0; j < v; j++) {
        for (int k = 0; k < v; k++) {
          dp[mask][j] = Math.min(dp[mask][j], dp[mask][k] + distances[k][j]);
        }
      }

    }

    // Return answer by looking up the mask with all of the bits set (which
    // represents that all terminal nodes are connected)
    return dp[(1 << t) - 1][subsetToConnect[0]];
  }


  /**
   * Given an adjacency matrix with edge weights between nodes, where
   * Double.POSITIVE_INFINITY is used to indicate that two nodes are not, 
   * connected, this method mutates the given matrix in order to give the
   * shortest distance between all pairs of nodes. Double.NEGATIVE_INFINITY
   * is used to indicate that the edge between node i and node j is part of a
   * negative cycle.
   *
   * NOTE: Usually the diagonal of the adjacency matrix is all  zeros (i.e.
   * distance[i][i] = 0 for all i) since there is typically no cost to go from
   * a node to itself, but this may depend on your graph and the problem you are
   * trying to solve.
   **/
  public static void floydWarshall(double[][] distance) {

    int n = distance.length;

    // Compute all pairs shortest paths
    for (int k = 0; k < n; k++)
      for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
          if (distance[i][k] + distance[k][j] < distance[i][j])
            distance[i][j] = distance[i][k] + distance[k][j];

    // Identify negative cycles (you can comment this
    // out if you know that no negative cycles exist)
    for (int k = 0; k < n; k++)
      for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
          if (distance[i][k] + distance[k][j] < distance[i][j])
            distance[i][j] = Double.NEGATIVE_INFINITY;

  }

}
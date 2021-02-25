package com.example.dots;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Graph {
    private int V;
    private List<List<Integer>> adj;

    Graph(int V)
    {
        this.V = V;
        adj = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            adj.add(i, new ArrayList<Integer>());
        }
    }

    void addEdge(int s, int d)
    {
        adj.get(s).add(d);
    }

    boolean BFS(int s, int d)
    {
        if (s == d)
            return true;

        boolean[] visited = new boolean[V];

        Queue<Integer> queue = new LinkedList<>();

        visited[s] = true;
        queue.offer(s);

        List<Integer> edges;

        while (!queue.isEmpty()) {
            s = queue.poll();

            edges = adj.get(s);
            for (int curr : edges) {
                if (curr == d)
                    return true;

                if (!visited[curr]) {
                    visited[curr] = true;
                    queue.offer(curr);
                }
            }
        }

        return false;
    }

    static boolean isSafe( int i, int j, int[][] M)
    {
        int N = M.length;
        return (i >= 0 && i < N) && (j >= 0 && j < N) && M[i][j] != 0;
    }

    static boolean findPath(int[][] M)
    {
        int s = -1, d = -1;
        int N = M.length;
        int V = N * N + 2;
        Graph g = new Graph(V);

        int k = 1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (M[i][j] != 0) {

                    if (isSafe(i, j + 1, M))
                        g.addEdge(k, k + 1);
                    if (isSafe(i, j - 1, M))
                        g.addEdge(k, k - 1);
                    if (i < N - 1
                            && isSafe(i + 1, j, M))
                        g.addEdge(k, k + N);
                    if (i > 0 && isSafe(i - 1, j, M))
                        g.addEdge(k, k - N);
                }

                if (M[i][j] == 1)
                    s = k;

                if (M[i][j] == 2)
                    d = k;
                k++;
            }
        }

        return g.BFS(s, d);
    }
}

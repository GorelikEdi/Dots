package com.example.dots;

import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;

public class BoardCell {

    private List<List<Integer>> possibleMoves = new ArrayList<>();
    private static String currentColor;
    private static boolean firstTouched;
    private static boolean secondTouched;
    private static ArrayList<BoardCell> dots;
    private boolean isDot;
    private boolean isColored;
    private ImageView imageView;
    private String color;
    private static int[][] graph;
    private int x;
    private int y;
    private int xSecond;
    private int ySecond;

    public BoardCell(ImageView imageView, int x, int y){
        dots = null;
        currentColor = null;
        secondTouched = false;
        firstTouched = false;
        this.imageView = imageView;
        this.isDot = false;
        this.isColored = false;
        this.color = "empty";
        this.x = x;
        this.y = y;
        setPossibleMoves(x, y);
        graph = new int[][]{
                { 3, 3, 3, 3, 3 },
                { 3, 3, 3, 3, 3 },
                { 3, 3, 3, 3, 3 },
                { 3, 3, 3, 3, 3 },
                { 3, 3, 3, 3, 3 }
        };
    }

    public void setXYsecond(int x, int y){
        this.xSecond = x;
        this.ySecond = y;
    }

    public static void setDots(ArrayList<BoardCell> dots){
        BoardCell.dots = dots;
    }

    public boolean isPath(){
        BoardCell tempDot = null;
        boolean isPath;
        for (BoardCell dot : dots){
            if ((dot.x == x && dot.y == y) || (dot.xSecond == x && dot.ySecond == y)) {
                tempDot = dot;
            }
        }
        dots.remove(tempDot);
        for (BoardCell dot : dots) {
            graph[dot.x][dot.y] = 1;
            graph[dot.xSecond][dot.ySecond] = 2;
            isPath = Graph.findPath(graph);
            graph[dot.x][dot.y] = 0;
            graph[dot.xSecond][dot.ySecond] = 0;
            color = dot.color;
            if (!isPath)
                return false;
        }
        return true;
    }

    public List<List<Integer>> getPossibleMoves(){ return this.possibleMoves; }

    public boolean isDot(){
        return isDot;
    }

    public void setIsDot(){
        graph[this.x][this.y] = 0;
        this.isDot = true;
    }

    public boolean isColored(){ return isColored; }

    public String getCurrentColor(){
        return currentColor;
    }

    public String getColor(){
        return this.color;
    }

    public void setIsColored(){
        graph[this.x][this.y] = 0;
        this.isColored = true;
    }

    public boolean isLegalMove(int xTemp, int yTemp){
        List<Integer> temp = new ArrayList<>(2);
        temp.add(xTemp); temp.add(yTemp);
        for (List<Integer> possibleMove : possibleMoves){
            if (possibleMove.equals(temp) && firstTouched)
                return true;
        }
        return false;
    }

    public void setColor(String color){
        int imageResource;
        switch (color){
            case "red":
                if (this.isDot) {
                    if (!this.isColored) {
                        imageResource = R.drawable.red_empty;
                        this.color = color;
                    }
                    else {
                        imageResource = R.drawable.red_colored;
                        currentColor = color;
                    }
                }
                else
                    imageResource = R.drawable.cell_red;
                break;
            case "blue":
                if (this.isDot) {
                    if (!this.isColored) {
                        imageResource =  R.drawable.blue_empty;
                        this.color = color;
                    }
                    else {
                        imageResource = R.drawable.blue_colored;
                        currentColor = color;
                    }
                }
                else
                    imageResource = R.drawable.cell_blue;
                break;
            case "green":
                if (this.isDot) {
                    if (!this.isColored) {
                        imageResource =  R.drawable.gren_empty;
                        this.color = color;
                    }
                    else {
                        imageResource = R.drawable.green_colored;
                        currentColor = color;
                    }
                }
                else
                    imageResource = R.drawable.cell_green;
                break;
            case "yellow":
                if (this.isDot) {
                    if (!this.isColored) {
                        imageResource =  R.drawable.yellow_empty;
                        this.color = color;
                    }
                    else {
                        imageResource = R.drawable.yellow_colored;
                        currentColor = color;
                    }
                }
                else
                    imageResource = R.drawable.cell_yellow;
                break;
            case "pink":
                if (this.isDot) {
                    if (!this.isColored) {
                        imageResource =  R.drawable.pink_empty;
                        this.color = color;
                    }
                    else {
                        imageResource = R.drawable.pink_colored;
                        currentColor = color;
                    }
                }
                else
                    imageResource = R.drawable.cell_pink;
                break;
            case "orange":
                if (this.isDot) {
                    if (!this.isColored) {
                        imageResource =  R.drawable.orange_empty;
                        this.color = color;
                    }
                    else {
                        imageResource = R.drawable.orange_colored;
                        currentColor = color;
                    }
                }
                else
                    imageResource = R.drawable.cell_orange;
                break;
            default:
                if (this.isDot) {
                    if (!this.isColored) {
                        imageResource =  R.drawable.purple_empty;
                        this.color = color;
                    }
                    else {
                        imageResource = R.drawable.purple_colored;
                        currentColor = color;
                    }
                }
                else
                    imageResource = R.drawable.cell_purple;
                break;
        }
        this.imageView.setImageResource(imageResource);
    }

    public void resetTouched(){
        firstTouched = false;
        secondTouched = false;
    }

    public void setTouched(String which){
        if (which.equals("first")) {
            firstTouched = true;
        } else {
            secondTouched = true;
        }
    }

    public boolean isTouched(String which){
        if (which.equals("first")) {
            return firstTouched;
        } else {
            return secondTouched;
        }
    }

    public void setPossibleMoves(int x, int y){
        int[][] temp = new int[5][5];

        try {
            temp[x-1][y] = 0;
            List<Integer> temp2 = new ArrayList<>(2);
            temp2.add(x-1);
            temp2.add(y);
            this.possibleMoves.add(temp2);
        } catch (IndexOutOfBoundsException ignore){}

        try {
            temp[x][y-1] = 0;
            List<Integer> temp2 = new ArrayList<>(2);
            temp2.add(x);
            temp2.add(y-1);
            this.possibleMoves.add(temp2);
        } catch (IndexOutOfBoundsException ignore){}

        try {
            temp[x+1][y] = 0;
            List<Integer> temp2 = new ArrayList<>(2);
            temp2.add(x+1);
            temp2.add(y);
            this.possibleMoves.add(temp2);
        } catch (IndexOutOfBoundsException ignore){}

        try {
            temp[x][y+1] = 0;
            List<Integer> temp2 = new ArrayList<>(2);
            temp2.add(x);
            temp2.add(y+1);
            this.possibleMoves.add(temp2);
        } catch (IndexOutOfBoundsException ignore){}
    }
}

package com.example.dots;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game extends AppCompatActivity {

    private Map<String, Integer> colorMap = new HashMap<>();
    private static final String LEVEL_KEY = Levels.LEVEL_KEY;
    private static final String LEVEL_NUM_KEY = Levels.LEVEL_NUM_KEY;
    private String levelNum;
    private BoardCell[][] boardCells = new BoardCell[5][5];
    private int xPrev;
    private int yPrev;
    private int linesCompleted;
    private boolean pathCompleted = false;
    private TextView gameMsg;
    private ArrayList<BoardCell> dots = new ArrayList<>();
    private DB db = new DB(Game.this);
    private int score;
    private int prevScore;
    private TextView scoreText;
    private TextView timer;
    private GameTimer gameTimer;
    private int lineCompleteTime = 0;
    public static boolean HOME_BUTTON_CLICKED = false;


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Menu.IS_RIGHT_TO_LEFT)
            setContentView(R.layout.game_heb);
        else
            setContentView(R.layout.game);
        Menu.MOVED_TO_ACTIVITY = false;
        setHashMapColors();

        Intent intent = getIntent();
        ArrayList<ArrayList<String>> level = (ArrayList<ArrayList<String>>)
                intent.getSerializableExtra(LEVEL_KEY);
        timer = findViewById(R.id.timer);
        gameTimer = new GameTimer(timer);
        scoreText = findViewById(R.id.score);
        levelNum = intent.getStringExtra(LEVEL_NUM_KEY);
        TextView lvlTextView = findViewById(R.id.lvlTextView);
        lvlTextView.setText(lvlTextView.getText() + " " + levelNum);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        gameMsg = findViewById(R.id.gameMsg);

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View child = tableLayout.getChildAt(i);
            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;
                for (int j = 0; j < row.getChildCount(); j++) {
                    ImageView imageView = (ImageView) row.getChildAt(j);
                    int x = Integer.parseInt(imageView.getContentDescription().toString().split(",")[0]);
                    int y = Integer.parseInt(imageView.getContentDescription().toString().split(",")[1]);
                    imageView.setOnClickListener(new GameOnclickListener());
                    boardCells[x][y] = new BoardCell(imageView, x, y);
                }
            }
        }
        loadLevel(level);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Menu.MOVED_TO_ACTIVITY)
            Menu.mBoundService.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!Menu.MOVED_TO_ACTIVITY)
            Menu.mBoundService.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        gameTimer.stopTimer();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return false;
    }

    public void gameDialog(int titleTextID, int contentTextID) {
        GameDialog gameDialog = new GameDialog();
        gameDialog.showDialog(Game.this, titleTextID, contentTextID, gameTimer);
    }

    public class GameOnclickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            ImageView imageView = findViewById(view.getId());
            String location = imageView.getContentDescription().toString();
            int xCurr = Integer.parseInt(location.split(",")[0]);
            int yCurr = Integer.parseInt(location.split(",")[1]);
            BoardCell cell = boardCells[xCurr][yCurr];
            boolean illegalMove = false;
            if (cell.isDot()) {
                if (!cell.isTouched("first")) {
                    pathCompleted = false;
                    cell.setTouched("first");
                    cell.setIsColored();
                    cell.setColor(cell.getColor());
                    xPrev = xCurr;
                    yPrev = yCurr;
                } else {
                    if (cell.getCurrentColor().equals(cell.getColor()) &&
                            cell.isLegalMove(xPrev, yPrev) && !cell.isColored()) {
                        cell.setTouched("second");
                        cell.setIsColored();
                        cell.setColor(cell.getColor());
                    } else
                        illegalMove = true;
                }
            } else {
                if (cell.isLegalMove(xPrev, yPrev) && !cell.isColored()) {
                    cell.setIsColored();
                    cell.setColor(cell.getCurrentColor());
                } else
                    illegalMove = true;
            }

            if (cell.isLegalMove(xPrev, yPrev) && !illegalMove) {
                xPrev = Integer.parseInt(location.split(",")[0]);
                yPrev = Integer.parseInt(location.split(",")[1]);
                cell.setIsColored();
                cell.setColor(cell.getCurrentColor());

                if (cell.isTouched("first") && cell.isTouched("second")) {
                    cell.resetTouched();
                    pathCompleted = true;
                    linesCompleted--;
                    if (linesCompleted == 0){
                        score += 100;
                        addScore();
                        db.updateScore(prevScore, score);
                        if (levelNum.equals(String.valueOf(Levels.MAX_LEVEL))) {
                            gameDialog(R.string.gameCompletedTitle, R.string.gameCompletedText);
                            db.addOne(score, "SCORES");
                            db.delete("CURRENT_SCORE");
                        }
                        else {
                            db.updateLevel(Integer.parseInt(levelNum));
                            gameDialog(R.string.levelCompletedTitle, R.string.levelCompletedText);
                        }
                        gameTimer.stopTimer();
                    }
                    else {
                        if (cell.isPath()) {
                            addScore();
                            scoreText.startAnimation(AnimationUtils.loadAnimation(Game.this, R.anim.score));
                            gameMsg.setText(R.string.lineCompleted);
                            gameMsg.setTextColor(colorMap.get(cell.getCurrentColor()));
                            gameMsg.setVisibility(View.INVISIBLE);
                            gameMsg.startAnimation(AnimationUtils.loadAnimation(Game.this, R.anim.game_msg));
                            Thread thread = new Thread();
                            try {
                                thread.start();
                                Thread.sleep(210);
                                gameMsg.setVisibility(View.INVISIBLE);
                            } catch (InterruptedException ignored) {}
                        } else {
                            gameDialog(R.string.gameOverTitle, R.string.noPossiblePath);
                            gameTimer.stopTimer();
                        }
                    }

                }
            }
            List<List<Integer>> possibleMoves = cell.getPossibleMoves();
            int coloredPossibleMoves = 0;
            if (!pathCompleted) {
                for (List<Integer> possibleMove : possibleMoves) {
                    if (boardCells[possibleMove.get(0)][possibleMove.get(1)].isColored())
                        coloredPossibleMoves++;
                    else if (boardCells[possibleMove.get(0)][possibleMove.get(1)].isDot() &&
                            !boardCells[possibleMove.get(0)][possibleMove.get(1)]
                                    .getColor().equals(cell.getCurrentColor()) &&
                            !boardCells[possibleMove.get(0)][possibleMove.get(1)].isColored())
                        coloredPossibleMoves++;
                }
                if (coloredPossibleMoves == possibleMoves.size()) {
                    gameDialog(R.string.gameOverTitle, R.string.noPossibleMove);
                    gameTimer.stopTimer();
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    public void loadLevel(ArrayList<ArrayList<String>> level) {
        for (ArrayList<String> dot : level) {
            String color = dot.get(0);
            String first = dot.get(1);
            String second = dot.get(2);
            int x = Integer.parseInt(first.split(",")[0]);
            int y = Integer.parseInt(first.split(",")[1]);
            boardCells[x][y].setIsDot();
            boardCells[x][y].setColor(color);
            dots.add(boardCells[x][y]);
            int xSecond = Integer.parseInt(second.split(",")[0]);
            int ySecond = Integer.parseInt(second.split(",")[1]);
            boardCells[xSecond][ySecond].setIsDot();
            boardCells[xSecond][ySecond].setColor(color);
            linesCompleted++;
            boardCells[x][y].setXYsecond(xSecond, ySecond);
            boardCells[xSecond][ySecond].setXYsecond(x, y);
        }
        BoardCell.setDots(dots);
        score = db.read("CURRENT_SCORE").get(0);
        prevScore = score;
        scoreText.setText(scoreText.getText() + "\n" + score);
        gameTimer.startTimer();
    }

    public void setHashMapColors() {
        colorMap.put("red", Color.parseColor("#ff6250"));
        colorMap.put("yellow", Color.parseColor("#fff996"));
        colorMap.put("purple", Color.parseColor("#c000ff"));
        colorMap.put("blue", Color.parseColor("#3789fe"));
        colorMap.put("green", Color.parseColor("#97d45f"));
        colorMap.put("pink", Color.parseColor("#f396ea"));
        colorMap.put("orange", Color.parseColor("#ffc94a"));
    }

    public void pause(View view) {
        gameTimer.pauseTimer();
        gameDialog(R.string.gamePausedTitle, 0);
    }

    @SuppressLint("SetTextI18n")
    public void addScore() {
        String[] time = timer.getText().toString().split(":");
        int currTimeInSecs = Integer.parseInt(time[0]) + Integer.parseInt(time[1]);
        int currTimeForCalculates = currTimeInSecs;
        currTimeForCalculates -= lineCompleteTime;
        try {
            score += 100 / currTimeForCalculates;
        } catch (ArithmeticException divide_by_zero) {
            score += 100;
        }
        String text = getResources().getString(R.string.score);
        scoreText.setText(text + "\n" + score);
        lineCompleteTime = currTimeInSecs;
    }
}
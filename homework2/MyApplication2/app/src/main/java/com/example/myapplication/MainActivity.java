package com.example.myapplication;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private final int[] containerArr = new int[9];
    private final ImageView[] containerViewArr = new ImageView[9];
    private final int[] dotArr = new int[9];
    private final ImageView[] dotViewArr = new ImageView[9];
    private ImageView backupDot;
    private float lastMotionX;
    private float lastMotionY;
    private int touchIndex;
    private static final int STATE_IDLE = 0;
    private static final int STATE_WAITING_DRAG = 1;
    private static final int STATE_HORIZONTAL_DRAG = 2;
    private static final int STATE_VERTICAL_DRAG = 3;
    private int state = STATE_IDLE;
    private int touchSlop;
    private boolean running = false; //计时状态
    private int seconds = 0;
    private int levelNum = 3;


    private TextView levelNameView;
    private ArrayAdapter<String> levelInfo;

    private int firstOpen = 0;

    //进入后台，暂停计时
    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    //重新进入app，开始计时
    @Override
    protected void onStart() {
        super.onStart();
        running = true;
    }

    //失去焦点(如分屏)，暂停计时
    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    //获得焦点,重新开始计时
    @Override
    protected void onResume() {
        super.onResume();
        running = true;
    }



    //计时方法
    private void runTime() {
        Thread thread = new Thread(() -> {
            TextView textView = findViewById(R.id.timeView);
            int hour;
            int minute;
            while(true) {
                while (running) {
                    seconds++;
                    hour = seconds / 3600 % 24;
                    minute = seconds % 3600 / 60;
                    @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hour, minute, seconds % 60);
                    textView.setText(time);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        dotViewArr[0] = findViewById(R.id.dot0);
        dotViewArr[1] = findViewById(R.id.dot1);
        dotViewArr[2] = findViewById(R.id.dot2);
        dotViewArr[3] = findViewById(R.id.dot3);
        dotViewArr[4] = findViewById(R.id.dot4);
        dotViewArr[5] = findViewById(R.id.dot5);
        dotViewArr[6] = findViewById(R.id.dot6);
        dotViewArr[7] = findViewById(R.id.dot7);
        dotViewArr[8] = findViewById(R.id.dot8);
        containerViewArr[0] = findViewById(R.id.container0);
        containerViewArr[1] = findViewById(R.id.container1);
        containerViewArr[2] = findViewById(R.id.container2);
        containerViewArr[3] = findViewById(R.id.container3);
        containerViewArr[4] = findViewById(R.id.container4);
        containerViewArr[5] = findViewById(R.id.container5);
        containerViewArr[6] = findViewById(R.id.container6);
        containerViewArr[7] = findViewById(R.id.container7);
        containerViewArr[8] = findViewById(R.id.container8);
        backupDot = findViewById(R.id.backup_dot);

        findViewById(R.id.btn_restart).setOnClickListener(v -> initLevel());

        Spinner levelScanner = (Spinner) findViewById(R.id.sp_city);
        levelNameView =(TextView) findViewById(R.id.level_name);
        levelInfo =new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item);
        levelInfo.add("选择关卡");
        levelInfo.add("第一关");
        levelInfo.add("第二关");
        levelInfo.add("第三关");
        levelInfo.add("第四关");
        levelInfo.add("第五关");
        levelInfo.add("第六关");
        levelInfo.add("第七关");
        levelInfo.add("第八关");
        levelInfo.add("第九关");
        levelScanner.setAdapter(levelInfo);
        levelScanner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("开始了");
                if(firstOpen == 0){
                    firstOpen++;
                }else {
                    if(position != 0){
                        levelNameView.setText(levelInfo.getItem(position));
                        levelNum = position;
                        initLevel();
                        saveData(levelNum);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                readData();
                levelNameView.setText(levelInfo.getItem(levelNum));
                initLevel();

            }
        });

        //读取管卡
        readData();

        //初始化管卡
        initLevel();
        //开始计时
        runTime();



    }

    //存储管卡
    public void saveData(int levelNum) {
        SharedPreferences.Editor editor=getSharedPreferences("levelNumData",MODE_PRIVATE).edit(); //通过getSharedPreferences()方法制定SharedPreferences的文件名为data，并得到了SharedPreferences.Editor对象。
        editor.putInt("levelNum", levelNum); //添加了整形数据
        editor.apply(); //调用commit()方法进行提交，从而完成了数据存储的操作。
    }

    //读取管卡
    public void readData() {
        SharedPreferences pref = getSharedPreferences("levelNumData",MODE_PRIVATE); //设置要读取的文件存储
        levelNum = pref.getInt("levelNum",3);//读取关卡数目
    }




    /**
     * 初始化关卡
     * （可以在这里提高一下关卡难度，或者修改为多关卡）
     */
    private void initLevel() {
        running = true;
        seconds = 0;
        @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", 0, 0, 0);
        TextView textViewOne = findViewById(R.id.timeView);
        textViewOne.setText(time);
        Arrays.fill(containerArr, 0);
        Arrays.fill(dotArr, 0);
        List<Integer> list = new ArrayList<>();
        for(int i = 0;i < 9;i++){
            list.add(i);
        }
        for(int i = 0;i < levelNum;i++){
            int index = (int) (Math.random() * list.size());
            containerArr[list.get(index)] = 1;
            list.remove(index);

        }
        list.clear();
        for(int i = 0;i < 9;i++){
            list.add(i);
        }
        for(int i = 0;i < levelNum;i++){
            int index = (int) (Math.random() * list.size());
            dotArr[list.get(index)] = 1;
            list.remove(index);

        }

        refreshView();
        levelNameView.setText(levelInfo.getItem(levelNum));

    }

    /**
     * 根据当前关卡状态刷新页面视图显示
     */
    private void refreshView() {
        for (int i = 0; i < 9; i++) {
            if (containerArr[i] == 1) {
                containerViewArr[i].setImageResource(R.drawable.shape_ring_white);
            } else {
                containerViewArr[i].setImageResource(0);
            }
        }
        for (int i = 0; i < 9; i++) {
            if (dotArr[i] == 1) {
                dotViewArr[i].setImageResource(R.drawable.shape_dot_white);
            } else {
                dotViewArr[i].setImageResource(R.drawable.shape_dot_black);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // 手指按下
            case MotionEvent.ACTION_DOWN: {
                state = STATE_IDLE;
                touchIndex = -1;
                lastMotionX = event.getRawX();
                lastMotionY = event.getRawY();
                for (int i = 0; i < 9; i++) {
                    ImageView dotView = dotViewArr[i];
                    if (isPointInView(dotView, lastMotionX, lastMotionY)) {
                        touchIndex = i;
                        state = STATE_WAITING_DRAG;
                        break;
                    }
                }
                break;
            }
            // 手指拖动
            case MotionEvent.ACTION_MOVE: {
                float deltaX = event.getRawX() - lastMotionX;
                float deltaY = event.getRawY() - lastMotionY;
                if (state == STATE_WAITING_DRAG) {
                    // 超过一定距离才认为有效滑动
                    if (Math.abs(deltaX) >= touchSlop || Math.abs(deltaY) >= touchSlop) {
                        state = Math.abs(deltaX) > Math.abs(deltaY) ? STATE_HORIZONTAL_DRAG : STATE_VERTICAL_DRAG;
                    }
                }
                if (state == STATE_HORIZONTAL_DRAG) {
                    // 横向滑动
                    horizontalDragging(touchIndex / 3, deltaX);
                } else if (state == STATE_VERTICAL_DRAG) {
                    // 纵向滑动
                    verticalDragging(touchIndex % 3, deltaY);
                }
                lastMotionX = event.getRawX();
                lastMotionY = event.getRawY();
                break;
            }
            // 手指抬起
            case MotionEvent.ACTION_UP: {
                if (state == STATE_HORIZONTAL_DRAG) {
                    // 横向滑动
                    horizontalDragEnd(touchIndex / 3);
                } else if (state == STATE_VERTICAL_DRAG) {
                    // 纵向滑动
                    verticalDragEnd(touchIndex % 3);
                }
                touchIndex = -1;
                state = STATE_IDLE;
                break;
            }
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断一个指定点是否在指定View的区域
     */
    private boolean isPointInView(View view, float x, float y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    /**
     * 横向滑动中，实现循环滑动
     */
    private void horizontalDragging(int rowIndex, float delta) {
        ImageView leftDot = dotViewArr[rowIndex * 3];
        ImageView middleDot = dotViewArr[rowIndex * 3 + 1];
        ImageView rightDot = dotViewArr[rowIndex * 3 + 2];
        float translationX = getValidTranslation(leftDot.getTranslationX() + delta);
        leftDot.setTranslationX(translationX);
        middleDot.setTranslationX(translationX);
        rightDot.setTranslationX(translationX);
        if (backupDot.getVisibility() != View.VISIBLE) {
            backupDot.setVisibility(View.VISIBLE);
        }
        if (translationX > 0) {
            // 向右滑，backup在左边出现
            backupDot.setTranslationX(translationX - backupDot.getWidth());
            backupDot.setImageDrawable(rightDot.getDrawable());
        } else {
            // 向左滑，backup在右边出现
            backupDot.setTranslationX(backupDot.getWidth() * 3 + translationX);
            backupDot.setImageDrawable(leftDot.getDrawable());
        }
        backupDot.setTranslationY(backupDot.getHeight() * rowIndex);
    }

    /**
     * 横向滑动结束，判断滑动距离是否足够触发移动。如果触发移动后，刷新页面展示，并判断关卡是否通过
     */
    private void horizontalDragEnd(int rowIndex) {
        ImageView leftDot = dotViewArr[rowIndex * 3];
        ImageView middleDot = dotViewArr[rowIndex * 3 + 1];
        ImageView rightDot = dotViewArr[rowIndex * 3 + 2];
        float targetTranslationX = leftDot.getTranslationX();
        leftDot.setTranslationX(0.0f);
        middleDot.setTranslationX(0.0f);
        rightDot.setTranslationX(0.0f);
        backupDot.setVisibility(View.INVISIBLE);
        backupDot.setTranslationX(0.0f);
        backupDot.setTranslationY(0.0f);
        if (targetTranslationX > backupDot.getWidth() * 1.0f / 2) {
            // 最终向右移动一格
            int tmp = dotArr[rowIndex * 3 + 2];
            dotArr[rowIndex * 3 + 2] = dotArr[rowIndex * 3 + 1];
            dotArr[rowIndex * 3 + 1] = dotArr[rowIndex * 3];
            dotArr[rowIndex * 3] = tmp;
            refreshView();
            judgeLevelPass();
        } else if (targetTranslationX < backupDot.getWidth() * -1.0f / 2) {
            // 最终向左移动一格
            int tmp = dotArr[rowIndex * 3];
            dotArr[rowIndex * 3] = dotArr[rowIndex * 3 + 1];
            dotArr[rowIndex * 3 + 1] = dotArr[rowIndex * 3 + 2];
            dotArr[rowIndex * 3 + 2] = tmp;
            refreshView();
            judgeLevelPass();
        }
    }

    /**
     * 纵向滑动中，实现循环滑动
     */
    private void verticalDragging(int columnIndex, float delta) {
        ImageView topDot = dotViewArr[columnIndex];
        ImageView middleDot = dotViewArr[columnIndex + 3];
        ImageView bottomDot = dotViewArr[columnIndex + 6];
        float translationY = getValidTranslation(topDot.getTranslationY() + delta);
        topDot.setTranslationY(translationY);
        middleDot.setTranslationY(translationY);
        bottomDot.setTranslationY(translationY);
        if (backupDot.getVisibility() != View.VISIBLE) {
            backupDot.setVisibility(View.VISIBLE);
        }
        backupDot.setTranslationX(backupDot.getWidth() * columnIndex);
        if (translationY > 0) {
            // 向下滑，backup在上边出现
            backupDot.setTranslationY(translationY - backupDot.getHeight());
            backupDot.setImageDrawable(bottomDot.getDrawable());
        } else {
            // 向上滑，backup在下边出现
            backupDot.setTranslationY(backupDot.getHeight() * 3 + translationY);
            backupDot.setImageDrawable(topDot.getDrawable());
        }
    }

    /**
     * 纵向滑动结束，判断滑动距离是否足够触发移动。如果触发移动后，刷新页面展示，并判断关卡是否通过
     */
    private void verticalDragEnd(int columnIndex) {
        ImageView topDot = dotViewArr[columnIndex];
        ImageView middleDot = dotViewArr[columnIndex + 3];
        ImageView bottomDot = dotViewArr[columnIndex + 6];
        float targetTranslationY = topDot.getTranslationY();
        topDot.setTranslationY(0.0f);
        middleDot.setTranslationY(0.0f);
        bottomDot.setTranslationY(0.0f);
        backupDot.setVisibility(View.INVISIBLE);
        backupDot.setTranslationX(0.0f);
        backupDot.setTranslationY(0.0f);
        if (targetTranslationY > backupDot.getWidth() * 1.0f / 2) {
            // 最终向下移动一格
            int tmp = dotArr[columnIndex + 3 * 2];
            dotArr[columnIndex + 3 * 2] = dotArr[columnIndex + 3];
            dotArr[columnIndex + 3] = dotArr[columnIndex];
            dotArr[columnIndex] = tmp;
            refreshView();
            judgeLevelPass();
        } else if (targetTranslationY < backupDot.getWidth() * -1.0f / 2) {
            // 最终向上移动一格
            int tmp = dotArr[columnIndex];
            dotArr[columnIndex] = dotArr[columnIndex + 3];
            dotArr[columnIndex + 3] = dotArr[columnIndex + 3 * 2];
            dotArr[columnIndex + 3 * 2] = tmp;
            refreshView();
            judgeLevelPass();
        }
    }

    /**
     * 限制一次只能滑动一格
     */
    private float getValidTranslation(float translation) {
        return Math.max(backupDot.getWidth() * -1, Math.min(translation, backupDot.getWidth()));
    }

    /**
     * 判断关卡是否通过，如果通过则弹窗提示
     */
    @SuppressLint("DefaultLocale")
    private void judgeLevelPass() {
        for (int i = 0; i < 9; i++) {
            if (dotArr[i] != containerArr[i]) {
                return;
            }
        }
        if(levelNum < 9){
            new AlertDialog.Builder(this).setTitle("恭喜过关").setMessage("用时: "+String.format("%02d:%02d:%02d", seconds / 3600 % 24, seconds % 3600 / 60, seconds % 60)).create().show();
            saveData(++levelNum);
        }else{
            levelNum = 1;
            new AlertDialog.Builder(this).setTitle("恭喜您，全部通关！轮回！").setMessage("用时: "+String.format("%02d:%02d:%02d", seconds / 3600 % 24, seconds % 3600 / 60, seconds % 60)).create().show();
            saveData(levelNum);
        }
        levelNameView.setText(levelInfo.getItem(levelNum));
        initLevel();

    }
}
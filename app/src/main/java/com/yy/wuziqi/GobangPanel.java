package com.yy.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.yy.wuziqi.util.CheckWin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 13160677911 on 2016-12-26.
 */

public class GobangPanel extends View {
    private int mPanelWidth;  //棋盘的宽度
    private float mLineHeight;  //棋盘的高度
    private int MAX_LINE = 15;


    private Paint mPaint = new Paint();

    //棋子相关
    private Bitmap mWhitePiece;  //白色棋子
    private Bitmap mBlackPiece;  //黑色棋子

    //棋子大小比例
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    //保存用户点击的坐标
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    //白棋先手，当前轮到白棋
    private boolean mIsWhite = true;

    //游戏结束
    private boolean mIsGameOver;
    //确定哪一个是赢家
    private boolean mIsWhiteWinner;

    //保存坐标
    private Point mPoint;


    //构造方法
    public GobangPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundColor(0x5500ffff);
        init();
    }

    //初始化Paint
    private void init() {
        mPaint.setColor(0x88000000);  //灰色
        mPaint.setAntiAlias(true);   //锯齿
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);  //风格

        //读取棋子图片
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_b1);
    }

    /**
     * View的测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //精确值 match_parent
        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;  //宽度由高度决定
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;   //宽度由宽度决定
        }

        setMeasuredDimension(width, width);
    }
    //棋盘高度，绘制行数

    /**
     * 当View宽高确定后返回值，产生回调(跟尺寸相关)
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    //View的触摸
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //游戏结束时不能再touch
        if (mIsGameOver) {
            return false;
        }

        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x, y);

            //contains比较值是否相同
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }

            if (mIsWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            //请求重新绘制
            invalidate();
            mIsWhite = !mIsWhite;
        }
        if (action == MotionEvent.ACTION_DOWN) {  //表明态度
            return true;
        }
        return super.onTouchEvent(event);
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    /**
     * View的绘制
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    /**
     * 五子棋逻辑
     */
    private void checkGameOver() {
        //横向，竖向，纵向判断是否有5连珠
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);
        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;
            String text = mIsWhiteWinner ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            boolean win = CheckWin.checkHorizontal(x, y, points);
            if (win) {  //如果横向5连成功
                return true;
            }
            win = CheckWin.checkVertical(x, y, points);
            if (win) {  //如果纵向5连成功
                return true;
            }
            win = CheckWin.checkLeftDiagonal(x, y, points);
            if (win) {  //如果左斜5连成功
                return true;
            }
            win = CheckWin.checkRightDiagonal(x, y, points);
            if (win) {  //如果右斜5连成功
                return true;
            }
        }
        return false;
    }


    /**
     * 棋子的绘制
     *
     * @param canvas
     */
    private void drawPieces(Canvas canvas) {
        //绘制白棋
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
        //绘制黑棋
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
    }

    /**
     * 棋盘的绘制
     *
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {

        //绘制棋盘
        int w = mPanelWidth;
        float lineHeight = mLineHeight;
        //画横线
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);
            int startY = (int) ((0.5 + i) * lineHeight);
            int endY = (int) ((0.5 + i) * lineHeight);
            //（横向）起点X坐标，起点Y坐标，终点X坐标，终点Y坐标
            canvas.drawLine(startX, startY, endX, endY, mPaint);
        }
        //画纵线
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) ((0.5 + i) * lineHeight);
            int endX = (int) ((0.5 + i) * lineHeight);
            int startY = (int) (lineHeight / 2);
            int endY = (int) (w - lineHeight / 2);
            //（纵向）起点X坐标，起点Y坐标，终点X坐标，终点Y坐标
            canvas.drawLine(startX, startY, endX, endY, mPaint);
        }
    }

    //重玩
    public void reStart() {
        //清空数据
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        //重新绘制View
        invalidate();
    }

    //悔棋
    public void Return() {
        if (mIsWhite) { //当前该白方下棋，意味着黑方式上一个下棋者，为黑方悔棋
            if (mBlackArray != null && !mBlackArray.isEmpty()) {
                mBlackArray.remove(mBlackArray.size() - 1);
                if (mWhiteArray != null && !mWhiteArray.isEmpty()) {
                    mPoint = mWhiteArray.get(mWhiteArray.size() - 1);
                }
                mIsWhite = !mIsWhite;
                invalidate();
                return;
            }
        } else if (!mIsWhite) {
            if (mWhiteArray != null && !mWhiteArray.isEmpty()) {
                mWhiteArray.remove(mWhiteArray.size() - 1);
                if (mBlackArray != null && !mBlackArray.isEmpty()) {
                    mPoint = mBlackArray.get(mBlackArray.size() - 1);
                }
                mIsWhite = !mIsWhite;
                invalidate();
                return;
            }
        }
    }


    //棋盘的恢复
    //需要存储的数据（白子，黑子，两个集合，当前游戏是否结束）
    //如果View需要存储与恢复，view组件在布局文件中的id一定要写
    /*View的存储与恢复*/

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";
    private static final String INSTANCE_mIsWhite = "instance_mIsWhite";

    /**
     * View的存储
     *
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());  //默认View的存储

        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);  //存储是否游戏结束
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);   //存储白棋的记录
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);  //存储黑棋的记录
        bundle.putBoolean(INSTANCE_mIsWhite, mIsWhite);  //存储该谁下棋
        return bundle;
    }

    /**
     * View的恢复
     *
     * @param state
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {  //是否为自己设置的类型
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);  //获取是否游戏结束
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);   //获取白棋的记录
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);   //获取黑棋的记录
            mIsWhite = bundle.getBoolean(INSTANCE_mIsWhite);   //获取该谁下棋
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

}

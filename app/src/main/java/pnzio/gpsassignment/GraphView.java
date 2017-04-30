package pnzio.gpsassignment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;


public class GraphView extends View {

    private Paint paint;
    private Context ctxt;
    private Integer[] GraphData;
    private Integer biggestValueInArray;
    private int textSize = 50;
    private int numberOfYShown;
    private int distance;
    private int maxYAxisWidth;

    GraphView(Context context,AttributeSet attrs) {
        super(context, attrs);
        ctxt = context;
        paint = new Paint();
        init();
    }
    private void init() {
        distance = 50;
    }
    public void setCoordinates(Integer[] data) {
        GraphData = data;
        numberOfYShown = 20;
        biggestValueInArray = 0;
        for(int i=0;i<GraphData.length;i++) {
            if(biggestValueInArray < GraphData[i])
                biggestValueInArray = GraphData[i];
        }
        setTextWidth(data);
        invalidate();
    }
    public Integer getMaxValueOfData() {
        return biggestValueInArray;
    }
    private void setTextWidth(Integer[] data) {
        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(textSize);
        for (int i=0;i< GraphData.length;i++) {
            int currentTextWidth =(int) paint.measureText(Integer.toString(data[i]));
            if (maxYAxisWidth < currentTextWidth)
                maxYAxisWidth = currentTextWidth;
        }
    }

    public void setXdetails(Point origin, String label, int centerX, Canvas canvas) {
        Rect bounds = new Rect();
        paint.getTextBounds(label, 0, label.length(), bounds);
        int y = origin.y + distance;
        int x = centerX - bounds.width() / 2;
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT);
        canvas.drawText(label, x, y, paint);
    }
    public void setYdetails(Point origin, int graphHeight, Canvas canvas) {
        Integer max = (int)getMaxValueOfData();
        Integer interval = graphHeight / numberOfYShown;
        Integer dataInterval = max / numberOfYShown;
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(textSize);

        for (int i=0;i< numberOfYShown;i++) {
            String string = "" +Math.round(max);
            Rect bounds = new Rect();
            paint.getTextBounds(string, 0, string.length(), bounds);
            int y = (int) ((origin.y - graphHeight) + interval *i);
            canvas.drawLine(origin.x - (distance >> 1), y, origin.x, y, paint);
            y = y + (bounds.height() >> 1);
            canvas.drawText(string, origin.x - bounds.width() - distance, y, paint);
            max = max - dataInterval;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int graphHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        int graphWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        Point origin = new Point(maxYAxisWidth + distance,getHeight() - getPaddingBottom());
        paint.setColor(ContextCompat.getColor(ctxt, R.color.darkRed));
        paint.setStrokeWidth(10);
        canvas.drawLine(origin.x, origin.y, origin.x, origin.y - (graphHeight), paint);
        canvas.drawLine(origin.x, origin.y, origin.x + graphWidth -(maxYAxisWidth), origin.y, paint);
        int width = (graphWidth - maxYAxisWidth) / ((GraphData.length << 1) + 1);
        int x1, x2, y1, y2;
        Integer maxValue = getMaxValueOfData();
        for (int i=0; i<GraphData.length;i++) {
            x1 = origin.x + ((i << 1) + 1) * width;
            x2 = origin.x + ((i << 1) + 2) * width;
            int barHeight = (int) ((graphHeight) *
                    GraphData[i] / maxValue);
            y1 = origin.y - barHeight;
            y2 = origin.y;
            canvas.drawRect(x1, y1, x2, y2, paint);
            if(GraphData.length<=6)
                setXdetails(origin, "" + GraphData[i], x1 + (x2 - x1) / 2, canvas);
        }
        setYdetails(origin, (graphHeight), canvas);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        if(widthMeasureSpec>heightMeasureSpec)
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        else
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
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

 /*
    Custom View that displays a Graph with each bar on the graph being a kilometer
    Gets data from UserActivity passing an array. If Array is empty, empty graph is displayed...
 */

public class GraphView extends View {

    // Variables
    private Paint paint;
    private Context ctxt;
    private int[] GraphData;
    private int biggestValueInArray;
    private int textSize;
    private int maxYAxisWidth;

    // Constructor
    GraphView(Context context,AttributeSet attrs) {
        super(context, attrs);
        textSize = 50;
        biggestValueInArray = 0;
        ctxt = context;
        paint = new Paint();
    }
    /* UserActivity calls this function to pass the array of seconds.
        Sets Text  Width and invalidates.
     */
    void setCoordinates(int[] data) {
        GraphData = data;
        for(int i=0;i<GraphData.length;i++) {
            if(biggestValueInArray < GraphData[i])
                biggestValueInArray = GraphData[i];
        }
        setTextWidth(data);
        invalidate();
    }
    /*
    Returns biggest value in the array, as that value changes after setCoordinates is called.
     */
    int getMaxValueOfData() {
        return biggestValueInArray;
    }
    void setTextWidth(int[] data) {
        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(textSize);
        for (int i=0;i< GraphData.length;i++) {
            int currentTextWidth =(int) paint.measureText(Integer.toString(data[i]));
            if (maxYAxisWidth < currentTextWidth)
                maxYAxisWidth = currentTextWidth;
        }
    }
    /*
    Draws the graph itself.
    Draws X and Y axis, then draws bars.

    Draws Small Lines on Y axis that correspond to bars. Add labels to them.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int graphHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        int graphWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        Point origin = new Point(maxYAxisWidth + 50,getHeight() - getPaddingBottom());
        paint.setColor(ContextCompat.getColor(ctxt, R.color.darkRed));
        paint.setStrokeWidth(10);
        canvas.drawLine(origin.x, origin.y, origin.x, origin.y - (graphHeight), paint);
        canvas.drawLine(origin.x, origin.y, origin.x + graphWidth -(maxYAxisWidth), origin.y, paint);
        int width = (graphWidth - maxYAxisWidth) / ((GraphData.length << 1) + 1);
        int x1, x2, y1, y2;

        int maxValue = getMaxValueOfData();
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT);


        for (int i=0; i<GraphData.length;i++) {
            String string = "" +Math.round(GraphData[i]);
            x1 = origin.x + ((i << 1) + 1) * width;
            x2 = origin.x + ((i << 1) + 2) * width;
            int barHeight = (int) ((graphHeight) *
                    GraphData[i] / maxValue);
            y1 = origin.y - barHeight;
            y2 = origin.y;
            canvas.drawRect(x1, y1, x2, y2, paint);
            canvas.drawLine(origin.x-20, y1, origin.x+20, y1, paint);
            Rect bounds = new Rect();
            paint.getTextBounds(string, 0, string.length(), bounds);
            canvas.drawText(string,origin.x-bounds.width()-50, y1, paint);



        }
    }
    /*
    Makes sure that Custom View is square in size by getting parents width and height and
    making changes accordingly.
     */
    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        if(widthMeasureSpec>heightMeasureSpec)
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        else
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
package d3kod.thehunt;

import android.graphics.PointF;

public interface Tool {

	boolean handleTouch(int action, PointF location);

	void update();

}

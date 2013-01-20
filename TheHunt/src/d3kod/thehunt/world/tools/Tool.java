package d3kod.thehunt.world.tools;

import android.graphics.PointF;

public interface Tool {

	boolean handleTouch(int action, PointF location);

	void update();
	
	void stop(PointF location);

}

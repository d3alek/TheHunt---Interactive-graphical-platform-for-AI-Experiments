package com.primalpond.hunt.world.tools;

import android.graphics.PointF;

public interface Tool {

	boolean handleTouch(int action, PointF location);

	void update();
	
	void stop(PointF location);

	boolean isActive();

	boolean didAction();
}

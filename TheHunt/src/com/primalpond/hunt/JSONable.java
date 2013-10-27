package com.primalpond.hunt;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONable {
	public JSONObject toJSON() throws JSONException;
}

package alabno.wserver;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JsonParser {

	private JSONObject jobject = null;

	public JsonParser(String input) {
		try {
			Object parsed = JSONValue.parse(input);
			jobject = (JSONObject) parsed;
		} catch (Exception e) {
			System.out.println("Could not parse json correctly");
			jobject = null;
		}
	}

	public JsonParser(JSONObject jobject) {
		this.jobject = jobject;
	}

	public String getString(String key) {
		Object tmp = jobject.get(key);
		if (tmp instanceof String) {
			return (String) tmp;
		} else {
			return null;
		}
	}

	public int getInt(String key) {
		Object tmp = jobject.get(key);
		if (tmp instanceof Integer) {
			return (Integer) tmp;
		} else if (tmp instanceof Double) {
			Double theDouble = (Double) tmp;
			return theDouble.intValue();
		} else if (tmp instanceof Long) {
			Long theLong = (Long) tmp;
			return theLong.intValue();
		} else {
			throw new RuntimeException("Could not parse a number");
		}
	}

	public JSONArray getArray(String key) {
		Object tmp = jobject.get(key);
		if (tmp instanceof JSONArray) {
			return (JSONArray) tmp;
		} else {
			return null;
		}
	}

	public JsonArrayParser getArrayParser(String key) {
		Object tmp = jobject.get(key);
		if (tmp instanceof JSONArray) {
			return new JsonArrayParser((JSONArray)tmp);
		} else {
			return null;
		}
	}
	
	public void putString(String key, String value) {
	    jobject.put(key, value);
	}
	
    public void putArray(String key, JSONArray value) {
        jobject.put(key, value);
    }
    
    public void putDouble(String key, Double value) {
        jobject.put(key, value);
    }
	
	public boolean isOk()
	{
		return jobject != null;
	}
	
	public JSONObject getObject() {
	    return jobject;
	}



}

package cn.ac.ia.directtrans.json;

import java.util.ArrayList;

public class JsonData extends Json {

	public String function = "";
	public String json = "";

	public ArrayList<JsonData> items = new ArrayList<JsonData>();

	public JsonData get(int nIndex) {
		try {
			if (items == null || items.size() <= 0) {
				return null;
			}
			if (nIndex >= 0 && nIndex < items.size()) {
				JsonData mJsonData = items.get(nIndex);
				return mJsonData;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int count() {

		return items.size();
	}

	public void add(JsonData data) {

		items.add(data);
	}

	public void add(String cmd, Object obj) {

		JsonData data = new JsonData();
		data.function = cmd;
		data.json = Json.toJson(obj);
		items.add(data);
	}

	public JsonData() {

	}

	public <T> T toClass(Class<T> classOfT) {

		return Json.fromJson(json, classOfT);
	}

	public JsonData(String cmd2, String json2) {
		// TODO Auto-generated constructor stub
		this.function = cmd2;
		this.json = json2;
	}

}

package Model;

import java.io.FileInputStream;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import database.dbFilter;
import security.codec;

public class CommonModel {

	/**
	 * 参数解码
	 * 
	 * @param param
	 *            按 base64 + 特殊格式 顺序 编码后的参数
	 * @return
	 */
	public String dencode(String param) {
		param = codec.DecodeHtmlTag(param);
		param = codec.decodebase64(param);
		return param;
	}

	/**
	 * 整合参数，将JSONObject类型的参数封装成JSONArray类型,包含额外参数wbid
	 * 
	 * @param object
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public JSONArray buildCondAddWbid(String Info, String currentWeb) {
//		JSONArray condArray = null;
//		if (!StringHelper.InvaildString(currentWeb)) {
//			JSONObject object = JSONObject.toJSON(Info);
//			if (object != null && object.size() > 0) {
//				object.put("wbid", "currentWeb");
//				condArray = buildCond(object);
//			}
//		}
//		return condArray;
//	}

	/**
	 * 整合参数，将JSONObject类型的参数封装成JSONArray类型
	 * 
	 * @param object
	 * @return
	 */
	public JSONArray buildCond(String Info) {
		String key;
		Object value;
		JSONArray condArray = null;
		JSONObject object = JSONObject.toJSON(Info);
		dbFilter filter = new dbFilter();
		if (object != null && object.size() > 0) {
			for (Object object2 : object.keySet()) {
				key = object2.toString();
				value = object.get(key);
				filter.eq(key, value);
			}
			condArray = filter.build();
		}
		return condArray;
	}

	public String getImageUri(String imageURL) {
		int i = 0;
		if (imageURL.contains("File//upload")) {
			i = imageURL.toLowerCase().indexOf("file//upload");
			imageURL = "\\" + imageURL.substring(i);
		}
		if (imageURL.contains("File\\upload")) {
			i = imageURL.toLowerCase().indexOf("file\\upload");
			imageURL = "\\" + imageURL.substring(i);
		}
		if (imageURL.contains("File/upload")) {
			i = imageURL.toLowerCase().indexOf("file/upload");
			imageURL = "\\" + imageURL.substring(i);
		}
		return imageURL;
	}

	public String getFile(int i) {
		return "http://" + getAppIp("file").split("/")[i];
	}

	private String getAppIp(String key) {
		String value = "";
		try {
			Properties pro = new Properties();
			pro.load(new FileInputStream("URLConfig.properties"));
			value = pro.getProperty(key);
		} catch (Exception e) {
			value = "";
		}
		return value;
	}
}

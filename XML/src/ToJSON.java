import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ToJSON {

	private JSONObject obj = new JSONObject();

	@SuppressWarnings("unchecked")
	public ToJSON(ArrayList<String> al) throws IOException {
		JSONArray children = new JSONArray();
		JSONObject data = new JSONObject();
				
		for (int i = 0; i < al.size() - 1; i++) {
			data.put("test" + i, al.get(i));
			
			if (i % 8 == 7) {
				children.add(data);
				data = new JSONObject();
			}
		}

		obj.put("children", children);

		try (FileWriter file = new FileWriter("D:/Eric Goncalves/Desktop/xml/merged.json")) {
			file.write(obj.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + obj);
		}
	}
}

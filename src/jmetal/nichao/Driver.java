package jmetal.nichao;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Driver {

	public static HashMap<String, String> infoMap = new HashMap<>();


	public static void main(String[] args) {

		//获取当前项目的配置文件
		Properties prop = new Properties();
		String enableDataset = null;
		try {

			prop.load(new FileInputStream("config.properties"));
			enableDataset = prop.get("enableDataset") + "";

			if (enableDataset.trim().equals("Relink")) {
				infoMap.put("experimentName_", prop.get("experimentNameRelink").toString());
			} else if (enableDataset.trim().equals("PROMISE")) {
				infoMap.put("experimentName_", prop.get("experimentNamePROMISE").toString());
			} else {
				throw new RuntimeException("enableDataset配置不正确，必须为Relink或者为PROMISE");
			}

			infoMap.put("independentRuns_", prop.get("independentRuns").toString());
			infoMap.put("numberOfThreads", prop.get("numberOfThreads").toString());
			infoMap.put("randomIndex",prop.get("randomIndex").toString());
			infoMap.put("basepath",prop.get("basepath").toString());
			//将实验结果的根目录加上随机因子值以表示不同轮次的独立运行结果
			infoMap.put("experimentBaseDirectory_", prop.get("experimentBaseDirectory").toString()+"-random-"+infoMap.get("randomIndex"));
			System.out.println("----------------配置信息如下------------------------");
			for (Map.Entry<String, String> entry : infoMap.entrySet()) {
				System.out.println(entry.getKey() + "：\t" + entry.getValue());
			}


			System.out.println("---------------------------------------------------");

			if (enableDataset.trim().equals("Relink")) {
				MyDriverRelink myDriverRelink = new MyDriverRelink();
				myDriverRelink.main(null);

			} else if (enableDataset.trim().equals("PROMISE")) {
				MyDriverPromise myDriverPromise = new MyDriverPromise();
				myDriverPromise.main(null);
			} else {
				throw new RuntimeException("enableDataset配置不正确，必须为Relink或者为PROMISE");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


	}
}

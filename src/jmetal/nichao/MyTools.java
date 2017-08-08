package jmetal.nichao;

import jmetal.encodings.variable.Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by ChaoNi on 2017/7/29.
 */

public class MyTools {

    /**
     * 问题名称到数据集名称的映射，比如Apache->Relink
     */
    private static HashMap<String,String> problemToDataset=null;

    private static HashMap<String,String> problemToArffName=null;

    /**
     * SafeKNN/ApacheKNN.arff等文件的根路径
     */
    private static String datasetBase="";

    //直接在静态代码块中初始化数据
    static {

        problemToDataset=new HashMap<>();

        //Relink数据集
        problemToDataset.put("ApacheKNN","Relink");
        problemToDataset.put("ApacheLR","Relink");
        problemToDataset.put("ApacheJ48","Relink");
        problemToDataset.put("ApacheNB","Relink");


        problemToDataset.put("SafeKNN","Relink");
        problemToDataset.put("SafeLR","Relink");
        problemToDataset.put("SafeJ48","Relink");
        problemToDataset.put("SafeNB","Relink");

        problemToDataset.put("ZxingKNN","Relink");
        problemToDataset.put("ZxingLR","Relink");
        problemToDataset.put("ZxingJ48","Relink");
        problemToDataset.put("ZxingNB","Relink");


        //problemToarff文件
        problemToArffName=new HashMap<>();

        problemToArffName.put("ApacheKNN","Apache");
        problemToArffName.put("ApacheLR","Apache");
        problemToArffName.put("ApacheJ48","Apache");
        problemToArffName.put("ApacheNB","Apache");


        problemToArffName.put("SafeKNN","Safe");
        problemToArffName.put("SafeLR","Safe");
        problemToArffName.put("SafeJ48","Safe");
        problemToArffName.put("SafeNB","Safe");

        problemToArffName.put("ZxingKNN","Zxing");
        problemToArffName.put("ZxingLR","Zxing");
        problemToArffName.put("ZxingJ48","Zxing");
        problemToArffName.put("ZxingNB","Zxing");


        Properties prop = new Properties();
        try {
            prop.load(MyTools.class.getClassLoader()
                    .getResourceAsStream("config.properties"));
           datasetBase =prop.get("basepath")+"";

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * <font color='red'>问题名称到数据集名称的映射，比如Apache->Relink</font>
     */
    public static String problemToDatasetName(String problem){
        if(problemToDataset==null||!problemToDataset.containsKey(problem)){
            System.err.println("未找到"+problem+"到对应数据集的完整路径");
            System.exit(1);
        }
        return  problemToDataset.get(problem);

    }



    /**
     * 根据问题，找到对应的项目数据全路径如Apache问题对应的数据是Apache.arff
     * @param problem
     * @return
     */
    public static String problemDataFullPath(String problem){
        if(!problemToArffName.containsKey(problem)){
            System.err.println("未找到"+problem+"到对应数据集的完整路径");
            System.exit(1);
        }

       //根据基准路径 数据集名称 问题名称得到对应的数据集
        String fullPath=datasetBase+"/"+problemToDataset.get(problem)+"/"+problemToArffName.get(problem)+".arff";

        return fullPath;
    }


    /**
     * 将ArrayList<Integer>转化成int[]
     * @param arrayList
     * @return
     */
    public static int[] toIntArray(ArrayList<Integer> arrayList){
        if(arrayList==null||arrayList.size()==0)
            return null;

        int[] arr=new int[arrayList.size()];
        for(int i=0;i<arr.length;i++){
            arr[i]=arrayList.get(i);
        }
        return arr;
    }


    public  static void main(String[] args){

        System.out.println("测试运行读取配置文件是否成功....");
        System.out.println(datasetBase);
        System.out.println(MyTools.problemToDatasetName("ApacheKNN"));
        System.out.println(MyTools.problemDataFullPath("ApacheKNN"));


    }



}

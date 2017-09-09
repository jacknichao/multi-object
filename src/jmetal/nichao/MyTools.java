package jmetal.nichao;

import jmetal.encodings.variable.Int;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        //Promise数据集

        problemToDataset.put("ant17KNN","PROMISE");
        problemToDataset.put("ant17LR","PROMISE");
        problemToDataset.put("ant17J48","PROMISE");
        problemToDataset.put("ant17NB","PROMISE");


        problemToDataset.put("ivy20KNN","PROMISE");
        problemToDataset.put("ivy20LR","PROMISE");
        problemToDataset.put("ivy20J48","PROMISE");
        problemToDataset.put("ivy20NB","PROMISE");


        problemToDataset.put("lucene24KNN","PROMISE");
        problemToDataset.put("lucene24LR","PROMISE");
        problemToDataset.put("lucene24J48","PROMISE");
        problemToDataset.put("lucene24NB","PROMISE");

        problemToDataset.put("synapse12KNN","PROMISE");
        problemToDataset.put("synapse12LR","PROMISE");
        problemToDataset.put("synapse12J48","PROMISE");
        problemToDataset.put("synapse12NB","PROMISE");

        problemToDataset.put("xalan26KNN","PROMISE");
        problemToDataset.put("xalan26LR","PROMISE");
        problemToDataset.put("xalan26J48","PROMISE");
        problemToDataset.put("xalan26NB","PROMISE");


        problemToDataset.put("camel16KNN","PROMISE");
        problemToDataset.put("camel16LR","PROMISE");
        problemToDataset.put("camel16J48","PROMISE");
        problemToDataset.put("camel16NB","PROMISE");

        problemToDataset.put("jedit40KNN","PROMISE");
        problemToDataset.put("jedit40LR","PROMISE");
        problemToDataset.put("jedit40J48","PROMISE");
        problemToDataset.put("jedit40NB","PROMISE");

        problemToDataset.put("poi30KNN","PROMISE");
        problemToDataset.put("poi30LR","PROMISE");
        problemToDataset.put("poi30J48","PROMISE");
        problemToDataset.put("poi30NB","PROMISE");

        problemToDataset.put("velocity16KNN","PROMISE");
        problemToDataset.put("velocity16LR","PROMISE");
        problemToDataset.put("velocity16J48","PROMISE");
        problemToDataset.put("velocity16NB","PROMISE");

        problemToDataset.put("xerces14KNN","PROMISE");
        problemToDataset.put("xerces14LR","PROMISE");
        problemToDataset.put("xerces14J48","PROMISE");
        problemToDataset.put("xerces14NB","PROMISE");



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


        //problemToarff文件 Relink
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

        //problemToarff文件 promise

        problemToArffName.put("ant17KNN","ant17");
        problemToArffName.put("ant17LR","ant17");
        problemToArffName.put("ant17J48","ant17");
        problemToArffName.put("ant17NB","ant17");


        problemToArffName.put("ivy20KNN","ivy20");
        problemToArffName.put("ivy20LR","ivy20");
        problemToArffName.put("ivy20J48","ivy20");
        problemToArffName.put("ivy20NB","ivy20");


        problemToArffName.put("lucene24KNN","lucene24");
        problemToArffName.put("lucene24LR","lucene24");
        problemToArffName.put("lucene24J48","lucene24");
        problemToArffName.put("lucene24NB","lucene24");

        problemToArffName.put("synapse12KNN","synapse12");
        problemToArffName.put("synapse12LR","synapse12");
        problemToArffName.put("synapse12J48","synapse12");
        problemToArffName.put("synapse12NB","synapse12");

        problemToArffName.put("xalan26KNN","xalan26");
        problemToArffName.put("xalan26LR","xalan26");
        problemToArffName.put("xalan26J48","xalan26");
        problemToArffName.put("xalan26NB","xalan26");


        problemToArffName.put("camel16KNN","camel16");
        problemToArffName.put("camel16LR","camel16");
        problemToArffName.put("camel16J48","camel16");
        problemToArffName.put("camel16NB","camel16");

        problemToArffName.put("jedit40KNN","jedit40");
        problemToArffName.put("jedit40LR","jedit40");
        problemToArffName.put("jedit40J48","jedit40");
        problemToArffName.put("jedit40NB","jedit40");

        problemToArffName.put("poi30KNN","poi30");
        problemToArffName.put("poi30LR","poi30");
        problemToArffName.put("poi30J48","poi30");
        problemToArffName.put("poi30NB","poi30");

        problemToArffName.put("velocity16KNN","velocity16");
        problemToArffName.put("velocity16LR","velocity16");
        problemToArffName.put("velocity16J48","velocity16");
        problemToArffName.put("velocity16NB","velocity16");

        problemToArffName.put("xerces14KNN","xerces14");
        problemToArffName.put("xerces14LR","xerces14");
        problemToArffName.put("xerces14J48","xerces14");
        problemToArffName.put("xerces14NB","xerces14");



        Properties prop = new Properties();
        try {

            prop.load(new FileInputStream("config.properties"));
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

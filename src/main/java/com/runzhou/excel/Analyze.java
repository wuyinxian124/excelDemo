package com.runzhou.excel;

import com.runzhou.excel.pojo.DemoData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author runzhouwu
 * @Description TODO
 * on 2021/8/10 9:01 下午
 */
public class Analyze {

    private static final Logger LOG = LoggerFactory.getLogger(Analyze.class);
    private static final Logger LOGwm = LoggerFactory.getLogger("wuLog");
    private static final Logger LOGmore = LoggerFactory.getLogger("moreLog");

    private List<DemoData> beforeData;

    private boolean begin;
    private boolean done;

    public Analyze() {
    }

    private final String[] indexItem = {"-很好","-无","-功能不足/建议","-其他"};
    private final String[][] valueKey = {
            {"很好","没问题","没有问题","还好","很方便","满意","很不错","还可以","没难度","没有难度","容易","还不错","挺方便的","无痛点","no"},
            {"跳过","差不多","没有","暂无","未使用","没用","不清楚","不痛","没什么问题","無","没用","没发现","没什么难点","无","1","没什么"},
            {"有问题","不方便","不知道","麻烦","很麻烦","很繁琐","繁琐"},
            {"建议"}
    };
    public void setBeforeData(List<DemoData> beforeData) {
        this.beforeData = beforeData;
    }

    public void notifyAnalyze(){
        begin = true;
        new Thread(new analyzeThread()).start();
    }

    class analyzeThread implements Runnable{

        @Override
        public void run() {
            analyze();

            done = true;
        }
    }

    public boolean isDone() {
        return done;
    }

    private void checkAndAdd(List<String> list,Map<String,Integer> map){
        for(String str:list){
            if(StringUtils.isBlank(str)){
                continue;
            }
            int size = 0;
            if(map.containsKey(str)){
                size = map.get(str);
                size++;
            }else{
                size = 1;
            }
            map.put(str,size);
        }
    }
    private void analyze() {
        while(!begin){
            LOG.info("wait  ..");
        }
        Map<String,Integer> hash = new HashMap<>();
        int total = 0;
        int idea = 0;
        int non = 0;
        total = beforeData.size();
        for(DemoData demoData: beforeData){
            String content = demoData.getContent();
            try {
                if(content.length() <  4){
                    non++;
                    LOGwm.info("------ ---user {} say {} no split", demoData.getName(), demoData.getContent());
                    continue;
                }
                List<String> keyList = splitContent(content);
                checkAndAdd(keyList,hash);
                int index =  0;
                String match = "" ;
                for(String[] valueK: valueKey){
                    match = containExt(valueK,keyList);
                    if(StringUtils.isNotBlank(match) && keyList.size() < 3){
                        break;
                    }
                    index++;
                }

                if(index < indexItem.length){
                    if(index == 1){
                        non++;
                        LOGwm.info("user {} say {} match key {} value {}", demoData.getName(), demoData.getContent(), indexItem[index],match);
                    }else {
                        LOG.info("user {} say {} match {}", demoData.getName(), demoData.getContent(), indexItem[index]);
                    }
                }else{
                    LOG.info("user {} say {} had not match any item,which keyList {}",
                            demoData.getName(),demoData.getContent(), StringUtils.join(keyList,"|"));
                    LOGmore.info("{} say {}",demoData.getName(),demoData.getContent());
                    idea++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        print20(hash);
        LOG.info("总数记录 {}  ,有效建议 {} ,无效建议 {}",total,idea,non);
    }

    private void print20(Map<String,Integer> map){

        List<Map.Entry<String, Integer>> mapList = new ArrayList<>(map.entrySet());
//根据value排序
        mapList.sort((o1, o2) -> (o2.getValue() - o1.getValue()));

        //排序后
        LOG.info("出现最多次数的关键字");
        for (int i = 0; i < 20; i++) {
            String key = mapList.get(i).getKey();
            if(key.length() < 2){
                continue;
            }
            int value = mapList.get(i).getValue();
            LOG.info("{} 出现了 {} 次",key,value);
        }

    }
    private String containExt(String[] valueAry,List<String> keyList){

        for(String value:valueAry) {
            if (keyList.contains(value)) {
                return value;
            }
        }

        return null;

    }

    public List<String> splitContent(String text) throws IOException {
        List<String> splitKey = new ArrayList<>();
        StringReader sr=new StringReader(text);
        IKSegmenter ik=new IKSegmenter(sr, true);
        Lexeme lex=null;
        while((lex=ik.next())!=null){
            splitKey.add(lex.getLexemeText());
        }
        return splitKey;
    }


}

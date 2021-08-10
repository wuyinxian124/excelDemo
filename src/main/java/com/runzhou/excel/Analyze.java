package com.runzhou.excel;

import com.runzhou.excel.pojo.DemoData;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author runzhouwu
 * @Description TODO
 * on 2021/8/10 9:01 下午
 */
public class Analyze {

    private List<DemoData> beforeData;

    private boolean begin;

    public Analyze() {

    }

    private final String[] index = {"很好","不足","其他","无","建议"};
    private final String[][] valueKey = {
            {"很好","没问题","没有问题"},
            {"有问题","不方便"},
            {"建议"},
            {"无","跳过"}
    };
    public void setBeforeData(List<DemoData> beforeData) {
        this.beforeData = beforeData;
    }

    public void notifyAnalyze(){
        begin = true;
    }

    private void analyze() {
        while(!begin){
            System.out.println("wait  ..");
        }
        for(DemoData demoData: beforeData){
            String content = demoData.getContent();
            try {
                List<String> keyList = splitContent(content);
                int index =  0;
                for(String[] valueK: valueKey){
                    if(keyList.contains(valueK)){
                        break;
                    }
                    index++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

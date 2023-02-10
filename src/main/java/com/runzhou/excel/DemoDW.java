package com.runzhou.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.runzhou.excel.pojo.DemoData;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * @author runzhouwu
 * @Description TODO
 *         on 2021/8/10 8:36 下午
 */
public class DemoDW {


    public static void main(String[] args) throws IOException, InterruptedException {

        // read
        String fileName = "src/main/resources/lb_task_link.csv";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
//        EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();

        Analyze analyze = new Analyze();
        ExcelReader excelReader = EasyExcel.read(fileName, DemoData.class, new DemoDataListener(analyze)).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
        excelReader.finish();
        while (!analyze.isDone()) {
            TimeUnit.SECONDS.sleep(2);
        }
        System.out.println("done ..");

    }

    private static String WordSplition(String sentence) throws IOException {
        StringBuilder text = new StringBuilder();
        // 创建分词对象
        Analyzer analyzer = new IKAnalyzer(true);
        StringReader reader = new StringReader(sentence);
        // 分词
        TokenStream tokenStream = analyzer.tokenStream("", reader);
        CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
        // 遍历分词数据
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            text.append(charTerm.toString()).append("|");
        }
        tokenStream.close();
        reader.close();
        return text.toString().trim() + "\n";
    }


}

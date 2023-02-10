package com.runzhou.csv;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class DemoReader {


    public static void main(String[] args) {

        Map<String, String> vt = readV();
//        Map<String, List<String>> fromTo = readLink(vt);
//        checkMatch(fromTo);

        ValueGraph<String, Integer> graph = readLinkStruct(vt);
        // 广度优先
//        Iterable<String> bfs = Traverser.forGraph(graph).breadthFirst("20220902093737652");

        // 查询入度
//        Set<String> father = graph.predecessors("20220521215850857");
//        log.info("father {}", StringUtils.join(father, ","));
//        checkMatchV1(graph);
        String fromKey = "20220902093737652";
        String toKey = "20220927164124602";
//        log.info("match ? {}", graph. (fromKey, toKey));
        List<String> shortestPath = DijkstraWithPriorityQueue.findShortestPath(graph, fromKey, toKey);
        log.info(StringUtils.join(shortestPath, ","));
    }

    private static List<String> readLame() {
        List<String> list = new ArrayList<>();
        try {
            File myObj = new File("src/main/resources/lame.txt");
            Scanner myReader = new Scanner(myObj);
            int i = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
//                if (i++ < 10) {
//                    System.out.println(data);
//                }
                list.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return list;
    }

    public static void checkMatchV1(MutableGraph<String> graph) {
        String fromKey = "20220902093737652";
        String toKey = "20220927164124602";
        log.info("match ? {}", graph.hasEdgeConnecting(fromKey, toKey));
    }


    public static void checkMatch(Map<String, List<String>> fromTo) {
        String fromKey = "20220902093737652";
        String toKey = "20220927164124602";
        broadFirstSearch(fromKey, fromTo, toKey);
    }

    public static void broadFirstSearch(String from, Map<String, List<String>> ftMap, String to) {
        Queue<String> queue = new LinkedList<>();
        List<String> sonList = new ArrayList<>();
        Map<String, Boolean> hadRead = new HashMap<>();
        queue.add(from);
        int i = 0;
        log.info("print 0 " + from);
        while (!queue.isEmpty()) {
            String node = queue.poll();
//            System.out.print(node + ", ");
            hadRead.put(node, true);

            if (node.equals(to)) {
                log.info("》》》》》》》》》》》》》match at " + i);
                break;
            }
            if (ftMap.containsKey(node)) {
                List<String> list = ftMap.get(node);
                i++;
//                queue.addAll(list);
                List<String> print = new ArrayList<>();
                for (String item : list) {
                    if (!queue.contains(item)) {
                        if (!hadRead.containsKey(item)) {
                            queue.add(item);
                            print.add(item);
                            sonList.add(item);
                        }
                    }
                }
                log.info("print " + i + " ," + StringUtils.join(print, ","));
            } else {
//                log.debug("---can not find son " + node);
            }
            // queue先进先出，所以先左后右
//            if (node.left != null) {
//                queue.add(node.left);
//            }
//            if (node.right != null) {
//                queue.add(node.right);
//            }
        }
        List<String> realSon = readLame();
        for (String son : realSon) {
            if (!sonList.contains(son)) {
                log.info("find spece one {}", son);
            }
        }
        log.info("done");

    }

    public static ValueGraph<String, Integer> readLinkStruct(Map<String, String> vt) {
        MutableValueGraph<String, Integer> graph1 = ValueGraphBuilder.directed() //指定为有向图
                .nodeOrder(ElementOrder.<String>insertion()) //节点按插入顺序输出
                //(还可以取值无序unordered()、节点类型的自然顺序natural())
                .expectedNodeCount(100000) //预期节点数
                .allowsSelfLoops(true) //允许自环
                .build();
        Map<String, List<String>> fromTo = new HashMap<>();
        CSVReader reader = null;
        try {
            //parsing a CSV file into CSVReader class constructor
            String fileName = "src/main/resources/lb_task_link.csv";
            reader = new CSVReader(new FileReader(fileName));
            String[] nextLine;
            int i = 0;
            //reads one line at a time
            while ((nextLine = reader.readNext()) != null) {
                if (i == 0) {
//                log.info(StringUtils.join(nextLine, ","));
                    log.info(nextLine[1] + "," + nextLine[2] + "," + nextLine[3]);
                } else {
                    String realFrom;
                    if ("real_real".equals(nextLine[1])) {
                        realFrom = nextLine[2];
                    } else {
                        realFrom = vt.get(nextLine[2]);
//                        fromTo.put(realFrom, nextLine[3]);
                    }
                    List<String> list;
                    if (fromTo.containsKey(realFrom)) {
                        list = fromTo.get(realFrom);
                    } else {
                        list = new ArrayList<>();
                    }
                    if (StringUtils.isNotBlank(realFrom) && StringUtils.isNotBlank(nextLine[3])) {
                        graph1.putEdgeValue(realFrom, nextLine[3], 1);
                        list.add(nextLine[3]);
                        fromTo.put(realFrom, list);
                    } else {
//                        log.info(realFrom + "," + nextLine[3]);

                    }
                }
                i++;
            }
            log.info("loader from to  " + i);
            log.info("from - to " + fromTo.size());
            graph1.putEdgeValue("20220626175957770", "20220523234900888", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("-----------------");
        return graph1;

    }

    public static Map<String, List<String>> readLink(Map<String, String> vt) {
        Map<String, List<String>> fromTo = new HashMap<>();
        CSVReader reader = null;
        try {
            //parsing a CSV file into CSVReader class constructor
            String fileName = "src/main/resources/lb_task_link.csv";
            reader = new CSVReader(new FileReader(fileName));
            String[] nextLine;
            int i = 0;
            //reads one line at a time
            while ((nextLine = reader.readNext()) != null) {
                if (i == 0) {
//                log.info(StringUtils.join(nextLine, ","));
                    log.info(nextLine[1] + "," + nextLine[2] + "," + nextLine[3]);
                } else {
                    String realFrom;
                    if ("real_real".equals(nextLine[1])) {
                        realFrom = nextLine[2];
                    } else {
                        realFrom = vt.get(nextLine[2]);
//                        fromTo.put(realFrom, nextLine[3]);
                    }
                    List<String> list;
                    if (fromTo.containsKey(realFrom)) {
                        list = fromTo.get(realFrom);
                    } else {
                        list = new ArrayList<>();
                    }
                    if (StringUtils.isNotBlank(realFrom) && StringUtils.isNotBlank(nextLine[3])) {
                        list.add(nextLine[3]);
                        fromTo.put(realFrom, list);
                    } else {
//                        log.info(realFrom + "," + nextLine[3]);
//                        log.info();
                    }
                }
                i++;
            }
            log.info("loader from to  " + i);
            log.info("from - to " + fromTo.size());
            String realFrom = "20220626175957770";
            List<String> list;
            if (fromTo.containsKey(realFrom)) {
                list = fromTo.get(realFrom);
            } else {
                list = new ArrayList<>();
            }
            list.add("20220523234900888");
            fromTo.put(realFrom, list);

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("-----------------");
        return fromTo;

    }

    public static Map<String, String> readV() {
        Map<String, String> v_t = new HashMap<>();
        CSVReader reader = null;
        try {

            //parsing a CSV file into CSVReader class constructor
            String fileName = "src/main/resources/virtual_task.csv";
            reader = new CSVReader(new FileReader(fileName));
            String[] nextLine;
            int i = 0;
            //reads one line at a time
            while ((nextLine = reader.readNext()) != null) {
                if (i == 0) {
//                    log.info(StringUtils.join(nextLine, ","));
                    log.info(nextLine[0] + "," + nextLine[9]);
                } else {
                    v_t.put(nextLine[0], nextLine[9]);
                }
                i++;

            }
            log.info("loader v t " + i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("-----------------");
        return v_t;
    }

}



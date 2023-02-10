package com.runzhou.csv;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class DijkstraSolve {

    private final String sourceNode;
    private final MutableValueGraph<String, Integer> graph;

    public DijkstraSolve(String sourceNode, MutableValueGraph<String, Integer> graph) {
        this.sourceNode = sourceNode;
        this.graph = graph;
    }

    public static void main(String[] args) {
//        MutableValueGraph<String, Integer> graph = buildGraph();
        Map<String, String> vt = readV();
        MutableValueGraph<String, Integer> graph = readLinkStruct(vt);
        DijkstraSolve dijkstraSolve = new DijkstraSolve("20220902093737652", graph);

        dijkstraSolve.dijkstra();
        dijkstraSolve.printResult();
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

    public static MutableValueGraph<String, Integer> readLinkStruct(Map<String, String> vt) {
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


    private void dijkstra() {
        initPathFromSourceNode(sourceNode);
        Set<String> nodes = graph.nodes();
        if (!nodes.contains(sourceNode)) {
            throw new IllegalArgumentException(sourceNode + " is not in this graph!");
        }

        Set<String> notVisitedNodes = new HashSet<>(graph.nodes());
        String currentVisitNode = sourceNode;
        while (!notVisitedNodes.isEmpty()) {
            String nextVisitNode = findNextNode(currentVisitNode, notVisitedNodes);
            if (nextVisitNode.equals("")) {
                break;
            }
            notVisitedNodes.remove(currentVisitNode);
            currentVisitNode = nextVisitNode;
        }
    }

    private String findNextNode(String currentVisitNode, Set<String> notVisitedNodes) {
        int shortestPath = Integer.MAX_VALUE;
        String nextVisitNode = "";

        for (String node : graph.nodes()) {
            if (currentVisitNode.equals(node) || !notVisitedNodes.contains(node)) {
                continue;
            }

            if (graph.successors(currentVisitNode).contains(node)) {
                Integer edgeValue =
                        graph.edgeValue(sourceNode, currentVisitNode).get() + graph.edgeValue(currentVisitNode, node)
                                .get();
                Integer currentPathValue = graph.edgeValue(sourceNode, node).get();
                if (edgeValue > 0) {
                    graph.putEdgeValue(sourceNode, node, Math.min(edgeValue, currentPathValue));
                }
            }

            if (graph.edgeValue(sourceNode, node).get() < shortestPath) {
                shortestPath = graph.edgeValue(sourceNode, node).get();
                nextVisitNode = node;
            }
        }

        return nextVisitNode;
    }

    private void initPathFromSourceNode(String sourceNode) {
        graph.nodes().stream().filter(
                        node -> !graph.adjacentNodes(sourceNode).contains(node))
                .forEach(node -> graph.putEdgeValue(sourceNode, node, Integer.MAX_VALUE));
        graph.putEdgeValue(sourceNode, sourceNode, 0);
    }

    private void printResult() {
        for (String node : graph.nodes()) {
            System.out.println(sourceNode + "->" + node + " shortest path is:" + graph.edgeValue(sourceNode, node));
        }
    }
}

package com.gome.note.manager;

import java.util.LinkedList;

/**
 * Created on 2017/4/20.
 *
 * @author pythoncat
 */

public class NodeManager {
    private static LinkedList<String> nodeList = new LinkedList<>();

    public static LinkedList<String> getNodeList() {
        return nodeList;
    }

    public static void setNodeList(LinkedList<String> nodeList) {
        NodeManager.nodeList = nodeList;
    }

    public static void releaseNode() {
        nodeList.clear();
    }
}

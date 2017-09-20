package com.liuwan.mydesign.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuwan on 2016/12/7.
 * 邻接矩阵实现有向图的深度优先遍历
 */
public class GraphUtil<T> {
    // 邻接矩阵
    private double[][] matrix;
    // 各个点所携带的信息
    private T[] vertex;
    // 结点的数目
    private int vertexNum;
    // 路径长度
    private double pathLength;
    // 当前结点是否还有下一个结点，判断递归是否结束的标志
    private boolean noNext = false;
    // 当前距离是否大于设定的距离，判断递归是否结束的标志
    private boolean isOver = false;
    // 所有路径的结果集
    private List<List<T>> pathList = new ArrayList<>();
    // 所有路径长度的集合
    private List<Double> pathLengthList = new ArrayList<>();
    // 要查询的路径最大长度
    private double distance;

    public GraphUtil(T[] vertex, double[][] matrix) {
        if (matrix.length != matrix[0].length) {
            throw new IllegalArgumentException("当前邻接矩阵不是方阵");
        }
        if (matrix.length != vertex.length) {
            throw new IllegalArgumentException("当前顶点数量与邻接矩阵大小不一致");
        }
        this.vertex = vertex;
        this.matrix = matrix;
        vertexNum = matrix.length;
    }

    /**
     * 深度遍历的递归
     */
    private void DFS(int begin, double length, List<T> edges) {
        // 累积当前路径长度
        pathLength += length;
        if (pathLength > distance) {
            isOver = true;
            return;
        }
        // 将当前结点加入记录队列
        edges.add(vertex[begin]);
        // 标记回滚位置
        int rollBackNum = -1;
        // 遍历相邻的结点
        for (int a = 0; a < vertexNum; a++) {
            if ((matrix[begin][a] > 0)) {
                // 临时加入相邻结点，试探新的路径是否已遍历过
                edges.add(vertex[a]);
                if (containBranch(pathList, edges)) {
                    // 路径已存在，将相邻结点再移出记录队伍
                    edges.remove(vertex[a]);
                    // 记录相邻点位置，用于循环结束发现仅有当前一个相邻结点时回滚事件
                    rollBackNum = a;
                    // 寻找下一相邻结点
                    continue;
                } else {
                    // 路径为新路径，准备进入递归，将相邻结点移出记录队伍，递归中会再加入，防止重复添加
                    edges.remove(vertex[a]);
                    // 递归
                    DFS(a, matrix[begin][a], edges);
                }
            }
            // 终止递归
            if (noNext || isOver) {
                return;
            }
        }
        if (rollBackNum > -1) {
            // 循环结束仅有一个相邻结点，从这个相邻结点往下递归
            DFS(rollBackNum, matrix[begin][rollBackNum], edges);
        } else {
            // 当前结点没有相邻结点，设置flag以结束递归
            noNext = true;
        }
    }

    /**
     * 开始深度优先遍历
     */
    public List<List<T>> startSearch(int begin, double distance) {
        this.distance = distance;
        // 计算所有分支数N，循环N次
        for (int i = 0; i < countPathNumber(); i++) {
            // 用于存储遍历过的点
            List<T> edges = new LinkedList<>();
            // 每次计算路径前，初始化变量
            pathLength = 0;
            noNext = false;
            isOver = false;
            // 开始遍历
            DFS(begin, 0, edges);
            // 保存结果
            pathList.add(edges);
            pathLengthList.add(pathLength);

            // 获取的路径相同，提前结束循环
            if (pathList.size() > 1) {
                if (pathList.get(pathList.size() - 1).toString()
                        .equals(pathList.get(pathList.size() - 2).toString())) {
                    pathList.remove(pathList.size() - 1);
                    pathLengthList.remove(pathList.size() - 1);
                    break;
                }
            }
        }

        // 按路径长度从大到小排序
        sortList(pathList, pathLengthList);
        return pathList;
    }

    /**
     * 计算路径的分支数量
     */
    private int countPathNumber() {
        int[] numberArray = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j][i] > 0) {
                    numberArray[j]++;
                }
            }
        }
        int number = 1;
        for (int aNumberArray : numberArray) {
            if (aNumberArray > 1) {
                number++;
            }
        }
        return number;
    }

    /**
     * 判断当前路径是否被已有路径的结果集合所包含
     */
    private boolean containBranch(List<List<T>> nodeLists, List<T> edges) {
        for (int i = 0; i < nodeLists.size(); i++) {
            List<T> list = nodeLists.get(i);
            if (list.containsAll(edges)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 冒泡排序
     */
    private void sortList(List<List<T>> vertexLists, List<Double> lengthList) {
        for (int i = 0; i < lengthList.size() - 1; i++) {
            // 最多做n-1趟排序
            for (int j = 0; j < lengthList.size() - i - 1; j++) {
                // 对当前无序区间score[0......length-i-1]进行排序(j的范围很关键，这个范围是在逐步缩小的)
                if (lengthList.get(j) < lengthList.get(j + 1)) {
                    // 把小的值交换到后面
                    double length = lengthList.get(j);
                    lengthList.set(j, lengthList.get(j + 1));
                    lengthList.set(j + 1, length);

                    List<T> vertexList = vertexLists.get(j);
                    vertexLists.set(j, vertexLists.get(j + 1));
                    vertexLists.set(j + 1, vertexList);
                }
            }
        }
    }

}
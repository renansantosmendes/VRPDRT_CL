/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithms;

import static Algorithms.Algorithms.buildInstaceName;
import static Algorithms.Methods.readProblemData;
import static Algorithms.Methods.readProblemUsingExcelData;
import ProblemRepresentation.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author renansantos
 */
public class MethodsTest {

    @Test
    public void testReadProblemUsingExcelData() throws Exception {
        final Long timeWindows = (long) 3;
        List<Request> requests1 = new ArrayList<>();
        List<List<Integer>> listOfAdjacencies1 = new LinkedList<>();
        List<List<Long>> distanceBetweenNodes1 = new LinkedList<>();
        List<List<Long>> timeBetweenNodes1 = new LinkedList<>();
        Set<Integer> Pmais1 = new HashSet<>();
        Set<Integer> Pmenos1 = new HashSet<>();
        Set<Integer> setOfNodes1 = new HashSet<>();
        int numberOfNodes1 = 12;
        Map<Integer, List<Request>> requestsWhichBoardsInNode1 = new HashMap<>();
        Map<Integer, List<Request>> requestsWhichLeavesInNode1 = new HashMap<>();
        List<Integer> loadIndexList1 = new LinkedList<>();
        Set<Integer> setOfVehicles1 = new HashSet<>();
        List<Request> listOfNonAttendedRequests1 = new ArrayList<>();
        List<Request> requestList1 = new ArrayList<>();
        
        List<Request> requests2 = new ArrayList<>();
        List<List<Integer>> listOfAdjacencies2 = new LinkedList<>();
        List<List<Long>> distanceBetweenNodes2 = new LinkedList<>();
        List<List<Long>> timeBetweenNodes2 = new LinkedList<>();
        Set<Integer> Pmais2 = new HashSet<>();
        Set<Integer> Pmenos2 = new HashSet<>();
        Set<Integer> setOfNodes2 = new HashSet<>();
        int numberOfNodes2 = 12;
        Map<Integer, List<Request>> requestsWhichBoardsInNode2 = new HashMap<>();
        Map<Integer, List<Request>> requestsWhichLeavesInNode2 = new HashMap<>();
        List<Integer> loadIndexList2 = new LinkedList<>();
        Set<Integer> setOfVehicles2 = new HashSet<>();
        List<Request> listOfNonAttendedRequests2 = new ArrayList<>();
        List<Request> requestList2 = new ArrayList<>();
        
        String filePath = "/home/renansantos/Área de Trabalho/Excel Instances/";
        int numberOfRequests = 50;
        //int numberOfNodes = 12;
        int requestTimeWindows = 10;
        String instanceSize = "s";
        String nodesData = "bh_n" + numberOfNodes1 + instanceSize;
        String adjacenciesData = "bh_adj_n" + numberOfNodes1 + instanceSize;
        String instanceName = buildInstaceName(nodesData, adjacenciesData, numberOfRequests, numberOfNodes1,
                requestTimeWindows, instanceSize);
        
        numberOfNodes1 = readProblemData(instanceName, nodesData, adjacenciesData, requests1, distanceBetweenNodes1,
                timeBetweenNodes1, Pmais1, Pmenos1, requestsWhichBoardsInNode1, requestsWhichLeavesInNode1, setOfNodes1,
                numberOfNodes1, loadIndexList1);

        numberOfNodes2 = readProblemUsingExcelData(filePath,instanceName, nodesData, adjacenciesData, requests2, distanceBetweenNodes2,
                timeBetweenNodes2, Pmais2, Pmenos2, requestsWhichBoardsInNode2, requestsWhichLeavesInNode2, setOfNodes2,
                numberOfNodes2, loadIndexList2);
        
        timeBetweenNodes1.forEach(System.out::println);
        System.out.println();
        timeBetweenNodes2.forEach(System.out::println);
        
        for(int i=0; i< requests1.size(); i++){
            assertEquals(requests1.get(i), requests2.get(i));
        }
        
    }

}

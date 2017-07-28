/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

import Algorithms.*;
import static Algorithms.Algorithms.*;
import java.util.*;
import ProblemRepresentation.*;
import static Algorithms.Methods.readProblemData;
import GoogleMapsApi.*;
import InstanceReaderWithMySQL.*;
import com.google.maps.errors.ApiException;
import java.io.IOException;
import Algorithms.*;
import static Algorithms.AlgorithmsCalibration.NSGAII_Calibration;
import static Algorithms.AlgorithmsCalibration.generateLambdas;
import static Algorithms.AlgorithmsCalibration.ils;
import static Algorithms.AlgorithmsCalibration.vnd;
import Controller.Controller;
import View.MainScreen;
import static Algorithms.EvolutionaryAlgorithms.NSGAII;
import static Algorithms.EvolutionaryAlgorithms.SPEA2;

/**
 *
 * @author renansantos
 */
public class VRPDRT {

    final static Long timeWindows = (long) 3;
    static List<Request> requests = new ArrayList<>();
    static List<List<Integer>> listOfAdjacencies = new LinkedList<>();
    static List<List<Long>> distanceBetweenNodes = new LinkedList<>();
    static List<List<Long>> timeBetweenNodes = new LinkedList<>();
    static Set<Integer> Pmais = new HashSet<>();
    static Set<Integer> Pmenos = new HashSet<>();
    static Set<Integer> setOfNodes = new HashSet<>();
    static int numberOfNodes;
    static Map<Integer, List<Request>> requestsWhichBoardsInNode = new HashMap<>();
    static Map<Integer, List<Request>> requestsWhichLeavesInNode = new HashMap<>();
    static List<Integer> loadIndexList = new LinkedList<>();
    static Set<Integer> setOfVehicles = new HashSet<>();
    static List<Request> listOfNonAttendedRequests = new ArrayList<>();
    static List<Request> requestList = new ArrayList<>();

    //-------------------Test--------------------------------
    static Long currentTime;
    static Integer lastNode;

    public static void main(String[] args) throws ApiException, InterruptedException, IOException {
        String directionsApiKey = "AIzaSyD9W0em7H723uVOMD6QFe_1Mns71XAi5JU";
        int numberOfRequests = 50;
        int numberOfNodes = 12;
        int requestTimeWindows = 5;
        String instanceSize = "s";
        String nodesData = "bh_n" + numberOfNodes + instanceSize;
        String adjacenciesData = "bh_adj_n" + numberOfNodes + instanceSize;
        String instanceName = buildInstaceName(nodesData, adjacenciesData, numberOfRequests, numberOfNodes,
                requestTimeWindows, instanceSize);
        final Integer numberOfVehicles = 50;
        final Integer vehicleCapacity = 3;
        Integer populationSize = 6;
        Integer maximumNumberOfGenerations = 10;
        Integer maximumNumberOfExecutions = 4;
        double probabilityOfMutation = 0.02;
        double probabilityOfCrossover = 0.7;
        int fileSize = populationSize;
        List<Double> parameters = new ArrayList<>();//0.0273, 0.5208, 0.0161, 0.3619, 0.0739
        List<Double> nadirPoint = new ArrayList<>();

//        new DataUpdaterUsingGoogleMapsApi(directionsApiKey, new NodeDAO(nodesData).getListOfNodes(),
//                adjacenciesData).updateAdjacenciesData();
        numberOfNodes = readProblemData(instanceName, nodesData, adjacenciesData, requests, distanceBetweenNodes,
                timeBetweenNodes, Pmais, Pmenos, requestsWhichBoardsInNode, requestsWhichLeavesInNode, setOfNodes,
                numberOfNodes, loadIndexList);
        Algorithms.printProblemInformations(requests, numberOfVehicles, vehicleCapacity, instanceName, adjacenciesData, nodesData);
        Methods.initializeFleetOfVehicles(setOfVehicles, numberOfVehicles);

               
        parameters.add(1.0);//1
        parameters.add((double) requestTimeWindows);//delta_t
        parameters.add((double) numberOfNodes);//n
        parameters.add((double) numberOfRequests * numberOfNodes * requestTimeWindows);// r n delta_t
        parameters.add((double) numberOfRequests * numberOfNodes);//r n

        nadirPoint.add(300000.0);
        nadirPoint.add(100000.0);

//        NSGAII(instanceName, parameters, nadirPoint, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
//                requests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
//                listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
//                timeWindows, currentTime, lastNode);
        SPEA2(instanceName, parameters, nadirPoint, populationSize, fileSize, maximumNumberOfGenerations, maximumNumberOfExecutions,
                probabilityOfMutation, probabilityOfCrossover, requests, requestsWhichBoardsInNode, requestsWhichLeavesInNode,
                numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests, requestList, loadIndexList,
                timeBetweenNodes, distanceBetweenNodes, timeWindows, currentTime, lastNode);

        
    }

}

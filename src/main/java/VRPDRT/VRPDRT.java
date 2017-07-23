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

/**
 *
 * @author renansantos
 */
public class VRPDRT {

    final static Long timeWindows = (long) 3;
    static List<Request> listOfRequests = new ArrayList<>();
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
        int requestTimeWindows = 10;
        String instanceSize = "s";
        String instanceName = "r050n12tw05";
        String nodesData = "bh_n" + numberOfNodes + instanceSize;
        String adjacenciesData = "bh_adj_n" + numberOfNodes + instanceSize;
        final Integer numberOfVehicles = 50;
        final Integer vehicleCapacity = 4;
        Integer populationSize = 100;
        Integer maximumNumberOfGenerations = 10;
        Integer maximumNumberOfExecutions = 2;
        double probabilityOfMutation = 0.02;
        double probabilityOfCrossover = 0.7;
        List<Double> parameters = new ArrayList<>();//0.0273, 0.5208, 0.0161, 0.3619, 0.0739
        List<Double> nadirPoint = new ArrayList<>();
        

//        new DataUpdaterUsingGoogleMapsApi(directionsApiKey, new NodeDAO(nodesData).getListOfNodes(),
//                adjacenciesData).updateAdjacenciesData();
        numberOfNodes = readProblemData(instanceName, nodesData, adjacenciesData, listOfRequests, distanceBetweenNodes,
                timeBetweenNodes, Pmais, Pmenos, requestsWhichBoardsInNode, requestsWhichLeavesInNode, setOfNodes,
                numberOfNodes, loadIndexList);

        Algorithms.printProblemInformations(listOfRequests, numberOfVehicles, vehicleCapacity, instanceName, adjacenciesData, nodesData);

        Methods.initializeFleetOfVehicles(setOfVehicles, numberOfVehicles);

        Solution solution = new Solution(Algorithms.greedyConstructive(0.20, 0.15, 0.55, 0.10, listOfRequests.subList(0, 10),
                requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                timeWindows, currentTime, lastNode));
//System.out.println(solution);
        //solution.getSetOfRoutes().forEach(route -> System.out.println(route.getRequestAttendanceList()));
        //solution.getStaticMapForEveryRoute(new NodeDAO(nodesData).getListOfNodes(), adjacenciesData, nodesData);
        //new GoogleStaticMap(new NodeDAO(nodesData).getListOfNodes(), adjacenciesData, nodesData).getStaticMapForInstance();
        //solution.getStaticMapWithAllRoutes(new NodeDAO(nodesData).getListOfNodes(), adjacenciesData, nodesData);

        //Algorithms algorithms = new Algorithms(instanceName, nodesData, adjacenciesData);
        //algorithms.getData().getListOfRequests().forEach(System.out::println);
        //Solution individualSolution = new Solution(algorithms.individualConstructive());
        //System.out.println(individualSolution);
        //individualSolution.getSetOfRoutes().forEach(System.out::println);
        //individualSolution.getStaticMapForEveryRoute(new NodeDAO(nodesData).getListOfNodes(), adjacenciesData, nodesData);
        //individualSolution.getStaticMapWithAllRoutes(new NodeDAO(nodesData).getListOfNodes(), adjacenciesData, nodesData);
//        parameters.add(0.0273);
//        parameters.add(0.5208);
//        parameters.add(0.0161);
//        parameters.add(0.3619);
//        parameters.add(0.0739);
        parameters.add(1.0);
        parameters.add(10.0);
        parameters.add(12.0);
        parameters.add(6000.0);
        parameters.add(300.0);
        
        nadirPoint.add(200000.0);
        nadirPoint.add(100000.0);

        NSGAII(instanceName, parameters, nadirPoint, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                timeWindows, currentTime, lastNode);

        //new SolutionGeneratorForAggregationTree().generateSolutionsForAggregationTree();
//        InstanceData data = new InstanceData(instanceName, nodesData, adjacenciesData);
//        data.readProblemData();
//        data.getListOfRequests().forEach(System.out::println);
//        new Controller(args);
//        MainScreen.main(args);
//        MainScreen ms = new MainScreen();
//        ms.setVisible(true);
//        ms.setLocationRelativeTo(null);
//        MainScreen mainScreen = new MainScreen();
//        new Controller(mainScreen);
//        mainScreen.setVisible(true);
//        mainScreen.configureMainScreen();
//        NSGAII_Calibration(populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
//                listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
//                listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
//                timeWindows, currentTime, lastNode);
//        ils(listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, setOfVehicles, listOfNonAttendedRequests,
//                requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes, timeWindows, currentTime, lastNode);
    }

}

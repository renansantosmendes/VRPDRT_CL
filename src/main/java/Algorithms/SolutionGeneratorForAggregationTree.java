/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithms;

import static Algorithms.Algorithms.generateRandomSolutionsUsingPerturbation;
import static Algorithms.Algorithms.greedyConstructive;
import static Algorithms.Algorithms.perturbation;
import static Algorithms.Methods.readProblemData;
import ProblemRepresentation.Request;
import ProblemRepresentation.Solution;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author renansantos
 */
public class SolutionGeneratorForAggregationTree {

    private String vehicleCapacities[] = {"4"};//"11""16""13" -> removed
    private String nodesDistance[] = {"s"};//m l
    private String numberOfRequests[] = {"100"};//100,150,200,250
    private String timeWindows[] = {"10"};//05 "03"-> removed
    private String numberOfNodes = "12";
    private int numberOfInstances;
    private int numberOfSolutionsPerInstance;
    private int idealNumberOfSolutions = 10000;

    public void generateSolutionsForAggregationTree(List<Double> parameters) throws FileNotFoundException {
        this.numberOfInstances = this.vehicleCapacities.length * this.nodesDistance.length
                * this.numberOfRequests.length * this.timeWindows.length;
        this.numberOfSolutionsPerInstance = 10000 / this.numberOfInstances + 1;

        String folder = "RandomSolutionsForAggregationTree";
        boolean success = (new File(folder)).mkdirs();
        String destinationFileForObjectives = folder + "/Random_Solutions_AT_Objectives.txt";
        String destinationFileForSolutions = folder + "/Random_Solutions_AT_Solutions.txt";

        PrintStream printStreamForObjectives = new PrintStream(destinationFileForObjectives);
        PrintStream printStreamForSolutions = new PrintStream(destinationFileForSolutions);
        
        System.out.println("Number of solutions per instance = " + this.numberOfSolutionsPerInstance);
        
        for (int i = 0; i < vehicleCapacities.length; i++) {
            for (int j = 0; j < nodesDistance.length; j++) {
                for (int k = 0; k < numberOfRequests.length; k++) {
                    for (int l = 0; l < timeWindows.length; l++) {
                        String requestsInstance = "r" + numberOfRequests[k] + "n" + numberOfNodes + "tw" + timeWindows[l];
                        int vehicleCapacityForInstance = Integer.valueOf(vehicleCapacities[i]);
                        String nodesInstance = "bh_n" + numberOfNodes + nodesDistance[j];
                        String adjacenciesInstance = "bh_adj_n" + numberOfNodes + nodesDistance[j];

                        System.out.println("Instance configuration = " + requestsInstance + "-" + vehicleCapacityForInstance
                                + "-" + nodesInstance + "-" + adjacenciesInstance);
                        generateSolution(parameters, requestsInstance, vehicleCapacityForInstance, nodesInstance, adjacenciesInstance,
                                printStreamForObjectives, printStreamForSolutions);

                    }
                }
            }
        }
    }

    private void generateSolution(List<Double> parameters, String requestsInstance, int vehicleCapacityForInstance, String nodesInstance,
            String adjacenciesInstance, PrintStream printStreamForObjectives, PrintStream printStreamForSolutions)
            throws FileNotFoundException {
        final Long timeWindows = (long) 3;
        List<Request> listOfRequests = new ArrayList<>();
        List<List<Integer>> listOfAdjacencies = new LinkedList<>();
        List<List<Long>> distanceBetweenNodes = new LinkedList<>();
        List<List<Long>> timeBetweenNodes = new LinkedList<>();
        Set<Integer> Pmais = new HashSet<>();
        Set<Integer> Pmenos = new HashSet<>();
        Set<Integer> setOfNodes = new HashSet<>();
        int numberOfNodes = 0;
        Map<Integer, List<Request>> requestsWichBoardsInNode = new HashMap<>();
        Map<Integer, List<Request>> requestsWichLeavesInNode = new HashMap<>();
        List<Integer> loadIndexList = new LinkedList<>();
        Set<Integer> setOfVehicles = new HashSet<>();
        List<Request> listOfNonAttendedRequests = new ArrayList<>();
        List<Request> requestList = new ArrayList<>();
        Long currentTime = (long) 0;
        Integer lastNode = 0;

        String instanceName = requestsInstance;
        String nodesData = nodesInstance;
        String adjacenciesData = adjacenciesInstance;
        final Integer numberOfVehicles = 50;
        final Integer vehicleCapacity = vehicleCapacityForInstance;

        numberOfNodes = readProblemData(instanceName, nodesData, adjacenciesData, listOfRequests, distanceBetweenNodes,
                timeBetweenNodes, Pmais, Pmenos, requestsWichBoardsInNode, requestsWichLeavesInNode, setOfNodes,
                numberOfNodes, loadIndexList);

        Methods.initializeFleetOfVehicles(setOfVehicles, numberOfVehicles);

        Solution solution = greedyConstructive(0.2, 0.15, 0.55, 0.1, listOfRequests, requestsWichBoardsInNode,
                requestsWichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests, requestList,
                loadIndexList, timeBetweenNodes, distanceBetweenNodes, timeWindows, currentTime, lastNode);

        Solution solution1 = new Solution();

        for (int i = 0; i < numberOfSolutionsPerInstance; i++) {//numberOfSolutionsPerInstance
            solution1.setSolution(perturbation(parameters, solution, listOfRequests, requestsWichBoardsInNode, requestsWichLeavesInNode,
                    numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes,
                    distanceBetweenNodes, timeWindows));
            //System.out.println(solution1);
            printStreamForObjectives.print(solution1.getStringWithAllNonReducedObjectivesForCSVFile() + "\n");
            printStreamForSolutions.print(solution1 + "\n");
        }
    }
}

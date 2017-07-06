/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithms;

import static Algorithms.Algorithms.*;
import static Algorithms.EvolutionaryAlgorithms.*;
import static Algorithms.Methods.*;
import AlgorithmsResults.ResultsGraphicsForMultiObjectiveOptimization;
import ProblemRepresentation.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @author renansantos
 */
public class AlgorithmsCalibration {

    public static void NSGAII_Calibration(Integer populationSize, Integer maximumNumberOfGenerations,
            Integer maximumNumberOfExecutions, double probabilityOfMutation, double probabilityOfCrossover,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWhichBoardsInNode,
            Map<Integer, List<Request>> requestsWhichLeavesInNode, Integer numberOfNodes, Integer vehicleCapacity,
            Set<Integer> setOfVehicles, List<Request> listOfNonAttendedRequests, List<Request> requestList,
            List<Integer> loadIndexList, List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes,
            Long timeWindows, Long currentTime, Integer lastNode) throws IOException {

        //List<Double> parameters = new ArrayList<>();
        double hypervolume = 0;
        Map<Double, List<Double>> hypervolumesMap = new HashMap<>();
        //Start the algorithm here - generating random weigths
        for (int i = 0; i < 2; i++) {
            List<Double> parameters = new ArrayList<>();
            parameters.addAll(new ArrayList<>(generateLambdas()));

            //executing the NSGA-II algorithm
            hypervolume = NSGAII(parameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                    listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                    listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                    timeWindows, currentTime, lastNode);
            
            System.out.println("Lambdas = " + parameters);
            hypervolumesMap.put(hypervolume, parameters);
            //parameters.clear();
        }
        
        System.out.println(hypervolumesMap);
    }

    public static List<Double> generateLambdas() {
        Random rnd = new Random();
        double x, y, z, w, t;
        do {
            x = rnd.nextDouble();
            y = rnd.nextDouble();
            z = rnd.nextDouble();
            w = rnd.nextDouble();
            t = 1 - x - y - z - w;
        } while (x + y + z + w > 1);

        List<Double> parameters = new ArrayList<>();
        parameters.add(x);
        parameters.add(y);
        parameters.add(z);
        parameters.add(w);
        parameters.add(t);

        return parameters;
    }

}

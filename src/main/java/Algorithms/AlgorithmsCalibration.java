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
import java.text.DecimalFormat;
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

    public static double vnd(List<Double> parameters, Integer populationSize, Integer maximumNumberOfGenerations,
            Integer maximumNumberOfExecutions, double probabilityOfMutation, double probabilityOfCrossover,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWhichBoardsInNode,
            Map<Integer, List<Request>> requestsWhichLeavesInNode, Integer numberOfNodes, Integer vehicleCapacity,
            Set<Integer> setOfVehicles, List<Request> listOfNonAttendedRequests, List<Request> requestList,
            List<Integer> loadIndexList, List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes,
            Long timeWindows, Long currentTime, Integer lastNode) throws IOException {

        double oldHypervolume = NSGAII(parameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                timeWindows, currentTime, lastNode);
        int k = 1;
        int r = 4;
        double newHypervolume;
        while (k <= r) {
            //encontrar melhor vizinho s' em N_k(s)
            //se f(s') > f(s) -> s = s' e k = 1
            //entao -> k++
            List<Double> newParameters = new ArrayList<>(parameters);
            System.out.println("k = " + k + "\tr = " + k);
            newHypervolume = firstImprovement(k, newParameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                    listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                    listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                    timeWindows, currentTime, lastNode);

            if (newHypervolume > oldHypervolume) {
                parameters.clear();
                parameters.addAll(newParameters);
                oldHypervolume = newHypervolume;
                k = 1;
            } else {
                k++;
            }
        }

        //System.out.println("Best Hypervolume = " + oldHypervolume);
        //System.out.println("Best Lambdas = " + parameters);
        return oldHypervolume;
    }

    public static double firstImprovement(int neighborhoodNumber, List<Double> parameters, Integer populationSize, Integer maximumNumberOfGenerations,
            Integer maximumNumberOfExecutions, double probabilityOfMutation, double probabilityOfCrossover,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWhichBoardsInNode,
            Map<Integer, List<Request>> requestsWhichLeavesInNode, Integer numberOfNodes, Integer vehicleCapacity,
            Set<Integer> setOfVehicles, List<Request> listOfNonAttendedRequests, List<Request> requestList,
            List<Integer> loadIndexList, List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes,
            Long timeWindows, Long currentTime, Integer lastNode) throws IOException {
        double delta;
        double hypervolume = 0;
        switch (neighborhoodNumber) {
            case 1:
                delta = 0.01;
                hypervolume = addVariation(delta, parameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                        listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                        listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                        timeWindows, currentTime, lastNode);
                break;
            case 2:
                delta = 0.02;
                hypervolume = addVariation(delta, parameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                        listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                        listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                        timeWindows, currentTime, lastNode);
                break;
            case 3:
                delta = 0.03;
                hypervolume = addVariation(delta, parameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                        listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                        listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                        timeWindows, currentTime, lastNode);
                break;
            case 4:
                delta = 0.1;
                hypervolume = addVariation(delta, parameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                        listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                        listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                        timeWindows, currentTime, lastNode);
                break;
            default:
                break;
        }
        return hypervolume;
    }

    private static double addVariation(double delta, List<Double> parameters, Integer populationSize, Integer maximumNumberOfGenerations,
            Integer maximumNumberOfExecutions, double probabilityOfMutation, double probabilityOfCrossover,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWhichBoardsInNode,
            Map<Integer, List<Request>> requestsWhichLeavesInNode, Integer numberOfNodes, Integer vehicleCapacity,
            Set<Integer> setOfVehicles, List<Request> listOfNonAttendedRequests, List<Request> requestList,
            List<Integer> loadIndexList, List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes,
            Long timeWindows, Long currentTime, Integer lastNode) throws IOException {

        double newHypervolume = 0;
        double oldHypervolume = 0;

        for (int i = 0; i < parameters.size(); i++) {
            for (int j = i + 1; j < parameters.size(); j++) {
                //Random rnd = new Random();
                //int expoente = rnd.nextInt(2);
                //delta = (double) Math.pow(-1, expoente) * delta;
                double x = parameters.get(i) + delta;
                double y = parameters.get(j) - delta;

                DecimalFormat formatator = new DecimalFormat("0.00");

                x = Double.parseDouble(formatator.format(x).replace(",", "."));
                y = Double.parseDouble(formatator.format(y).replace(",", "."));

                List<Double> newParameters = new ArrayList<>(parameters);
                newParameters.set(i, x);
                newParameters.set(j, y);
                System.out.println("***************************************************************");
                System.out.println("*                     Lambdas Calibration                     *");
                System.out.println("***************************************************************");
                System.out.println("Lambdas = " + newParameters);

                oldHypervolume = NSGAII(parameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                        listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                        listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                        timeWindows, currentTime, lastNode);

                newHypervolume = NSGAII(newParameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                        listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                        listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                        timeWindows, currentTime, lastNode);

                if (newHypervolume > oldHypervolume) {
                    System.out.println("New Lambdas increased the hypervolume!!!");
                    parameters.clear();
                    parameters.addAll(newParameters);
                    return newHypervolume;
                }
            }
        }

        return oldHypervolume;
    }

    public static double perturbation(List<Double> parameters, Integer populationSize, Integer maximumNumberOfGenerations,
            Integer maximumNumberOfExecutions, double probabilityOfMutation, double probabilityOfCrossover,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWhichBoardsInNode,
            Map<Integer, List<Request>> requestsWhichLeavesInNode, Integer numberOfNodes, Integer vehicleCapacity,
            Set<Integer> setOfVehicles, List<Request> listOfNonAttendedRequests, List<Request> requestList,
            List<Integer> loadIndexList, List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes,
            Long timeWindows, Long currentTime, Integer lastNode) throws IOException {
        double perturbationDelta = 0.01;
        Random rnd = new Random();
        int expoente = rnd.nextInt(2);
        perturbationDelta = (double) Math.pow(-1, expoente) * perturbationDelta;
        int i, j;

        do {
            i = rnd.nextInt(parameters.size());
            j = rnd.nextInt(parameters.size());
        } while (i == j);

        double x = parameters.get(i) + perturbationDelta;
        double y = parameters.get(j) - perturbationDelta;

        DecimalFormat formatator = new DecimalFormat("0.00");

        x = Double.parseDouble(formatator.format(x).replace(",", "."));
        y = Double.parseDouble(formatator.format(y).replace(",", "."));
        parameters.set(i, x);
        parameters.set(j, y);

        double hypervolume = NSGAII(parameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                timeWindows, currentTime, lastNode);

        return hypervolume;
    }

    public static void ils(List<Request> listOfRequests, Map<Integer, List<Request>> requestsWhichBoardsInNode,
            Map<Integer, List<Request>> requestsWhichLeavesInNode, Integer numberOfNodes, Set<Integer> setOfVehicles,
            List<Request> listOfNonAttendedRequests, List<Request> requestList, List<Integer> loadIndexList,
            List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes, Long timeWindows, Long currentTime,
            Integer lastNode) throws IOException {

        Integer numberOfVehicles = 50;
        Integer vehicleCapacity = 4;
        Integer populationSize = 100;
        Integer maximumNumberOfGenerations = 3;
        Integer maximumNumberOfExecutions = 3;
        double probabilityOfMutation = 0.02;
        double probabilityOfCrossover = 0.7;

        List<Double> parameters = new ArrayList<>();
        double oldHypervolume, newHypervolume;
        int maxNumberOfIterations = 10, currentIteration = 0;
        parameters.add(0.20);
        parameters.add(0.20);
        parameters.add(0.20);
        parameters.add(0.20);
        parameters.add(0.20);

        oldHypervolume = vnd(parameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                timeWindows, currentTime, lastNode);
        
        while(currentIteration < maxNumberOfIterations){
            List<Double> newParameters = new ArrayList<>(parameters);
            newHypervolume = perturbation(newParameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                timeWindows, currentTime, lastNode);
            
            newHypervolume = vnd(newParameters, populationSize, maximumNumberOfGenerations, maximumNumberOfExecutions, probabilityOfMutation, probabilityOfCrossover,
                listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles,
                listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes,
                timeWindows, currentTime, lastNode);
            if(newHypervolume > oldHypervolume){
                oldHypervolume = newHypervolume;
                parameters.clear();
                parameters.addAll(newParameters);
            }
            currentIteration++;
        }
    }

}

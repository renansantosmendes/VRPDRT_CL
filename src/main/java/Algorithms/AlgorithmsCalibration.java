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

    public static double NSGAII_Calibration(List<Double> parameters, Integer populationSize, Integer maximumNumberOfGenerations,
            Integer maximumNumberOfExecutions, double probabilityOfMutation, double probabilityOfCrossover,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWhichBoardsInNode,
            Map<Integer, List<Request>> requestsWhichLeavesInNode, Integer numberOfNodes, Integer vehicleCapacity,
            Set<Integer> setOfVehicles, List<Request> listOfNonAttendedRequests, List<Request> requestList,
            List<Integer> loadIndexList, List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes,
            Long timeWindows, Long currentTime, Integer lastNode) throws IOException {

        List<Solution> offspring = new ArrayList<>();
        List<Solution> population = new ArrayList<>();
        List<Solution> finalPareto = new ArrayList<>();
        List<Solution> nonDominatedSolutions = new ArrayList();
        List<Solution> fileWithSolutions = new ArrayList();
        List<Integer> parents = new ArrayList<>();
        List<Solution> parentsAndOffspring = new ArrayList();
        List<List<Solution>> nonDominatedFronts = new ArrayList<>();
        double hypervolume = 0;
        String folderName, fileName;

        LocalDateTime time = LocalDateTime.now();
        folderName = "Algorithm_Normalization_Test" + time.getYear() + "_" + time.getMonthValue() + "_" + time.getDayOfMonth();
        fileName = "NSGAII";

        boolean success = (new File(folderName)).mkdirs();
        if (!success) {
            System.out.println("Folder already exists!");
        }
        try {
            List<Solution> combinedPareto = new ArrayList<>();
            PrintStream printStreamForCombinedPareto = new PrintStream(folderName + "/" + fileName + "-Pareto_Combinado.txt");
            for (int executionCounter = 0; executionCounter < maximumNumberOfExecutions; executionCounter++) {
                String executionNumber;
                executionNumber = Integer.toString(executionCounter);
                PrintStream saida1 = new PrintStream(folderName + "/" + fileName + "-Execucao-" + executionNumber + ".txt");
                PrintStream saida2 = new PrintStream(folderName + "/" + fileName + "-tamanho_arquivo-" + executionNumber + ".txt");
                PrintStream saida3 = new PrintStream(folderName + "/" + fileName + "-Execucao-Normalizada-" + executionNumber + ".txt");

                int maximumSize;

                inicializePopulationForCalibration(parameters, population, populationSize, listOfRequests,
                        requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests,
                        requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes, timeWindows, currentTime, lastNode);

                //normalizeObjectiveFunctionsValues(population);
                //normalizeObjectiveFunctions(population);
                normalizeObjectiveFunctionsForSolutions(population);
                evaluateAggregatedObjectiveFunctionsNormalizedForCalibration(parameters, population);

                //printPopulation(population);
                dominanceAlgorithm(population, nonDominatedSolutions);
                maximumSize = population.size();
                offspring.addAll(population);
                nonDominatedFrontiersSortingAlgorithm(offspring, nonDominatedFronts);
                fitnessEvaluationForMultiObjectiveOptimization(offspring);

                rouletteWheelSelectionAlgorithm(parents, offspring, maximumSize);

                twoPointsCrossover(offspring, population, maximumSize, probabilityOfCrossover, parents, listOfRequests,
                        requestList, setOfVehicles, listOfNonAttendedRequests, requestsWhichBoardsInNode,
                        requestsWhichLeavesInNode, timeBetweenNodes, distanceBetweenNodes, numberOfNodes,
                        vehicleCapacity, timeWindows);

                mutation2Shuffle(offspring, probabilityOfMutation, listOfRequests, requestsWhichBoardsInNode,
                        requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests,
                        requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes, timeWindows, currentTime, lastNode);

                //normalizeObjectiveFunctionsValues(fileWithSolutions);
                normalizeObjectiveFunctionsForSolutions(fileWithSolutions);
                evaluateAggregatedObjectiveFunctionsNormalizedForCalibration(parameters, fileWithSolutions);

                normalizeObjectiveFunctionsForSolutions(offspring);
                evaluateAggregatedObjectiveFunctionsNormalizedForCalibration(parameters, offspring);
                //normalizeObjectiveFunctions(fileWithSolutions);
                //normalizeObjectiveFunctions(fileWithSolutions);
                for (Solution s : fileWithSolutions) {
                    saida1.print("\t" + s.getAggregatedObjective1() + "\t" + s.getAggregatedObjective2() + "\n");
                    saida3.print("\t" + s.getAggregatedObjective1Normalized() + "\t" + s.getAggregatedObjective2Normalized() + "\n");
                }
                saida1.print("\n\n");
                saida2.print(fileWithSolutions.size() + "\n");
                saida3.print("\n\n");

                //dominanceAlgorithm(offspring, nonDominatedSolutions);
                //fileWithSolutions.addAll(nonDominatedSolutions);
                System.out.println("Execution = " + executionCounter);
                int actualGeneration = 0;
                while (actualGeneration < maximumNumberOfGenerations) {
                    dominanceAlgorithm(offspring, nonDominatedSolutions);
                    fileWithSolutions.addAll(nonDominatedSolutions);
                    nonDominatedFrontiersSortingAlgorithm(offspring, nonDominatedFronts);
                    fitnessEvaluationForMultiObjectiveOptimization(offspring);
                    parentsAndOffspring.clear();

                    parentsAndOffspring.addAll(population);
                    parentsAndOffspring.addAll(offspring);

                    nonDominatedFrontiersSortingAlgorithm(parentsAndOffspring, nonDominatedFronts);
                    fitnessEvaluationForMultiObjectiveOptimization(parentsAndOffspring);
                    dominanceAlgorithm(parentsAndOffspring, nonDominatedSolutions);

                    updateNSGASolutionsFile(parentsAndOffspring, fileWithSolutions, maximumSize);
                    //normalizeObjectiveFunctionsValues(fileWithSolutions);
                    normalizeObjectiveFunctionsForSolutions(fileWithSolutions);
                    evaluateAggregatedObjectiveFunctionsNormalizedForCalibration(parameters, fileWithSolutions);
                    //normalizeObjectiveFunctions(fileWithSolutions);
                    reducePopulation(population, nonDominatedFronts, maximumSize);
                    offspring.clear();

                    offspring.addAll(population);
                    rouletteWheelSelectionAlgorithm(parents, offspring, maximumSize);
                    twoPointsCrossover(offspring, population, maximumSize, probabilityOfCrossover, parents, listOfRequests, requestList, setOfVehicles, listOfNonAttendedRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, timeBetweenNodes, distanceBetweenNodes, numberOfNodes, vehicleCapacity, timeWindows);
                    mutation2Shuffle(offspring, probabilityOfMutation, listOfRequests, requestsWhichBoardsInNode, requestsWhichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes, timeWindows, currentTime, lastNode);
                    //normalizeObjectiveFunctionsValues(offspring);
                    normalizeAggregatedObjectiveFunctions(offspring);

                    normalizeObjectiveFunctionsForSolutions(offspring);
                    evaluateAggregatedObjectiveFunctionsNormalizedForCalibration(parameters, offspring);

                    System.out.println("Generation = " + actualGeneration + "\t" + fileWithSolutions.size());

                    for (Solution s : fileWithSolutions) {
                        saida1.print("\t" + s.getAggregatedObjective1() + "\t" + s.getAggregatedObjective2() + "\n");
                        saida3.print("\t" + s.getAggregatedObjective1Normalized() + "\t" + s.getAggregatedObjective2Normalized() + "\n");
                    }
                    saida1.print("\n\n");
                    saida2.print(fileWithSolutions.size() + "\n");
                    saida3.print("\n\n");
                    actualGeneration++;
                }
                offspring.clear();
                parentsAndOffspring.clear();
                combinedPareto.addAll(fileWithSolutions);
                fileWithSolutions.clear();
                population.clear();
                nonDominatedFronts.clear();
            }

            dominanceAlgorithm(combinedPareto, finalPareto);
            printPopulation(finalPareto);
            for (Solution individual : finalPareto) {
                printStreamForCombinedPareto.print(individual + "\n");
            }

            new ResultsGraphicsForMultiObjectiveOptimization(finalPareto, "ResultGraphics", "CombinedParetoSet");
            hypervolume = smetric(finalPareto);
            System.out.println("S-Metric = " + hypervolume);
            System.out.println("Final Pareto");
            finalPareto.forEach(u -> System.out.println(u.getAggregatedObjective1() + "\t" + u.getAggregatedObjective2()));
//            finalPareto.get(0).getStaticMapForEveryRoute(new NodeDAO("bh_nodes_little").getListOfNodes(),
//                    "adjacencies_bh_nodes_little_test", "bh_nodes_little");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return hypervolume;
    }

    public static void evaluateAggregatedObjectiveFunctionsNormalizedForCalibration(List<Double> parameters, Solution S) {
        S.setAggregatedObjective1(parameters.get(0) * S.getTotalDistanceNormalized() + parameters.get(1) * S.getTotalDeliveryDelayNormalized()
                + parameters.get(2) * S.getTotalRouteTimeChargeBanlanceNormalized()
                + parameters.get(4) * S.getNumberOfVehiclesNormalized());
        S.setAggregatedObjective2(parameters.get(3) * S.getNumberOfNonAttendedRequestsNormalized());
    }

    public static void evaluateAggregatedObjectiveFunctionsNormalizedForCalibration(List<Double> parameters, List<Solution> solutions) {
        for (Solution solution : solutions) {
            AlgorithmsCalibration.evaluateAggregatedObjectiveFunctionsNormalizedForCalibration(parameters, solution);
        }
    }

    public static void inicializePopulationForCalibration(List<Double> parameters, List<Solution> Pop, Integer TamPop, List<Request> listRequests,
            Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout, Integer n, Integer Qmax, Set<Integer> K,
            List<Request> U, List<Request> P, List<Integer> m, List<List<Long>> d, List<List<Long>> c,
            Long TimeWindows, Long currentTime, Integer lastNode) {

        for (int i = 0; i < TamPop; i++) {
            Solution S = new Solution();
            S.setSolution(generateRandomSolutionForCalibration(parameters, Pop, TamPop, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode));
            Pop.add(S);
        }

        for (int i = 0; i < TamPop; i++) {
            Solution solucao = new Solution(Pop.get(i));
        }
        Inicializa(Pop, TamPop, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);
    }

    public static Solution generateRandomSolutionForCalibration(List<Double> parameters, List<Solution> Pop, Integer TamPop, List<Request> listRequests, Map<Integer, List<Request>> Pin,
            Map<Integer, List<Request>> Pout, Integer n, Integer Qmax, Set<Integer> K, List<Request> U,
            List<Request> P, List<Integer> m, List<List<Long>> d, List<List<Long>> c,
            Long TimeWindows, Long currentTime, Integer lastNode) {

        P.clear();
        U.clear();
        P.addAll(listRequests);

        //Step 1
        Solution solution = new Solution();
        String log = "";

        int currentK;
        Map<Integer, Double> CRL = new HashMap<>(n), // Cost Rank List
                NRL = new HashMap<>(n), // Number of Passengers Rank List
                DRL = new HashMap<>(n), // Delivery time-window Rank List
                TRL = new HashMap<>(n), // Time-window Rank List
                NRF = new HashMap<>(n);	// Time-window Rank List

        Iterator<Integer> itK = K.iterator();
        U.clear();
        while (!P.isEmpty() && itK.hasNext()) {

            separateOriginFromDestination(U, Pin, Pout, n, P);

            //Step 2
            Route R = new Route();
            currentK = itK.next();
            log += "\tGROTA " + (currentK + 1) + " ";

            //Step 3
            R.addVisitedNodes(0);
            currentTime = (long) 0;
            double max, min;
            lastNode = R.getLastNode();

            boolean encontrado;

            while (!P.isEmpty()) {
                encontrado = false;
                m.clear();
                for (int i = 0; i < n; i++) {
                    m.add(Pin.get(i).size() - Pout.get(i).size());
                }

                //Step 4
                Set<Integer> FeasibleNode = new HashSet<>();
                List<Long> EarliestTime = new ArrayList<>();

                findFeasibleNodes(n, lastNode, encontrado, Qmax, R, Pin, Pout, FeasibleNode, d, currentTime, TimeWindows);

                //System.out.println("FEASIBLE NODES = "+ FeasibleNode);			
                if (FeasibleNode.size() > 1) {
                    //Step 4.1
                    CalculaCRL(FeasibleNode, CRL, c, lastNode);
                    //Step 4.2
                    CalculaNRL(FeasibleNode, NRL, m, lastNode);
                    //Step 4.3
                    CalculaDRL(FeasibleNode, DRL, Pout, lastNode, d, EarliestTime);
                    //Step 4.4
                    CalculaTRL(FeasibleNode, TRL, Pin, lastNode, d, EarliestTime);
                } else {
                    //Step 4.1
                    CalculaListaSemNosViaveis(CRL, FeasibleNode);
                    //Step 4.2
                    CalculaListaSemNosViaveis(NRL, FeasibleNode);
                    //Step 4.3
                    CalculaListaSemNosViaveis(DRL, FeasibleNode);
                    //Step 4.4
                    CalculaListaSemNosViaveis(TRL, FeasibleNode);
                }

                //Step 5 
                //CalculaNRF(NRF,CRL,NRL,DRL,TRL,alphaD,alphaP,alphaV,alphaT,FeasibleNode);
                Random gerador = new Random(19700621);
                for (Integer i : FeasibleNode) {
                    NRF.put(i, gerador.nextDouble());
                }
                //Step 6              
                //System.out.println("Tamanho da NRF = " + NRF.size());              
                max = Collections.max(NRF.values());

                currentTime = AdicionaNo(NRF, CRL, NRL, DRL, TRL, max, lastNode, Pin, d, EarliestTime, currentTime, R);
                lastNode = R.getLastNode();

                //Step 7
                //RETIRAR A LINHA DE BAIXO DEPOIS - inicialização de listRequestAux
                List<Request> listRequestAux = new LinkedList<>();
                //Desembarca as solicitações no nó 
                Desembarca(Pin, Pout, lastNode, currentTime, P, listRequestAux, R, log);
                //Embarca as solicitações sem tempo de espera
                Embarca(Pin, lastNode, currentTime, P, listRequestAux, R, log, Qmax);
                //Embarca agora as solicitações onde o veículo precisar esperar e guarda atualiza o tempo (currentTime)                               
                currentTime = EmbarcaRelaxacao(Pin, lastNode, currentTime, P, listRequestAux, R, log, Qmax, TimeWindows);

                //---------- Trata as solicitações inviáveis -----------
                RetiraSolicitacoesInviaveis(Pin, Pout, listRequestAux, currentTime, P, U);
                encontrado = ProcuraSolicitacaoParaAtender(R, Qmax, Pin, Pout, currentTime, n, d, lastNode, TimeWindows, encontrado);
                RetiraSolicitacaoNaoSeraAtendida(encontrado, Pin, Pout, listRequestAux, currentTime, P, U);

                //Step 8
                currentTime = FinalizaRota(P, R, currentTime, lastNode, d, solution);
            }

            //Step 9
            AnaliseSolicitacoesViaveisEmU(U, P, itK, d);
        }

//        S.setNonAttendedRequestsList(U);
//        //S.setfObjetivo1(FOp(S, c));
//        S.setLogger(log);
//        S.linkTheRoutes();
        //S.setFuncaoObjetivo(FuncaoObjetivo(S,c));
        solution.setNonAttendedRequestsList(U);
        solution.setTotalDistance(FO1(solution, c));
        solution.setTotalDeliveryDelay(FO2(solution));
        solution.setTotalRouteTimeChargeBanlance(FO3(solution));
        solution.setNumberOfNonAttendedRequests(FO4(solution));
        solution.setNumberOfVehicles(FO5(solution));
        solution.setTotalTravelTime(FO6(solution));
        solution.setTotalWaintingTime(FO7(solution));
        solution.setDeliveryTimeWindowAntecipation(FO8(solution));
        solution.setTotalOccupationRate(FO9(solution, Qmax));
        evaluateAggregatedObjectiveFunctionsNormalizedForCalibration(parameters,solution);

        solution.setObjectiveFunction(FuncaoDeAvaliacao(solution, listRequests, c));
        solution.setLogger(log);
        solution.linkTheRoutes();
        //S.setfObjetivo1((int) FuncaoObjetivo(S, c));
        return solution;
    }

}

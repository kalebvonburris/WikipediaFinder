package Finder;

import org.fastily.jwiki.core.NS;
import org.fastily.jwiki.core.Wiki;

import java.io.IOException;
import java.util.ArrayList;

public class HitlerFinder {

    /**************************************************
     HitlerFinder. By Kaleb Burris, 2020. Public Domain.
     ***************************************************/
    public static void main(String[] args) {
        // This is our article we want to find.
        String targetArticle = "Adolf Hitler";
        // Storing the random article url in order to simply things.
        Wiki wiki = new Wiki.Builder().withDefaultLogger(false).withDebug(false).build();
        ArrayList<String> randomArticle = wiki.getRandomPages(1, NS.MAIN);
        // Our manager for all of the paths we will go through.
        AlgorithmManager algorithmManager = new AlgorithmManager();
        String currentPage = randomArticle.get(0);
        try {
            RandomCollection<String> choices = new RandomCollection<>();
            algorithmManager.getCategories(targetArticle);
            AlgorithmManager.setRelevance(currentPage);
            // An ArrayList for storing the links to check through.
            ArrayList<String> links;
            ArrayList<String> path = new ArrayList<>();
            ArrayList<Integer> pathLength = new ArrayList<>();
            String randomChoice;
            // Initializing our current page.
            here:
            while (true) {
                System.out.print(currentPage + " -> ");
                while (!currentPage.equals(targetArticle)) {
                    path.add(currentPage);
                    pathLength.add(1);
                    // Getting the links from the current Wikipedia article.
                    links = algorithmManager.getLinks(currentPage);
                    AlgorithmManager.setDistance(currentPage, "1");
                    // Looping through the links and setting their relevance.
                    for (String link : links) {
                        if (path.contains(link)) {
                            if (links.size() == 1) {
                                AlgorithmManager.setRelevance(link);
                                choices = choices.add(algorithmManager.checkRelevance(link) + (100 / algorithmManager.checkDistance(link)), link);
                            }
                            continue;
                        }
                        if (link.equals(targetArticle)) {
                            System.out.println(targetArticle);
                            AlgorithmManager.writeToFile();
                            continue here;
                        }
                        AlgorithmManager.setRelevance(link);
                        choices = choices.add(algorithmManager.checkRelevance(link) + (100 / algorithmManager.checkDistance(link)), link);
                    }
                    // Iterating the distance we've gone so far.
                    for (int i = 0; i < path.size(); i++) {
                        pathLength.set(i, pathLength.get(i) + 1);
                    }
                    // Saving the data we have so far.
                    AlgorithmManager.writeToFile();
                    randomChoice = choices.next();
                    AlgorithmManager.setRelevance(randomChoice);
                    currentPage = randomChoice;
                    System.out.print(randomChoice + " -> ");
                    choices.clear();
                }
                for (int i = 0; i < path.size(); i++) {
                    AlgorithmManager.setDistance(path.get(i), Integer.toString(pathLength.get(i)));
                }
                AlgorithmManager.writeToFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

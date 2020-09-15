package Finder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class HitlerFinder {

    /**************************************************
     HitlerFinder. By Kaleb Burris, 2020. Public Domain.
     ***************************************************/
    public static void main(String[] args) throws IOException {
        // This is our article we want to find.
        String targetArticle = "https://en.wikipedia.org/wiki/Adolf_Hitler";
        // Storing the random article url in order to simply things.
        String randomArticle = "https://en.wikipedia.org/wiki/Special:Random";
        // Our manager for all of the paths we will go through.
        AlgorithmManager algorithmManager = new AlgorithmManager(targetArticle);

        try {
            RandomCollection<String> choices = new RandomCollection<>();
            // Getting the target's article.
            Document target = Jsoup.connect(targetArticle).get();
            algorithmManager.getCategories(targetArticle);
            // An ArrayList for storing the links to check through.
            ArrayList<String> links = new ArrayList<>();
            ArrayList<String> path = new ArrayList<>();
            // Initializing our current page.
            Document currentPage = Jsoup.connect(randomArticle).get();
            AlgorithmManager.setRelevance(currentPage.location());
            String randomChoice = "";
            System.out.print(currentPage.location() + " -> ");
            while (!currentPage.location().equals(targetArticle)) {
                path.add(currentPage.location());
                // Getting the links from the current Wikipedia article.
                links = algorithmManager.getLinks(currentPage.location());
                AlgorithmManager.setDistance(currentPage.location(), "1");
                // Looping through the links and setting their relevance.
                for (String link : links) {
                    AlgorithmManager.setRelevance(link);
                    choices.add(algorithmManager.checkRelevance(link) / algorithmManager.checkDistance(link), link);
                }
                // Iterating the distance we've gone so far.
                for (String location : path) {
                    AlgorithmManager.increaseDistance(location);
                }
                // Saving the data we have so far.
                AlgorithmManager.writeToFile();
                randomChoice = choices.next();
                currentPage = Jsoup.connect(randomChoice).get();
                AlgorithmManager.setRelevance(currentPage.location());
                System.out.print(randomChoice + " -> ");
                choices.clear();
            }
            System.out.println(targetArticle);
            AlgorithmManager.writeToFile();
            return;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

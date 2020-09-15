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
            algorithmManager.getCategories(targetArticle);
            // An ArrayList for storing the links to check through.
            ArrayList<String> links;
            ArrayList<String> path = new ArrayList<>();
            String randomChoice;
            // Initializing our current page.
            here: while(true) {
                Document currentPage = Jsoup.connect(randomArticle).get();
                String linkName = AlgorithmManager.setRelevanceString(currentPage.location());
                System.out.print(currentPage.location() + " -> ");
                while (!currentPage.location().equals(targetArticle)) {
                    path.add(currentPage.location());
                    // Getting the links from the current Wikipedia article.
                    links = algorithmManager.getLinks(currentPage);
                    AlgorithmManager.setDistance(currentPage.location(), "1");
                    // Looping through the links and setting their relevance.
                    for (String link : links) {
                        if(path.contains(link)) {
                            if(link.equals(targetArticle)){
                                System.out.println(targetArticle);
                                AlgorithmManager.writeToFile();
                                continue here;
                            }
                            if (links.size() == 1){
                                linkName = AlgorithmManager.setRelevanceString(link);
                                choices.add((double) ((algorithmManager.checkRelevance(linkName) + (100 / algorithmManager.checkDistance(linkName)))), link);
                            }
                            continue;
                        }
                        linkName = AlgorithmManager.setRelevanceString(link);
                        choices.add((double) (algorithmManager.checkRelevance(linkName) / algorithmManager.checkDistance(linkName)), link);
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

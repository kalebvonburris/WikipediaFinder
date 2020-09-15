package Finder;

import org.fastily.jwiki.core.NS;
import org.fastily.jwiki.core.Wiki;

import java.io.*;
import java.util.ArrayList;

public class AlgorithmManager {

    private static ArrayList<String> Categories = new ArrayList<>();
    private static final ArrayList<String> ArticleName = new ArrayList<>();
    private static final ArrayList<String> ArticleRelevance = new ArrayList<>();
    private static final ArrayList<String> ArticleDistance = new ArrayList<>();
    private static final Wiki wiki = new Wiki.Builder().withDefaultLogger(false).withDebug(false).build();

    // The constructor.
    public AlgorithmManager() {
        // Reading data.txt (if it exists).
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line;
            // Loops through data.txt and copies the data line by line to Articles[][].
            while ((line = reader.readLine()) != null && line.length() != 0) {
                String[] tempArray = line.split("&&");
                ArticleName.add(tempArray[0]);
                ArticleRelevance.add(tempArray[1]);
                ArticleDistance.add(tempArray[2]);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // A function to save all of our information to a file for future use.
    public static void writeToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"));
            StringBuilder completeList = new StringBuilder();
            for (int i = 0; i < ArticleName.size(); i++) {
                if (ArticleName.get(i) == null) {
                    writer.write(completeList.toString());
                    writer.close();
                    return;
                }
                if (ArticleName.get(i).equals("Book sources")) {
                    continue;
                }
                completeList.append(ArticleName.get(i)).append("&&").append(ArticleRelevance.get(i)).append("&&").append(ArticleDistance.get(i)).append("\n");
            }
            writer.write(completeList.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sets the relevance to the target article via category.
    public static void setRelevance(String wikiName) {
        for (int i = 0; i < ArticleName.size() - 1; i++) {
            if (ArticleName.get(i).equals(wikiName)) {
                return;
            }
        }
        int relatedArticles = 0;
        ArrayList<String> relatedCategories = wiki.getCategoriesOnPage(wikiName);
        for(String category : relatedCategories){
            if(Categories.contains(category))
                relatedArticles++;
        }
        ArticleName.add(wikiName);
        ArticleRelevance.add(Integer.toString(relatedArticles));
        ArticleDistance.add("100");
    }

    // Sets the distance for an article to a desired value.
    public static void setDistance(String wikiName, String distance) throws IOException {
        // If the article has yet to be run through setRelevance(), we return an error because we've never seen it.
        for (int i = 0; i < ArticleName.size(); i++) {
            if (ArticleName.get(i).equals(wikiName)) {
                ArticleDistance.set(i, distance);
                return;
            }
        }
        System.out.println("\nError: Article " + wikiName + " hasn't had its relevance set.");
        throw new IOException();
    }

    // Finds the categories the article falls under.
    public void getCategories(String targetArticle) {
        Categories = wiki.getCategoriesOnPage(targetArticle);
    }

    // Checks to see how relevant an article is to the target.
    public int checkRelevance(String wikiName) throws IOException {
        // If the article has yet to be run through setRelevance(), we return an error because we've never seen it.
        for (int i = 0; i < ArticleName.size(); i++) {
            if (ArticleName.get(i).equals(wikiName)) {
                return Integer.parseInt(ArticleRelevance.get(i));
            }
        }
        System.out.println("\nError: Article " + wikiName + " hasn't had its relevance set.");
        throw new IOException();
    }

    // Checks to see how relevant an article is to the target.
    public int checkDistance(String wikiName) throws IOException {
        // If the article has yet to be run through setRelevance(), we return an error because we've never seen it.
        for (int i = 0; i < ArticleName.size(); i++) {
            if (ArticleName.get(i).equals(wikiName)) {
                return Integer.parseInt(ArticleDistance.get(i));
            }
        }
        System.out.println("\nError: Article " + wikiName + " hasn't had its relevance set.");
        throw new IOException();
    }

    public ArrayList<String> getLinks(String wikiName) {
        ArrayList<String> links = wiki.getLinksOnPage(wikiName, NS.MAIN);
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).contains("Help:") || links.get(i).contains("File:") || links.get(i).contains("#") || links.get(i).contains("Template:") || links.get(i).contains("Wikipedia:") || links.get(i).contains("Category:") || links.get(i).contains("Portal:") || links.get(i).contains("Template_talk:") || links.get(i).contains("Template talk:")) {
                links.remove(i);
                i--;
            }
        }
        return links;
    }
}
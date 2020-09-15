package Finder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;

public class AlgorithmManager {

    private static final ArrayList<String> Categories = new ArrayList<>();
    private static String[][] Articles = new String[1][3];
    private static String targetName;

    // The constructor.
    public AlgorithmManager(String targetArticle) throws IOException {
        Document target = Jsoup.connect(targetArticle).get();
        targetName = target.select("#firstHeading").text();
        // Reading data.txt (if it exists).
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line;
            // Loops through data.txt and copies the data line by line to Articles[][].
            while ((line = reader.readLine()) != null && line.length() != 0) {
                String[][] tempString = new String[Articles.length + 1][3];
                for (int i = 0; i < Articles.length; i++) {
                    tempString[i][0] = Articles[i][0];
                    tempString[i][1] = Articles[i][1];
                    tempString[i][2] = Articles[i][2];
                }
                Articles = tempString;
                String[] tempArray = line.split("&&");
                Articles[Articles.length - 2][0] = tempArray[0];
                Articles[Articles.length - 2][1] = tempArray[1];
                Articles[Articles.length - 2][2] = tempArray[2];
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
            for (String[] article : Articles) {
                if (article[0] == null) {
                    writer.write(completeList.toString());
                    writer.close();
                    return;
                }
                if (article[0] == "Book sources") {
                    continue;
                }
                completeList.append(article[0]).append("&&").append(article[1]).append("&&").append(article[2]).append("\n");
            }
            System.out.println("Articles.length: " + Articles.length);
            writer.write(completeList.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sets the relevance to the target article via category.
    public static void setRelevance(String url) throws IOException {
        // Getting the target's article.
        Document target = Jsoup.connect(url).get();

        // Grabbing the script which declares the categories
        Elements ElementsCategories = target.getElementsByTag("script");

        targetName = target.select("#firstHeading").text();

        for (String[] article : Articles) {
            if (article[0].equals(targetName)) {
                return;
            }
        }

        String[] SplitStrings;
        // Grabbing the Categories
        Element ElementCategories = ElementsCategories.first();
        // Big ol thing that grabs the category section. Declaring ScriptString first in case the article doesn't have a "wgCategories" section.
        String ScriptString;
        // If it doesn't have a "wgCategories" section, we just store it with 0 relevant categories.
        try {
            ScriptString = ElementCategories.data().substring(ElementCategories.data().indexOf("[\"", ElementCategories.data().indexOf("wgCategories")), ElementCategories.data().indexOf("\"],", ElementCategories.data().indexOf("wgCategories")));
        } catch (Exception e) {
            String[][] tempString = new String[Articles.length + 1][3];
            for (int i = 0; i < Articles.length; i++) {
                tempString[i][0] = Articles[i][0];
                tempString[i][1] = Articles[i][1];
                tempString[i][2] = Articles[i][2];
            }
            Articles = tempString;
            Articles[Articles.length - 2][0] = targetName;
            Articles[Articles.length - 2][1] = "0";
            Articles[Articles.length - 2][2] = "1";
            return;
        }
        // Splitting the string so that we can get the categories.
        SplitStrings = ScriptString.split("\",\"");
        // Cleaning the Categories up because Wikipedia is mean.
        int relatedArticles = 0;
        for (String splits : SplitStrings) {
            splits = splits.replace("\"", "");
            splits = splits.replace(",", "");
            splits = splits.replace("[", "");
            splits = splits.replace("]", "");
            // Adding the Categories.
            if (Categories.contains(splits)) {
                relatedArticles++;
            }
        }
        // Some minor pointer shenanigans and setting the article up with its relevance.
        String[][] tempString = new String[Articles.length + 1][3];
        for (int i = 0; i < Articles.length; i++) {
            tempString[i][0] = Articles[i][0];
            tempString[i][1] = Articles[i][1];
            tempString[i][2] = Articles[i][2];
        }
        Articles = tempString;
        Articles[Articles.length - 2][0] = targetName;
        Articles[Articles.length - 2][1] = Integer.toString(relatedArticles);
        Articles[Articles.length - 2][2] = "100";
    }

    public static String setRelevanceString(String url) throws IOException {
        // Getting the target's article.
        Document target = Jsoup.connect(url).get();

        // Grabbing the script which declares the categories
        Elements ElementsCategories = target.getElementsByTag("script");

        targetName = target.select("#firstHeading").text();

        for (String[] article : Articles) {
            if (article.equals(targetName)) {
                return targetName;
            }
        }

        String[] SplitStrings;
        // Grabbing the Categories
        Element ElementCategories = ElementsCategories.first();
        // Big ol thing that grabs the category section. Declaring ScriptString first in case the article doesn't have a "wgCategories" section.
        String ScriptString;
        // If it doesn't have a "wgCategories" section, we just store it with 0 relevant categories.
        try {
            ScriptString = ElementCategories.data().substring(ElementCategories.data().indexOf("[\"", ElementCategories.data().indexOf("wgCategories")), ElementCategories.data().indexOf("\"],", ElementCategories.data().indexOf("wgCategories")));
        } catch (Exception e) {
            String[][] tempString = new String[Articles.length + 1][3];
            for (int i = 0; i < Articles.length; i++) {
                tempString[i][0] = Articles[i][0];
                tempString[i][1] = Articles[i][1];
                tempString[i][2] = Articles[i][2];
            }
            Articles = tempString;
            Articles[Articles.length - 2][0] = targetName;
            Articles[Articles.length - 2][1] = "0";
            Articles[Articles.length - 2][2] = "100";
            return targetName;
        }
        // Splitting the string so that we can get the categories.
        SplitStrings = ScriptString.split("\",\"");
        // Cleaning the Categories up because Wikipedia is mean.
        int relatedArticles = 0;
        for (String splits : SplitStrings) {
            splits = splits.replace("\"", "");
            splits = splits.replace(",", "");
            splits = splits.replace("[", "");
            splits = splits.replace("]", "");
            // Adding the Categories.
            if (Categories.contains(splits)) {
                relatedArticles++;
            }
        }
        // Some minor pointer shenanigans and setting the article up with its relevance.
        String[][] tempString = new String[Articles.length + 1][3];
        for (int i = 0; i < Articles.length; i++) {
            tempString[i][0] = Articles[i][0];
            tempString[i][1] = Articles[i][1];
            tempString[i][2] = Articles[i][2];
        }
        Articles = tempString;
        Articles[Articles.length - 2][0] = targetName;
        Articles[Articles.length - 2][1] = Integer.toString(relatedArticles);
        Articles[Articles.length - 2][2] = "100";
        return targetName;
    }

    // Increments the distance to the target article for the given url.
    public static void increaseDistance(String url) throws IOException {
        // Getting the target's article.
        Document target = Jsoup.connect(url).get();
        targetName = target.select("#firstHeading").text();
        // If the article has yet to be run through setRelevance(), we return an error because we've never seen it.
        for (int i = 0; i < Articles.length - 1; i++) {
            if (Articles[i][0].equals(targetName)) {
                Articles[i][2] = Integer.toString(Integer.parseInt(Articles[i][2]) + 1);
                return;
            }
        }
        System.out.println("Error: Article hasn't had its relevance set.");
        throw new IOException();
    }

    // Sets the distance for an article to a desired value.
    public static void setDistance(String location, String distance) throws IOException {
        // Getting the target's article.
        Document target = Jsoup.connect(location).get();
        targetName = target.select("#firstHeading").text();
        // If the article has yet to be run through setRelevance(), we return an error because we've never seen it.
        for (int i = 0; i < Articles.length; i++) {
            if (Articles[i][0].equals(targetName)) {
                Articles[i][2] = distance;
                return;
            }
        }
        System.out.println("\nError: Article " + targetName + " hasn't had its relevance set.");
        throw new IOException();
    }

    // Finds the categories the article falls under.
    public void getCategories(String targetArticle) throws IOException {
        // Getting the target's article.

        Document target = Jsoup.connect(targetArticle).get();

        // Grabbing the script which declares the categories.
        Elements ElementsCategories = target.getElementsByTag("script");
        // Getting the target's name.
        targetName = target.select("#firstHeading").text();
        // Grabbing the Categories
        Element ElementCategories = ElementsCategories.first();
        // Big ol thing that grabs the category section.
        String ScriptString = ElementCategories.data().substring(ElementCategories.data().indexOf("[\"", ElementCategories.data().indexOf("wgCategories")), ElementCategories.data().indexOf("\"],", ElementCategories.data().indexOf("wgCategories")));
        // Splitting the string so that we can get the categories.
        String[] SplitStrings = ScriptString.split("\",\"");
        // Cleaning the Categories up because Wikipedia is mean.
        for (String splits : SplitStrings) {
            splits = splits.replace("\"", "");
            splits = splits.replace(",", "");
            // Adding the Categories.
            Categories.add(splits);
        }
    }

    // Checks to see how relevant an article is to the target.
    public int checkRelevance(String targetName) throws IOException {
        // If the article has yet to be run through setRelevance(), we return an error because we've never seen it.
        for (int i = 0; i < Articles.length - 1; i++) {
            if (Articles[i][0].equals(targetName)) {
                return Integer.parseInt(Articles[i][1]);
            }
        }
        System.out.println("\nError: Article " + targetName + " hasn't had its relevance set.");
        throw new IOException();
    }

    // Checks to see how relevant an article is to the target.
    public int checkDistance(String targetName) throws IOException {
        // If the article has yet to be run through setRelevance(), we return an error because we've never seen it.
        for (String[] article : Articles) {
            if (article[0].equals(targetName)) {
                return Integer.parseInt(article[2]);
            }
        }
        System.out.println("\nError: Article " + targetName + " hasn't had its relevance set.");
        throw new IOException();
    }

    public ArrayList<String> getLinks(String url) throws IOException {
        ArrayList<String> returnLinks = new ArrayList<>();

        Document finder = Jsoup.connect(url).get();
        Element body = finder.getElementById("content");
        Elements links = body.select("a[href]");
        String tempLink;
        for (Element link : links) {
            tempLink = link.attr("abs:href");
            if (!tempLink.contains("https://en.wikipedia.org/wiki/") || tempLink.contains("Help:") || tempLink.contains("File:") || tempLink.contains("#") || tempLink.contains("Template:") || tempLink.contains("Wikipedia:") || tempLink.contains("Category:") || returnLinks.contains(tempLink) || tempLink.contains("Portal:") || tempLink.contains("Template_talk:")) {
                continue;
            }
            returnLinks.add(tempLink);
        }
        return returnLinks;
    }
}
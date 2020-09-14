package Finder;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AlgorithmManager {

    // Stores the targeted article.
    String targetArticle;
    String targetName;

    public ArrayList<String> Categories = new ArrayList<>();

    public String[][] Articles = new String[1][3];

    // The constructor.
    public AlgorithmManager(String targetArticle) throws IOException {
        this.targetArticle = targetArticle;
        Document target = Jsoup.connect(targetArticle).get();
        this.targetName = target.select("#firstHeading").text();
    }

    // Finds the categories the article falls under.
    public void getCategories(String targetArticle) throws IOException {
        // Getting the target's article.
        
        Document target = Jsoup.connect(targetArticle).get();

        // Grabbing the script which declares the categories
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
        for(String splits : SplitStrings){
            splits = splits.replace("\"", "");
            splits = splits.replace(",", "");
            // Adding the Categories.
            Categories.add(splits);
        }
    }

    // Sets the relevance to the target article via category.
    public void setRelevance(String url) throws IOException{
        // Getting the target's article.
        Document target = Jsoup.connect(url).get();

        // Grabbing the script which declares the categories
        Elements ElementsCategories = target.getElementsByTag("script");

        targetName = target.select("#firstHeading").text();

        if(Arrays.asList(Articles).contains(targetName)){
            return;
        }

        String[] SplitStrings;
        // Grabbing the Categories
        Element ElementCategories = ElementsCategories.first();
        // Big ol thing that grabs the category section. Declaring ScriptString first in case the article doesn't have a "wgCategories" section.
        String ScriptString = "";
        // If it doesn't have a "wgCategories" section, we just store it with 0 relevant categories.
        try {
            ScriptString = ElementCategories.data().substring(ElementCategories.data().indexOf("[\"", ElementCategories.data().indexOf("wgCategories")), ElementCategories.data().indexOf("\"],", ElementCategories.data().indexOf("wgCategories")));
        } catch(Exception e){
            String[][] temp = Articles;
            Articles = new String[Articles.length + 1][3];
            Articles = temp;
            Articles[Articles.length - 1][0] = targetName;
            Articles[Articles.length - 1][1] = "0";
            return;
        }
        // Splitting the string so that we can get the categories.
        SplitStrings = ScriptString.split("\",\"");
        // Cleaning the Categories up because Wikipedia is mean.
        int i = 0;
        for(String splits : SplitStrings) {
            splits = splits.replace("\"", "");
            splits = splits.replace(",", "");
            splits = splits.replace("[", "");
            // Adding the Categories.
            if(this.Categories.contains(splits)){
                i++;
            }
        }
        // Some minor pointer shenanigans and setting the article up with its relevance.
        String[][] temp = Articles;
        Articles = new String[Articles.length + 1][3];
        Articles = temp;
        Articles[Articles.length - 1][0] = targetName;
        Articles[Articles.length - 1][1] = Integer.toString(i);
    }

    // Checks to see how relevant an article is to the target.
    public int checkRelevance(String url) throws IOException{
        // Getting the target's article.
        Document target = Jsoup.connect(url).get();

        targetName = target.select("#firstHeading").text();
        // If the article has yet to be run through setRelevance(), we return an error because we've never seen it.
        if(!Arrays.asList(Articles).contains(targetName)){
            System.out.println("Error: Article hasn't had its relevance set.");
            throw new IOException();
        }
        // Looks through the Articles[][] array for the appropriate article and returns the relevance.
        for (int indexOfTarget = 0; indexOfTarget < Articles.length; indexOfTarget++)
            if (target.equals(Articles[indexOfTarget][0]))
                return Integer.parseInt(Articles[indexOfTarget][1]);
        return -1;
    }
}
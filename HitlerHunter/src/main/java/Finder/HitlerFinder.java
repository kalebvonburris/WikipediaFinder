package Finder;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HitlerFinder {

    /**************************************************
    HitlerFinder. By Kaleb Burris, 2020. Public Domain.
    ***************************************************/
    public static void main(String args[]) throws IOException {
        // This is our article we want to find.
        String targetArticle = "https://en.wikipedia.org/wiki/Adolf_Hitler";
        // Storing the random article url in order to simply things.
        String randomArticle = "https://en.wikipedia.org/wiki/Special:Random";
        // Our manager for all of the paths we will go through.
        AlgorithmManager algorithmManager = new AlgorithmManager(targetArticle);

        try {
            // Getting the target's article.
            Document target = Jsoup.connect(targetArticle).get();
            algorithmManager.getCategories(targetArticle);

            Document finder = null;

            while(finder != target) {
                finder = Jsoup.connect(randomArticle).get();
                Element body = finder.getElementById("content");
                Elements links = body.select("a[href]");
                String tempLink = "";
                for (Element link : links) {
                    tempLink = link.attr("abs:href");
                    if (!tempLink.contains("https://en.wikipedia.org/wiki/") || tempLink.contains("Help:") || tempLink.contains("File:") || tempLink.contains("#") || tempLink.contains("Template:") || tempLink.contains("Wikipedia:") || tempLink.contains("Category:")) {
                        continue;
                    }
                    System.out.println(tempLink);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

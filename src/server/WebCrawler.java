package server;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class WebCrawler {
    public static boolean checkurl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String[] getPageLinks(String URL) {
        String[] output = { "Not an URL", "END", "END", "END", "END", "END", "END", "END", "END", "END", "END", "END" };
        if (checkurl(URL)) {
            try {
                // 2. Fetch the HTML code
                Document document = Jsoup.connect(URL).get();
                // 3. Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");

                // 5. For each extracted URL... go back to Step 4.
                int count = 0;
                for (Element page : linksOnPage) {
                    if (count < 10) {
                        output[count] = page.attr("abs:href");
                        // System.out.println(output[count]);
                        count++;
                    } else {
                        break;
                    }
                    // getPageLinks(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
        return output;
    }

}
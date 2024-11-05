import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.time.Duration;

import java.util.*;

import java.net.HttpURLConnection;
import java.io.IOException;
import java.net.URL;


public class Utilities
{

    private WebDriver driver;
    private List<WebElement> links;
    private Map<String,Map> brokenLinks;
    private Map<String,Map> connectedLinks;
    private int count_Kaput_links;

    public Utilities(WebDriver driver)
    {
        this.driver = driver;
        this.brokenLinks = new LinkedHashMap<>();
        this.connectedLinks = new LinkedHashMap<>();
        this.count_Kaput_links = 0;

    }



    public void lunch( String url)
    {
        driver.get(url);
        driver.manage().window().maximize();
    }

    public void close()
    {
        driver.quit();
    }

    public void wait( int seconds )
    {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }

    public WebDriverWait wait_for( int seconds )
    {
        return new WebDriverWait( driver , Duration.ofSeconds(seconds) );
    }


    public void locate_link_elements ( By loater ) throws InterruptedException
    {
        try
        {
            this.links = wait_for(20).until(ExpectedConditions.presenceOfAllElementsLocatedBy(loater));
        }
        catch (StaleElementReferenceException e)
        {
            Thread.sleep(2000);
            this.links = wait_for(20).until(ExpectedConditions.presenceOfAllElementsLocatedBy(loater));
        }
    }


    public void click_element ( By loater )
    {
        WebElement btn = wait_for(20).until(ExpectedConditions.visibilityOfElementLocated(loater));
        btn.click();
    }



    public void get_broken_links () throws InterruptedException, IOException {


        for (WebElement link : links)
        {
            String url = "unkown";
            String link_text = " unknown ";

            try
            {
                url = link.getAttribute("href");
                link_text = link.getAttribute("data-zjs-component_text");
            }
            catch (StaleElementReferenceException e)
            {
                links = driver.findElements(By.tagName("a"));
            }


            // Check if the URL is valid
            if (url == null || !url.startsWith("http"))
            {
                count_Kaput_links++;
                continue;
            }

            

            Map responses = new LinkedHashMap<String , Integer>();

            if (url != null)
            {
                try
                {
                    // Create a connection to the URL
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    // Get the response code
                    int responseCode = connection.getResponseCode();

                    // Check if the response code indicates a broken link

                    if ( responseCode >= 400 )
                    {
                        responses.put(url, responseCode);
                        brokenLinks.put(link_text, responses);
                    }
                    else
                    {
                        responses.put(url, responseCode);
                        connectedLinks.put(link_text, responses);
                    }
                }
                catch ( Exception e)
                {
                    
                     System.out.println("Error checking link: " + url + " - " + e.getMessage());
                    
                }
            }
        }


    }


    public void print_broken_links ()
    {

        System.out.println("\n\n************** ***************** *************\n");
        System.out.println("\nList of broken links:\n");


        if (brokenLinks.size() == 0)
        {
            System.out.println("\n************** NO Broken Links Were Found *************\n");
        }
        else
        {
            System.out.printf("%-30s %-60s %-10s%n", "Link Text", "URL", "Response Code");
            System.out.println("---------------------------------------------------------------------------------------------------------");
            for (Map.Entry<String, Map> brokenLink : brokenLinks.entrySet())
            {

                String linktext = brokenLink.getKey();

                Map<String, Integer> responses = brokenLink.getValue();

                for (Map.Entry<String, Integer> response : responses.entrySet())
                {
                    String url = response.getKey();
                    int responseCode = response.getValue();
                    System.out.printf("%-30s %-60s %-10d%n", linktext, url, responseCode);
                }


            }
        }
    }



    public void print_connected_links ()
    {

        System.out.println("\n\n************** ***************** *************\n");
        System.out.println("\nList of connected links:\n");


        if (connectedLinks.size() == 0)
        {
            System.out.println("\n************** NO Connected Links Were Found *************\n");
        }
        else
        {
            System.out.printf("%-30s %-60s %-10s%n", "Link Text", "URL", "Response Code"); // Table header
            System.out.println("---------------------------------------------------------------------------------------------------------");
            for (Map.Entry<String, Map> validLink : connectedLinks.entrySet())
            {

                String linktext = validLink.getKey();

                Map<String, Integer> responses = validLink.getValue();


                for (Map.Entry<String, Integer> response : responses.entrySet()) {
                    String url = response.getKey();
                    int responseCode = response.getValue();
                    System.out.printf("%-30s %-60s %-10d%n", linktext, url, responseCode);
                }


            }
        }
    }


    public void print_invalid_links ()
    {

        System.out.println("\n\n************** ***************** *************\n");
        System.out.println("\nList of invalid links:\n");


            if (count_Kaput_links == 0)
            {
                System.out.println("\n************** NO Invalid Links Were Found *************\n");
            }
            else
            {
                System.out.print("::  __Total Invalid Links Found__  :: ");
                System.out.print("\t ===>\t");
                System.out.println(count_Kaput_links);



            }

    }




}

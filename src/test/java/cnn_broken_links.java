    import org.openqa.selenium.By;
    import org.openqa.selenium.WebDriver;
    import org.openqa.selenium.chrome.ChromeDriver;

    import java.io.IOException;

    public class cnn_broken_links
    {


        public static void main(String[] args) throws InterruptedException, IOException
        {

            WebDriver driver = new ChromeDriver();

            Utilities util = new Utilities(driver);

            util.lunch("https://edition.cnn.com/");

            util.wait(20);

            util.click_element(By.xpath("//button[@id='onetrust-reject-all-handler']"));

            util.locate_link_elements(By.tagName("a"));

            util.get_broken_links();

            util.print_valid_links();

            util.print_broken_links();

            util.close();




        }
    }

package iso.network;

import java.net.*;
import java.io.*;

/* @author      PureIso
 * @version     0.2 */
public class Connection{

    /**Check Internet Connection through a dummy request
     * @return boolean -- True for Internet Connection present and False for offline connection*/
    public static boolean CheckInternet()
    {
        boolean connection = true;
        try 
        {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");
            
            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

            /*trying to retrieve data from the source. 
             * If there is no connection, this line will fail*/
            urlConnect.getContent();
        
        } 
        catch (UnknownHostException e) 
        {
            return false;
        }
        catch (IOException e) 
        {
            return false;
        }
        return connection;
    }
    
    /**Check Intranet Connection through a dummy request
     * @return boolean -- True for Intranet Connection present and False for offline connection*/
    public static boolean CheckIntranet()
    {
        boolean connection = true;
        try 
        {
            //make a URL to a known source
            URL url = new URL("http://127.0.0.1/");
            
            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

            /*trying to retrieve data from the source. 
             * If there is no connection, this line will fail*/
            urlConnect.getContent();
        } 
        catch (UnknownHostException e) 
        {
            return false;
        }
        catch (IOException e) 
        {
            return false;
        }
        return connection;
    }
}

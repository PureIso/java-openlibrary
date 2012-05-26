package iso.network;

import java.net.*;
import javax.swing.*;

/**@author      PureIso
 * @version     0.2 */
public class IPAddress
{
    /**Returns the WAN IP Address of your network
     * @return String - The WANIP address of your network parse from a url*/
    public static String returnWANIP()
    {
        String wanIP = null;
        try
        {
            java.net.URL url = new java.net.URL("http://automation.whatismyip.com/n09230945.asp");

            java.net.HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
            java.io.InputStream stream = con.getInputStream();
            java.io.InputStreamReader reader = new java.io.InputStreamReader(stream);

            java.io.BufferedReader bReader = new java.io.BufferedReader(reader);

            wanIP = bReader.readLine();
        }
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null,e.getMessage(),"Get WAN IP Error",JOptionPane.ERROR_MESSAGE);
        }
        return wanIP;
    }
}

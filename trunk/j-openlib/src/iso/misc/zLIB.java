package iso.misc;

import java.io.File; //Used to Create a File
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane; //For Error MessageBox
import java.io.RandomAccessFile; //For Opening File - Reader/Write
import java.util.zip.Deflater; //Compress
import java.util.zip.Inflater; //Decompress
/*Exception Catching*/
import java.io.IOException; // IO Exception Catching
import java.util.zip.DataFormatException; //Zip Exception

/** @author      PureIso
 * @version     0.2 */
public class zLIB
{
    private RandomAccessFile rand;
    
    /** zLIB compression
     * @param path - This is the path/the location of the file you want to compress*/
    public zLIB(String path)
    {
        try
        {
            File file = new File(path);
            if(!file.exists())
            {
                JOptionPane.showMessageDialog(null,"File Does Not Exist!","File Reader Error",JOptionPane.ERROR_MESSAGE);  
            }
            else
            {
                this.rand = new RandomAccessFile(path,"rw");
            }
        }
        catch (IOException e)
            {
                JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    
    /**zLIB Compression - Deflater
     * Will Compress a given file, the file will be overwritten with the compression*/
    public void Compress()
    {
        try
        {
            int len = (int)rand.length();
            byte[] input = new byte[len];
            rand.read(input,0,len);
            
            // Compress the bytes
            //plus the adler32 checksum 4 bytes
            byte[] output = new byte[(int)len+4];
            Deflater compresser = new Deflater();
            compresser.setInput(input);
            compresser.finish();
            compresser.deflate(output);
            
            rand.seek(0);
            rand.write(output);
            rand.close();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        catch (NullPointerException e)
        {
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
            try {
                rand.close();
            } catch (IOException ex) {
                Logger.getLogger(zLIB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }  
    
    /**zLIB Compression - Inflater
     * Will Decompress a given file, the compressed file will be overwritten.*/
    public void Decompress()
    {
        int coveredData = 0;
        try
        {
            coveredData = (int)rand.length();
            byte[] input = new byte[coveredData];
            
            rand.seek(0); //Compressed Data
            rand.read(input,0,coveredData);
                
            // Decompress the bytes
            Inflater decompresser = new Inflater();
            decompresser.setInput(input);
                
            //minus the adler32 checksum 4 bytes
            byte[] result = new byte[coveredData-4];
            decompresser.inflate(result);
            decompresser.end(); 
            
            rand.seek(0);
            rand.write(result);
            rand.close();
        }
        catch (IOException e)
        {
            //Catch exception if any
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        catch (DataFormatException e)
        {
            //Catch exception if any
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        catch (NullPointerException e)
        {
            //Catch exception if any
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
            try {
                rand.close();
            } catch (IOException ex) {
                Logger.getLogger(zLIB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**zLIB Compression - Deflater
     * @param value - This is the byte that will be compressed
     * @return bytes[] - This is the compressed byte array that will be returned*/
    public static byte[] Compress(byte[] value)
    {
        byte[] output = null;
        try
        {
            //Compress the bytes
            //plus the adler32 checksum 4 bytes
            output = new byte[(value.length)+4];
            Deflater compresser = new Deflater();
            compresser.setInput(value);
            compresser.finish();
            compresser.deflate(output);  
        }
        catch (Exception e)
        {
            //Catch exception if any
            JOptionPane.showMessageDialog(null,e,"Compression Error",JOptionPane.ERROR_MESSAGE);
        }
        return output;
    } 
    
    /**zLIB Decompression - Inflater
     * @param value - This is the byte that will be decompressed
     * @return bytes[] - This is the decompressed byte array that will be returned*/
    public static byte[] Decompress(byte[] value)
    {
        byte[] result = null;
        try
        {
            // Decompress the bytes
            Inflater decompresser = new Inflater();
            decompresser.setInput(value);
                
            //minus the adler32 checksum 4 bytes
            result = new byte[(value.length)-4];
            decompresser.inflate(result);
            decompresser.end(); 
        }
        catch (Exception e)
        {
            //Catch exception if any
            JOptionPane.showMessageDialog(null,e,"Decompression Error",JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }
}

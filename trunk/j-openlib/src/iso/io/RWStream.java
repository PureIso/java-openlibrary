/**Put Byte array into stream
 */
package iso.io;

import iso.io.Conversions.CharSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.JOptionPane;
import java.util.Random;

/**@author Ola
 * @version 0.1
 */
public class RWStream 
{
    private long length;
    private long position;
    private boolean isBigEndian;
    private RandomAccessFile rand;
    private boolean accessed = false;
    private File file;
    private String path;
    private Random l = new Random();
    private String tempName = "tmp-file"+l.nextInt();
    
    /** Constructor
     * @param path     - File Path that will be used by this Object
     * @param endian   - The Endian Type the bytes will be converted to before written*/
    public RWStream(String path, boolean isBigEndian) throws Exception
    {
        file = new File(path);
        if(!file.exists())
            throw new Exception("File Does Not Exist!");
        else
        {
            try
            {
                this.path = path;
                this.position = 0;
                this.isBigEndian = isBigEndian;
                this.rand = new RandomAccessFile(path,"rw");
                this.length = file.length();
                accessed = true;
            }
            catch (IOException e)
            {
                //Catch exception if any
                JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
            }
        } 
    }
    
    /**Constructor
     * Big Endian by default
     * @param buffer - Int8 array that will be used */
    public RWStream(byte[] buffer) throws Exception
    {
        file = new File(tempName);
        if (!(file.createNewFile()))
        {
            file.delete();
            file.createNewFile();
        }
        file.deleteOnExit();
        
        this.rand = new RandomAccessFile(file.getAbsolutePath(),"rw");
        rand.write(buffer);
        isBigEndian = true;
        length = file.length();
        accessed = true;
    }
    
    /**Constructor for temporary file
     * @param isBigEndian
     * @throws Exception 
     */
    public RWStream(boolean isBigEndian) throws Exception
    {
        file = new File(tempName);
        if (!(file.createNewFile()))
        {
            file.delete();
            file.createNewFile();
        }
        file.deleteOnExit();
        
        rand = new RandomAccessFile(file.getAbsolutePath(),"rw");
        this.isBigEndian = isBigEndian;
        length = file.length();
        accessed = true;
        path = file.getAbsolutePath();
    }
    
    /**Constructor
     * @param buffer - Int8 array that will be used 
     * @param isBigEndian - boolean check*/
    public RWStream(byte[] buffer, boolean isBigEndian) throws Exception
    {
        if(!isBigEndian)
        {
            buffer = EndianSwap.swap(buffer);
        }
        file = new File(tempName);
        if (!(file.createNewFile()))
        {
            file.delete();
            file.createNewFile();
        }
        file.deleteOnExit();
        
        this.rand = new RandomAccessFile(file.getAbsolutePath(),"rw");
        rand.write(buffer);
        length = file.length();
        this.isBigEndian = isBigEndian;
        accessed = true;
    }
    
    /** close Method
     * (Required to close after you are finished with Reader)
     * Closes this random access file stream and releases any system resources 
     * associated with the stream. A closed random access file cannot perform 
     * input or output operations and cannot be reopened.*/
    public void close() throws Exception
    {
        try
        {
            rand.close();
            accessed = false;
            if (file.getName().contains("tmp-file")){
                file.delete();
            }
        }
        catch (IOException e)
        {
            throw new Exception(e);
        }
    }
    
    /**The absolute path of the file in use*/
    public String path()
    {
        return path;
    }
    
    /**if the stream is still in use
     * @return boolean*/
    public boolean accessed()
    {
        return accessed;
    }
    
    /** Seek Method
     * If you want to seek a new offset to write
     * @param value - The value the seeker will start reading from*/
    public void seek(long value)
    {
        try 
        {
            rand.seek(value);
            position = value;
        } 
        catch (IOException ex) 
        {
            JOptionPane.showMessageDialog(null,ex,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
     /** Position
     * @return Long - Position the reader is currently on*/
    public long position()
    {
        try 
        {
            return rand.getFilePointer();
        } 
        catch (IOException ex) 
        {
            JOptionPane.showMessageDialog(null,ex,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }
    
    /** Length - Is the length of the current file
     * @return long - The Long value of the length*/
    public long length()
    {
        return length;
    }
    
    /**Force all system buffers to synchronize with the underlying device. 
     * This method returns after all modified data and attributes of this 
     * FileDescriptor have been written to the relevant device(s). 
     * In particular, if this FileDescriptor refers to a physical storage medium, 
     * such as a file in a file system, sync will not return until all in-memory modified 
     * copies of buffers associated with this FileDesecriptor have been written to the 
     * physical medium. sync is meant to be used by code that requires physical storage 
     * (such as a file) to be in a known state For example, a class that provided a simple 
     * transaction facility might use sync to ensure that all changes to a file caused by a 
     * given transaction were recorded on a storage medium.*/
    public void flush()
    {
        try
        {
            rand.getFD().sync();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"RWStream Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /** ReadPlainText Method
     * @return String - The String value of the current file*/
    public String readPlainText()
    {
        //...checks on aFile are elided
        StringBuilder contents = new StringBuilder();
        try 
        {
            BufferedReader input =  new BufferedReader(new java.io.FileReader(file.getAbsolutePath()));
            try 
            {
                String line = null; //not declared within while loop
                /** readLine is a bit quirky :
                 * it returns the content of a line MINUS the newline.
                 * it returns null only for the END of the stream.
                 * it returns an empty String if two newlines appear in a row.*/
                while (( line = input.readLine()) != null){
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
            finally 
            {
                input.close();
            }
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null,ex,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                position = rand.getFilePointer();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    return contents.toString();
    }
    
    /**Get a copy of the current file
     * @return Byte[] - A copy of the file
     * @throws Exception */
    public byte[] readStream()
    {
        try
        {
            byte[] xReturn = new byte[(int)length];
            long posbefore = position();
            seek(0);
            position = 0;
            xReturn = readInt8(xReturn.length);
            position = posbefore;
            seek(position);
            return xReturn;
        }
        catch (Exception x)
        {
            JOptionPane.showMessageDialog(null,x,"readStream",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally {
            if(!(rand == null))
            try {
                position = rand.getFilePointer();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Read - The specified character set 
     * @param CharSet - The selected charset option
     * @return String - The String value of the current file*/
    public String read(CharSet charset)
    {
        byte[] value = new byte[(int)this.length - (int)position()];
        String output = null;
        try
        {
            if(charset == CharSet.ASCII)
            {
                value = readInt8(value.length);
                output = Conversions.toUSASCIIString(value);
            }
            else if(charset == CharSet.ISO88591)
            {
                value = readInt8(value.length);
                output = Conversions.toISO88591String(value);
            }
            else if(charset == CharSet.UTF16)
            {
                value = readInt8(value.length);
                output = Conversions.toUTF16String(value);
            }
            else if(charset == CharSet.UTF16BE)
            {
                value = readInt8(value.length);
                output = Conversions.toUTF16BEString(value);
            }
            else if(charset == CharSet.UTF16LE)
            {
                value = readInt8(value.length);
                output = Conversions.toUTF16LEString(value);
            }
            else if(charset == CharSet.UTF8)
            {
                value = readInt8(value.length);
                output = Conversions.toUTF8String(value);
            }
            else if(charset == CharSet.HexString)
            {
                value = readInt8(value.length);
                output = Conversions.toHexString(value);
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                position = rand.getFilePointer();
                length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return output;
    }
    
    /** Read - The specified character set 
     * @param CharSet - The selected charset option
     * @param length - The length of data to read starting from the given position/offset
     * @return String - The String value of the current file*/
    public String read(int length,CharSet charset) throws Exception
    {
        if (length>this.length)
            throw new Exception("Value Out of Bounds");
        byte[] value = new byte[length];
        String output = null;
        try
        {
            if(charset == CharSet.ASCII)
            {
                value = readInt8(length);
                output = Conversions.toUSASCIIString(value);
            }
            else if(charset == CharSet.ISO88591)
            {
                value = readInt8(length);
                output = Conversions.toISO88591String(value);
            }
            else if(charset == CharSet.UTF16)
            {
                value = readInt8(length);
                output = Conversions.toUTF16String(value);
            }
            else if(charset == CharSet.UTF16BE)
            {
                value = readInt8(length);
                output = Conversions.toUTF16BEString(value);
            }
            else if(charset == CharSet.UTF16LE)
            {
                value = readInt8(length);
                output = Conversions.toUTF16LEString(value);
            }
            else if(charset == CharSet.UTF8)
            {
                value = readInt8(length);
                output = Conversions.toUTF8String(value);
            }
            else if(charset == CharSet.HexString)
            {
                value = readInt8(length);
                output = Conversions.toHexString(value);
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return output;
    }
    
    /** Read - The specified character set 
     * @param CharSet - The selected charset option
     * @param length - The length of data to read starting from the given position/offset
     * @param position - The Integer position in the file/data the function starts reading from
     * @return String - The String value of the current file*/
    public String read(int position, int length,CharSet charset) throws Exception
    {
        if (length>this.length || position>this.length)
            throw new Exception("Value Out of Bounds");
        byte[] value = null;
        String output = null;
        rand.seek(position);
        try
        {
            if(charset == CharSet.ASCII)
            {
                value = readInt8(length);
                output = Conversions.toUSASCIIString(value);
            }
            else if(charset == CharSet.ISO88591)
            {
                value = readInt8(length);
                output = Conversions.toISO88591String(value);
            }
            else if(charset == CharSet.UTF16)
            {
                value = readInt8(length);
                output = Conversions.toUTF16String(value);
            }
            else if(charset == CharSet.UTF16BE)
            {
                value = readInt8(length);
                output = Conversions.toUTF16BEString(value);
            }
            else if(charset == CharSet.UTF16LE)
            {
                value = readInt8(length);
                output = Conversions.toUTF16LEString(value);
            }
            else if(charset == CharSet.UTF8)
            {
                value = readInt8(length);
                output = Conversions.toUTF8String(value);
            }
            else if(charset == CharSet.HexString)
            {
                value = readInt8(length);
                output = Conversions.toHexString(value);
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return output;
    }
    
    /**Read - Signed Bytes -128 - 127
     * @param length - The length of data you want to read
     * @return Signed Byte Array*/
    public byte[] readInt8(long length)
    {
        byte[] buffer = new byte[(int)length];
        try
        {
            rand.read(buffer);
            return buffer;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return buffer;
    }
    
    /**Read - Unsigned Bytes 0 - 255
     * @param length - The length of data you want to read
     * @return UnsignedSigned Byte Array*/
    public int[] readUInt8(long length)
    {
        int[] buffer = new int[(int)length];
        byte[] tb = new byte[(int)length];
        try
        {
            rand.read(tb);
            buffer = Conversions.toUInt8(tb);
            return buffer;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return buffer;
    }
    
    /**Read - Signed Bytes -128 - 127
     * @param length - The byte length of what you want to read
     * @param position - The long position to seek, which the value will start writing from
     * @return UnsignedSigned Byte Array*/
    public byte[] readInt8(long position, long length)
    {
        byte[] buffer = new byte[(int)length];
        try
        {
            rand.seek(position);
            rand.read(buffer);
            return buffer;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return buffer;
    }
    
    /**Read - Unsigned Bytes 0 - 255
     * @param length - The byte length of what you want to read
     * @param position - The long position to seek, which the value will start writing from
     * @return UnsignedSigned Byte Array*/
    public int[] readUInt8(long position, long length)
    {
        int[] buffer = new int[(int)length];
        byte[] tb = new byte[(int)length];
        try
        {
            rand.seek(position);
            rand.read(tb);
            buffer = Conversions.toUInt8(tb);
            return buffer;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return buffer;
    }
    
    /** Read Unsigned Integer 8 / Read Bytes
     * values between 0 - 255
     * @return short - The value that was read*/
    public short readUInt8()
    {
        int value = 0;
        try
        {
            value = rand.readUnsignedByte();
            return (short)value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return (short)value;
    }
    
    /** Read Integer 8 / Read Signed Bytes
     * values between -128 - 127
     * @return byte - The value that was read*/
    public byte readInt8()
    {
        byte value = 0;
        try
        {
            value = rand.readByte();
            return value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }
    
    /** Read Integer 16 / Read Short
     * values between -32768 - 32767
     * @return short - The value that was read*/
    public short readInt16()
    {
        short value = 0;
        try
        {
            value = rand.readShort();
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            return value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }
        
    /** Read Unsigned Integer 16 / Read Unsigned Short
     * values between 0 - 65535
     * @return Integer - The value that was read*/
    public int readUInt16()
    {
        int value = 0;
        try
        {
            value = rand.readUnsignedShort();
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            return value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }
    
    /** Read Unsigned Integer 24 / Read 3 bytes
     * values between 0 - 16777215
     * @return Integer - The value that was read*/
    public int readUInt24()
    {
        byte[] buffer = new byte[3];
        int value = 0;
        try
        {
            rand.read(buffer);
            value = 0;
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            for (int i = 0; i < buffer.length; i++)
            {
                value = (value << 8) + (buffer[i] & 0xff);
            }
            return value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }
    
    /** Read Integer 24 / Read 3 bytes
     * values between -8388608 - 8388607
     * @return Integer - The value that was read*/
    public int readInt24()
    {
        byte[] buffer = new byte[3];
        int value = 0;
        try
        {
            rand.read(buffer);
            value = 0;
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            for (int i = 0; i < buffer.length; i++)
            {
                value = (value << 8) + (buffer[i] & 0xff);
            }
            
            if (value > 8388607)
            {
                value = 8388608 - value;
            }
            return value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }
    
    /** Read Integer 32 / Read Integer
     * values between -2147483648 - 2147483647
     * @return Integer - The value that was read*/
    public int readInt32()
    {
        int value = 0;
        try
        {
            value = rand.readInt();
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            return value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }
    
    /** Read Unsigned Integer 32 / Read Unsigned Integer
     * values between 0 - 4294967295
     * @return Long - The value that was read*/
    public long readUInt32()
    {
        long value = 0;
        try
        {
            value = rand.readInt();
            if (value < 0)
            {
                value += 4294967296L;
            }
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            return value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }
    
    /** Read Unsigned Integer 40 / Read 5 bytes
     * values between 0 - 1099511627775
     * @return long - The value that was read*/
    public long readUInt40()
    {
        byte[] buffer = new byte[5];
        long value = 0;
        try
        {
            rand.read(buffer);
            
            value = 0;
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            for (int i = 0; i < buffer.length; i++)
            {
                value = (value << 8) + (buffer[i] & 0xff);
            }
            return value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }
    
    /** Read Integer 40 / Read 5 bytes
     * values between -549755813888 - 549755813887
     * @return long - The value that was read*/
    public long readInt40()
    {
        byte[] buffer = new byte[5];
        long value = 0;
        try
        {
            rand.read(buffer);
            
            value = 0;
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            for (int i = 0; i < buffer.length; i++)
            {
                value = (value << 8) + (buffer[i] & 0xff);
            }
            
            if (value > 549755813887L)
            {
                value = 549755813888L - value;
            }
            return value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }
    
    /** Read Integer 64 / Read Long
     * values between -9223372036854775807 - 9223372036854775807
     * @return Long - The value that was read*/
    public long readInt64()
    {
        long value = 0;
        try
        {
            value = rand.readLong();
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            return value;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }
     
     /** WritePlainText 
     * @param text - The String text that will be written in plain text*/
    public void writePlainText(String text)
    {
        try 
        {
            //use buffering
            BufferedWriter output = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            output.write(text);
            output.close();
        }
        catch (IOException e)
        {
            //Catch exception if any
            JOptionPane.showMessageDialog(null,e.getMessage(),"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write - The specified value
     * @param CharSet - The selected charset option
     * @param value - The String text that will be written in plain text*/
    public void write(String value, CharSet charset)
    {
        byte[] out = null;
        try
        {
            if(charset == CharSet.ASCII)
            {
                out = value.getBytes("US-ASCII");
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.ISO88591)
            {
                out = value.getBytes("ISO-8859-1");
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.UTF16)
            {
                out = value.getBytes("UTF-16");
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.UTF16BE)
            {
                out = value.getBytes("UTF-16BE");
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.UTF16LE)
            {
                out = value.getBytes("UTF-16LE");
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.UTF8)
            {
                out = value.getBytes("UTF-8");
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.HexString)
            {
                out = Conversions.toInt8(value);
                rand.seek(position);
                rand.write(out);
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write - The specified value
     * @param CharSet - The selected charset option
     * @param length - The length of the data to be written
     * @param value - The String text that will be written in plain text*/
    public void write(String value, CharSet charset, int length)
    {
        byte[] out = null;
        try
        {
            if(charset == CharSet.ASCII)
            {
                out = value.getBytes("US-ASCII");
                out = Conversions.resizeArray(out, 0, length);
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.ISO88591)
            {
                out = value.getBytes("ISO-8859-1");
                out = Conversions.resizeArray(out, 0, length);
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.UTF16)
            {
                out = value.getBytes("UTF-16");
                out = Conversions.resizeArray(out, 0, length);
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.UTF16BE)
            {
                out = value.getBytes("UTF-16BE");
                out = Conversions.resizeArray(out, 0, length);
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.UTF16LE)
            {
                out = value.getBytes("UTF-16LE");
                out = Conversions.resizeArray(out, 0, length);
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.UTF8)
            {
                out = value.getBytes("UTF-8");
                out = Conversions.resizeArray(out, 0, length);
                rand.seek(position);
                rand.write(out);
            }
            else if(charset == CharSet.HexString)
            {
                out = Conversions.toInt8(value);
                out = Conversions.resizeArray(out, 0, length);
                rand.seek(position);
                rand.write(out);
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write - Integer 8 array (-128 - 127)
     * @param value - The byte value you want to write*/
    public void writeInt8(byte[] value)
    {
        try
        {
            rand.seek(position);
            rand.write(value);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write - Unsigned Integer 8 array (0 - 255)
     * @param value - The byte value you want to write*/
    public void writeUInt8(int[] value)
    {
        try
        {
            rand.seek(position);
            byte[] tempByte = Conversions.toInt8(value);
            rand.write(tempByte);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write - Integer 8 array (-128 - 127)
     * @param value - The byte value you want to write
     * @param position - The long position to seek, which the value will start writing from*/
    public void writeInt8(byte[] value, long position)
    {
        try
        {
            rand.seek(position);
            rand.write(value);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write - Integer 8 array (0 - 255)
     * @param value - The byte value you want to write
     * @param position - The long position to seek, which the value will start writing from*/
    public void writeUInt8(int[] value, long position)
    {
        try
        {
            rand.seek(position);
            byte[] tempByte = Conversions.toInt8(value);
            rand.write(tempByte);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Unsigned Integer 8 / Write Bytes
     * values between 0 - 255
     * @param value - byte value to be written*/
    public void writeUInt8(int value) throws Exception
    {
        if (value>255 || value<0)
            throw new Exception("Value out of range");
        try
        {
            rand.seek(position);  //Seek to start point of file
            rand.writeByte(value);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Integer 8 / Write Signed Bytes
     * values between -128 - 127
     * @param value - short value will be converted to the SByte that will be written*/
    public void writeInt8(byte value)
    {
        try
        {
            rand.seek(position);  //Seek to start point of file
            rand.write(value);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Integer 16 / Write Short
     * values between -32768 - 32767
     * @param value - short value will be written*/
    public void writeInt16(short value)
    {
        try
        {
            rand.seek(position);  //Seek to start point of file
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            rand.writeShort(value);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Unsigned Integer 16 / Write Unsigned Short
     * values between 0 - 65535
     * @param value - Integer value will be converted to the UShort that will be written*/
    public void writeUInt16(int value) throws Exception
    {
        if (value>65535 || value<0)
            throw new Exception("Value out of range");
        try
        { 
            rand.seek(position);  //Seek to start point of file
            byte[] b = new byte[2];
            for (int i = 0; i < 2; i++) 
            {
                int offset = (b.length - 1 - i) * 8;
                b[i] = (byte) ((value >>> offset) & 0xFF);
            }
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            rand.write(b);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Integer 24 / Write 3 bytes
     * values between -8388608 - 8388607
     * @param value - short value will be written*/
    public void writeInt24(int value) throws Exception
    {
        if (value < -8388608 || value > 8388607)
            throw new Exception("Invalid value");
        byte[] buffer = new byte[3];
        try
        {
            rand.seek(position);  //Seek to start point of file
            
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            
            buffer[2] =(byte)(value & 0xFF);
            buffer[1] =(byte)((value >> 8) & 0xFF);
            buffer[0] =(byte)((value >> 16) & 0xFF);
            
            rand.write(buffer);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Unsigned Integer 24 / Write 3 bytes
     * values between 0 - 16777215*/
    public void writeUInt24(int value) throws Exception
    {
        if (value < 0 || value > 16777215)
            throw new Exception("Invalid value");
        byte[] buffer = new byte[3];
        try
        {
            rand.seek(position);  //Seek to start point of file
            
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            
            buffer[2] =(byte)(value & 0xFF);
            buffer[1] =(byte)((value >> 8) & 0xFF);
            buffer[0] =(byte)((value >> 16) & 0xFF);
            
            rand.write(buffer);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Integer 32 / Read Integer
     * values between -2147483648 - 2147483647
     * @param value - - Integer value that will be written*/
    public void writeInt32(int value)
    {
        try
        {
            rand.seek(position);  //Seek to start point of file
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            rand.writeInt(value);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Unsigned Integer 32 / Write Unsigned Integer
     * values between 0 - 4294967295
     * @param value - Long value will be converted to the Integer that will be written*/
    public void writeUInt32(long value) throws Exception
    {
        if (value>4294967295L || value<0)
            throw new Exception("Value out of range");
        try
        { 
            rand.seek(position);  //Seek to start point of file
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            byte[] b = new byte[4];
            for (int i = 0; i < 4; i++) 
            {
                int offset = (b.length - 1 - i) * 8;
                b[i] = (byte) ((value >>> offset) & 0xFF);
            }
            rand.write(b);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Integer 40 / Write 5 bytes
     * values between -549755813888 - 549755813887
     * @param value - short value will be written*/
    public void writeInt40(long value) throws Exception
    {
        if (value < -549755813888L || value > 549755813887L)
            throw new Exception("Invalid value");
        byte[] buffer = new byte[5];
        try
        {
            rand.seek(position);  //Seek to start point of file
            
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            
            buffer[4] =(byte)(value & 0xFF);
            buffer[3] =(byte)((value >> 8) & 0xFF);
            buffer[2] =(byte)((value >> 16) & 0xFF);
            buffer[1] =(byte)((value >> 24) & 0xFF);
            buffer[0] =(byte)((value >> 32) & 0xFF);
            
            rand.write(buffer);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Unsigned Integer 40 / Write 5 bytes
     * values between 0 - 1099511627775*/
    public void writeUInt40(long value) throws Exception
    {
        if (value < 0 || value > 1099511627775L)
            throw new Exception("Invalid value");
        byte[] buffer = new byte[5];
        try
        {
            rand.seek(position);  //Seek to start point of file
            
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            
            buffer[4] =(byte)(value & 0xFF);
            buffer[3] =(byte)((value >> 8) & 0xFF);
            buffer[2] =(byte)((value >> 16) & 0xFF);
            buffer[1] =(byte)((value >> 24) & 0xFF);
            buffer[0] =(byte)((value >> 32) & 0xFF);
            
            rand.write(buffer);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Write Integer 64 / Write Long
     * values between -9223372036854775807 - 9223372036854775807
     * @param value - The Long value that was Written*/
    public void writeInt64(long value)
    {
        try
        { 
            rand.seek(position);  //Seek to start point of file
            if(!isBigEndian)
            {
                value = EndianSwap.swap(value);
            }
            rand.writeLong(value);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"File Writer Error",JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(!(rand == null))
            try {
                this.position = rand.getFilePointer();
                this.length = file.length();
            } 
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,ex,"Position",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

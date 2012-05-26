/** Iso.Io Reader*/
package iso.io;

import iso.io.Conversions.CharSet;
import iso.io.EndianSwap.Endian;
import java.io.RandomAccessFile; //For Opening File - Reader/Write
import java.io.File; //Used to Create a File
import javax.swing.JOptionPane; //For Error MessageBox
import java.io.BufferedReader; //Read Plain Text
import java.io.IOException; // IO Exception Catching
import java.util.Arrays;
import java.util.Random;

/** @author     PureIso
 * @version     0.3 */
public class Reader
{
    private boolean accessed = false;
    private String path;
    private long position;
    private Endian endian;
    private File file;
    private RandomAccessFile rand;
    private long length;
    private Random l = new Random();
    private String tempName = "tmp-file"+l.nextInt();
    
    /** Constructor
     * @param path   - File Path that will be used by this Object*/
    public Reader(String path) throws Exception
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
                this.endian = Endian.Big;
                this.rand = new RandomAccessFile(path,"r");
                this.length = rand.length();
                accessed = true;
            }
            catch (IOException e)
            {
                //Catch exception if any
                JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**Constructor
     * @param buffer - Int8 array that will be used 
     * @param isBigEndian - boolean check*/
    public Reader(byte[] buffer, boolean isBigEndian) throws Exception
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
        
        this.rand = new RandomAccessFile(file.getAbsolutePath(),"r");
        rand.write(buffer);
        length = file.length();
        if(isBigEndian == true)
        {
            this.endian = Endian.Big;
        }
        else
        {
            this.endian = Endian.Little;
        }
        accessed = true;
    }
    
    /** Constructor
     * @param path   - File Path that will be used by this Object
     * @param endian - The Endian Type the bytes will be converted to before read*/
    public Reader(String path,Endian endian) throws Exception
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
                this.endian = endian;
                this.rand = new RandomAccessFile(path,"r");
                this.length = rand.length();
                accessed = true;
            }
            catch (IOException e)
            {
                //Catch exception if any
                JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Constructor
     * @param path     - File Path that will be used by this Object
     * @param endian   - The Endian Type the bytes will be converted to before read
     * @param position - Set the position the reader will start from*/
    public Reader(String path,Endian endian, long position) throws Exception
    {
        file = new File(path);
        if(!file.exists())
            throw new Exception("File Does Not Exist!");
        else
        {
            try
            {
                this.path = path;
                this.position = position;
                this.endian = endian;
                this.rand = new RandomAccessFile(path,"r");
                this.length = rand.length();
                accessed = true;
            }
            catch (IOException e)
            {
                //Catch exception if any
                JOptionPane.showMessageDialog(null,e,"File Reader Error",JOptionPane.ERROR_MESSAGE);
            }
        } 
    }
    
    /** Method
     * @param value   - The HexString value that will are looking for
     * @param Integer - position of the first instance of the HexString that was found*/
    public int searchHexString(String value) throws Exception
    {
        if(this.position > this.length)
            throw new Exception("Argument out of Range");
        int instancePos = 0;
        byte[] item = Conversions.toInt8(value);
        
        for (int i = (int)this.position; i < this.length; i++)
        {
            if(i+item.length >= this.length-1)
                break;
            this.position = i;
            byte[] buffer = this.readInt8(item.length);
            if(Arrays.equals(item, buffer) || Arrays.equals(item, EndianSwap.swap(buffer)))
            {
                instancePos = i;
                return instancePos;
            }
        }
        return instancePos;
    }
    
    /** Method
     * @param value   - The HexString value that will are looking for
     * @param position   - This is the position we will start looking from
     * @param Integer - position of the first instance of the HexString that was found*/
    public int searchHexString(String value, long position) throws Exception
    {
        if(position > this.length || position < 0)
            throw new Exception("Argument out of Range");
        int instancePos = 0;
        byte[] item = Conversions.toInt8(value);
        
        for (int i = (int)position; i < this.length; i++)
        {
            if(i+item.length >= this.length-1)
                break;
            this.position = i;
            byte[] buffer = this.readInt8(item.length);
            if(Arrays.equals(item, buffer) || Arrays.equals(item, EndianSwap.swap(buffer)))
            {
                instancePos = i;
                return instancePos;
            }
        }
        return instancePos;
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
    
    /**Get a copy of the current file
     * @return Byte[] - A copy of the file
     * @throws Exception */
    public byte[] readStream() throws Exception
    {
        try
        {
            byte[] xReturn = new byte[(int)length];
            long posbefore = position();
            position = 0;
            xReturn = readInt8(xReturn.length);
            position = posbefore;
            return xReturn;
        }
        catch (Throwable x)
        {
            throw new Exception(x);
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
            if(endian == Endian.Little)
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
            if(endian == Endian.Little)
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
            if(endian == Endian.Little)
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
            if(endian == Endian.Little)
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
            if(endian == Endian.Little)
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
            if(endian == Endian.Little)
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
            if(endian == Endian.Little)
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
            if(endian == Endian.Little)
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
            if(endian == Endian.Little)
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
}

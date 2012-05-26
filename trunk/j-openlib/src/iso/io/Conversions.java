/** Class for data conversions
 * Int8 arrays to UInt8 arrays  && UInt8 to Int8 arrays
 * etc
 */
package iso.io;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import javax.swing.JOptionPane;

/**@author      PureIso
 * @version     0.3 */
public class Conversions
{
    /** Enumeration for Character types*/
    public static enum CharSet
    {
        HexString,
        UTF8,
        ASCII,
        ISO88591,
        UTF16BE,
        UTF16LE,
        UTF16;
    }

    /** Resize a byte[]
     * @param value - The data in this array will be resized
     * @param length - The length of the result
     * @param offset - Where to start data transfer
     * @return byte[] - New resigned byte[]
     */
    public static byte[] resizeArray(byte[] value, int offset, int length)
    {
        byte[] buffer = new byte[length];
        for(int i = offset; i < length; i++)
        {
            if(i >= value.length)
            {
                buffer[i] = 0;
            }
            else
            {
                buffer[i] = value[i];
            }
        }
        return buffer;
    }
    
    /** Resize a byte[]
     * @param value - The data in this array will be resized
     * @param length - The length of value
     * @param offset - Where to start data transfer
     * @param bufferLength - The length of the result
     @return byte[] - New resigned byte[]
     */
    public static byte[] resizeArray(byte[] value, int offset, int length, int bufferLength)
    {
        int count = 1;
        byte[] buffer = new byte[bufferLength];
        for(int i = 0; i < length; i++)
        {
            if (!(count > bufferLength))
            {
                if(i >= value.length)
                {
                    buffer[i] = 0;
                }
                else
                {
                    buffer[i] = value[offset];
                }
            }
            else
            {
                break;
            }
            offset++;
            count++;
        }
        return buffer;
    }
    
    /**Convert SBytes(-128 - 127) to Bytes (0 - 255)
     * @param sbytesArray - The byte array that will be converted
     * @return Integer Array - The converted representation*/
    public static int[] toUInt8(byte[] sbytesArray) throws Exception
    {
        try
        {
            int[] buffer = new int[sbytesArray.length];
            for (int i = 0; i < sbytesArray.length; i++)
            buffer[i] = sbytesArray[i] & 0xFF;
            return buffer;
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
    }
    
    /**Convert Bytes (0 - 255) to SBytes (-128 - 127) 
     * @param sbytesArray - The byte array that will be converted
     * @return Integer Array - The converted representation*/
    public static byte[] toInt8(int[] bytesArray)
    {
        try
        {
            byte[] buffer = new byte[bytesArray.length];
            for (int i = 0; i < bytesArray.length; i++)
            buffer[i] = (byte) (bytesArray[i] & 0xFF);
            return buffer;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e,"STFS Error",JOptionPane.ERROR_MESSAGE); 
        }
        return null;
    }
    
    /**Convert String to Hex bytes Array SBytes(-128 - 127)
     * @param value - The String value that will be converted to Hex byte value
     * @return Byte - The byte array representation of the string value*/
    public static byte[] toInt8(String value) throws Exception
    {
        if (!isValidHex(value))
            throw new Exception("Not a valid Hex String");
        if (value.length()%2 == 1){
            //if we have 1 remainder
            value = value+"0";
        }
        
        byte[] buffer = new byte[(int)value.length()/2];
        for (int i = 0; i < (value.length()/2); i++)
        {
            String n = value.substring(i*2, i*2+2);
            int e = Integer.decode("0x"+n);
            buffer[i] =(byte)e;
        }
        return buffer;
    }
    
    /**Convert String to Hex bytes Array Bytes (0 - 255)
     * @param value - The String value that will be converted to Hex byte value
     * @return Integer Array - The byte array representation of the string value*/
    public static int[] toUInt8(String value)
    {
        try
        {
            if (!isValidHex(value))
                throw new Exception("Not a valid Hex String");
            if (value.length()%2 == 1){
                //if we have 1 remainder
                value = value+"0";
            }
        
            int[] buffer = new int[(int)value.length()/2];
            for (int i = 0; i < (value.length()/2); i++)
            {
                String n = value.substring(i*2, i*2+2);
                int e = Integer.decode("0x"+n);
                buffer[i] = e;
            }
            return buffer;
        }
        catch(Exception e)
        {
             JOptionPane.showMessageDialog(null,e,"STFS Error",JOptionPane.ERROR_MESSAGE);        
        }
        return null;
    }
    
    /**Convert String to Hex bytes Array
     * @param value - The byte value that will be converted to Hex byte value
     * @return Byte - The byte array representation of the string value*/
    public static String toHexString(byte[] value)
    {
        String hex = "";
        for (int i = 0; i < value.length; i++)
        {
            hex += Integer.toHexString(0xFF & value[i]);
        }
        return hex.toUpperCase();
    }
    
    /**Convert String to Hex bytes Array
     * @param value - The byte value that will be converted to Hex byte value
     * @return Byte - The byte array representation of the string value*/
    public static String toHexString(int[] value)
    {
        String hex = "";
        for (int i = 0; i < value.length; i++)
        {
            hex += Integer.toHexString(0xFF & value[i]);
        }
        return hex.toUpperCase();
    }
    
    /**Convert bytes array to US-ASCII bytes Array
     * @param value - The bytes array that will be converted to US-ASCII bytes Array
     * @return Byte - The bytes array representation of the US-ASCII bytes Array value*/
    public static byte[] toUSASCIIArray(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "US-ASCII");
        value = outputString.getBytes("US-ASCII");
        return value;
    }
    
    /**Convert bytes array to ISO-8859-1 bytes Array
     * @param value - The bytes array that will be converted to ISO-8859-1 bytes Array
     * @return Byte - The bytes array representation of the ISO-8859-1 bytes Array value*/
    public static byte[] toISO88591Array(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "ISO-8859-1");
        value = outputString.getBytes("ISO-8859-1");
        return value;
    }
    
    /**Convert bytes array to UTF-16 bytes Array
     * @param value - The bytes array that will be converted to UTF-16 bytes Array
     * @return Byte - The bytes array representation of the UTF-16 bytes Array value*/
    public static byte[] toUTF16Array(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "UTF-16");
        value = outputString.getBytes("UTF-16");
        return value;
    }
    
    /**Convert bytes array to UTF-16BE bytes Array
     * @param value - The bytes array that will be converted to UTF-16BE bytes Array
     * @return Byte - The bytes array representation of the UTF-16BE bytes Array value*/
    public static byte[] toUTF16BEArray(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "UTF-16BE");
        value = outputString.getBytes("UTF-16BE");
        return value;
    }
    
    /**Convert bytes array to UTF-16LE bytes Array
     * @param value - The bytes array that will be converted to UTF-16LE bytes Array
     * @return Byte - The bytes array representation of the UTF-16LE bytes Array value*/
    public static byte[] toUTF16LEArray(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "UTF-16LE");
        value = outputString.getBytes("UTF-16LE");
        return value;
    }
    
    /**Convert bytes array to UTF-8 bytes Array
     * @param value - The bytes array that will be converted to UTF-8 bytes Array
     * @return Byte - The bytes array representation of the UTF-8 bytes Array value*/
    public static byte[] toUTF8Array(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "UTF-8");
        value = outputString.getBytes("UTF-8");
        return value;
    }
    
    /**Convert bytes array to US-ASCII bytes Array
     * @param value - The bytes array that will be converted to US-ASCII bytes Array
     * @return Byte - The bytes array representation of the US-ASCII bytes Array value*/
    public static String toUSASCIIString(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "US-ASCII");
        return outputString;
    }
    
    /**Convert bytes array to ISO-8859-1 String
     * @param value - The bytes array that will be converted to ISO-8859-1 bytes Array
     * @return String - The String representation of the ISO-8859-1 bytes Array value*/
    public static String toISO88591String(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "ISO-8859-1");
        return outputString;
    }
    
    /**Convert bytes array to UTF-16 String
     * @param value - The bytes array that will be converted to UTF-16 bytes Array
     * @return String - The String representation of the UTF-16 bytes Array value*/
    public static String toUTF16String(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "UTF-16");
        return outputString;
    }
    
    /**Convert bytes array to UTF-16BE String
     * @param value - The bytes array that will be converted to UTF-16BE bytes Array
     * @return String - The String representation of the UTF-16BE bytes Array value*/
    public static String toUTF16BEString(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "UTF-16BE");
        return outputString;
    }
    
    /**Convert bytes array to UTF-16LE String
     * @param value - The bytes array that will be converted to UTF-16LE bytes Array
     * @return String - The String representation of the UTF-16LE bytes Array value*/
    public static String toUTF16LEString(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "UTF-16LE");
        return outputString;
    }
    
    /**Convert bytes array to UTF-8 String
     * @param value - The bytes array that will be converted to UTF-8 bytes Array
     * @return String - The String representation of the UTF-8 bytes Array value*/
    public static String toUTF8String(byte[] value) throws UnsupportedEncodingException
    {
        String outputString = new String(value, 0, value.length, "UTF-8");
        return outputString;
    }
    
    /**Checks if a string is a valid Hex value
     * @param value - The String value that will be checked
     * @return boolean - true for valid hex and false for invalid hex*/
    private static boolean isValidHex(String value)
    {
            return value.matches("[0-9a-fA-F]+");
    }
    
    /**Converts byte array to short
     * @param value - The byte array
     * @param BigEndian - if its big endian
     * @return short - value of the byte array*/
    public static short toInt16(byte[] value, boolean BigEndian)
    {
        int returnValue = 0;
        if (!(BigEndian))
        {
            value = EndianSwap.swap(value);
        }
        for (int i = 0; i < value.length; i++)
        {
            returnValue = (returnValue << 8) + (value[i] & 0xff);
        }
        return (short)returnValue;
    }
    
    /**Converts byte array to unsigned short
     * @param value - The byte array
     * @param BigEndian - if its big endian
     * @return Integer - (unsigned short)value of the byte array*/
    public static int toUInt16(byte[] value, boolean BigEndian)
    {
        int returnValue = 0;
        if (!(BigEndian))
        {
            value = EndianSwap.swap(value);
        }
        for (int i = 0; i < value.length; i++)
        {
            returnValue = (returnValue << 8) + (value[i] & 0xff);
        }
        return returnValue;
    }
    
    /**Converts byte array to Integer
     * @param value - The byte array
     * @param BigEndian - if its big endian
     * @return Integer - (unsigned short)value of the byte array*/
    public static int toInt32(byte[] value, boolean BigEndian)
    {
        int returnValue = 0;
        if (!(BigEndian))
        {
            value = EndianSwap.swap(value);
        }
        for (int i = 0; i < value.length; i++)
        {
            returnValue = (returnValue << 8) + (value[i] & 0xff);
        }
        return returnValue;
    }
    
    /**Converts byte array to Integer
     * @param value - The byte array
     * @param BigEndian - if its big endian
     * @return Integer - (unsigned short)value of the byte array*/
    public static long toUInt32(byte[] value, boolean BigEndian)
    {
        long returnValue = 0;
        if (!(BigEndian))
        {
            value = EndianSwap.swap(value);
        }
        for (int i = 0; i < value.length; i++)
        {
            returnValue = (returnValue << 8) + (value[i] & 0xff);
        }
        if (returnValue < 0)
        {
            returnValue += 4294967296L;
        }
        return returnValue;
    }
    
    /**Convert short to Signed Bytes array/Byte array(-128 - 127)
     * @param value - the short value that will be converted
     * @param BigEndian - if its big endian
     * @return Byte - The byte array representation of the short value*/
    public static byte[] getInt8(short value, boolean BigEndian)
    {
        byte[] bytes = ByteBuffer.allocate(2).putInt(value).array();
        if (!(BigEndian))
        { 
            bytes = EndianSwap.swap(bytes);
        }
        return bytes;
    }
    
    /**Convert Integer to Signed Bytes array/Byte array(-128 - 127)
     * @param value - the integer value that will be converted
     * @param BigEndian - if its big endian
     * @return Byte - The byte array representation of the short value*/
    public static byte[] getInt8(int value, boolean BigEndian)
    {
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        if (!(BigEndian))
        {
            bytes = EndianSwap.swap(bytes);
        }
        return bytes;
    }
    
    /**Convert integer to Signed Bytes array/Byte array(0 - 255)
     * @param value - the integer value that will be converted
     * @param BigEndian - if its big endian
     * @return Unsigned Byte - The byte array representation of the short value*/
    public static int[] getUInt8(int value, boolean BigEndian) throws Exception
    {
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        int[] buffer = Conversions.toUInt8(bytes);
        
        if (!(BigEndian))
        {
            bytes = EndianSwap.swap(bytes);
        }
        return buffer;
    }
    
    /**Convert integer to Signed Bytes array/Byte array(0 - 255)
     * @param value - the long value that will be converted
     * @param BigEndian - if its big endian
     * @return Byte - The byte array representation of the short value*/
    public static byte[] getInt8(long value, boolean BigEndian) throws Exception
    {
        byte[] bytes = ByteBuffer.allocate(8).putLong(value).array();
        if (!(BigEndian))
        {
            bytes = EndianSwap.swap(bytes);
        }
        return bytes;
    }
    
    /**Convert integer to Signed Bytes array/Byte array(0 - 255)
     * @param value - the long value that will be converted(UInt32)
     * @param BigEndian - if its big endian
     * @return Byte - The byte array representation of the short value*/
    public static byte[] getInt8FromUInt32(long value, boolean BigEndian) throws Exception
    {
        byte[] bytes = ByteBuffer.allocate(8).putLong(value).array();
        byte[] newbyte = resizeArray(bytes,4,8,4);
        
        if (!(BigEndian))
        {
            newbyte = EndianSwap.swap(newbyte);
        }
        return newbyte;
    }
    
    /**Convert integer to Signed Bytes array/Byte array(0 - 255)
     * @param value - the integer value that will be converted
     * @param BigEndian - if its big endian
     * @return Unsigned Byte - The byte array representation of the short value*/
    public static int[] getUInt8(long value, boolean BigEndian) throws Exception
    {
        byte[] bytes = ByteBuffer.allocate(4).putLong(value).array();
        int[] buffer = Conversions.toUInt8(bytes);
        
        if (!(BigEndian))
        {
            bytes = EndianSwap.swap(bytes);
        }
        return buffer;
    }
    
    public static int[] bytePiece(byte[] piece, long startOffset, long size) throws Exception
    {
       byte[] buffer = new byte[(int)size];
       for (int i = 0; i < size; i++)
       {
           buffer[i] = piece[(int)startOffset+i];
       }
       return Conversions.toUInt8(buffer);
    }
   
}



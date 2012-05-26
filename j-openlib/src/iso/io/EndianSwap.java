/**Class for doing byte swapping (i.e. conversion between
 * little-endian and big-endian representations) of different data types.
 * Byte swapping is typically used when data is read from a stream 
 * delivered by a system of different endian type as the present one.
 */  
package iso.io;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EndianSwap 
{
    /** Enumeration for Endian types*/
    public static enum Endian 
    {
        Big,
        Little;
    }
    
    /**Byte swap a single short value.
     * @param value  Value to byte swap.
     * @return Byte swapped representation.*/
    public static short swap (short value)
    {
        int b1 = value & 0xff;
        int b2 = (value >> 8) & 0xff;

        return (short) (b1 << 8 | b2);
    } 
    
    /**Byte swap a single Integer value.
     * @param value  Value to byte swap.
     * @return Byte swapped representation.*/
    public static int swap (int value)
    {
        int b1 = (value) & 0xff;
        int b2 = (value >>  8) & 0xff;
        int b3 = (value >> 16) & 0xff;
        int b4 = (value >> 24) & 0xff;

        return b1 << 24 | b2 << 16 | b3 << 8 | b4;
    }
  
    /**Byte swap a single long value.
     * @param value  Value to byte swap.
     * @return Byte swapped representation.*/
    public static long swap (long value)
    {
        long b1 = (value) & 0xff;
        long b2 = (value >>  8) & 0xff;
        long b3 = (value >> 16) & 0xff;
        long b4 = (value >> 24) & 0xff;
        long b5 = (value >> 32) & 0xff;
        long b6 = (value >> 40) & 0xff;
        long b7 = (value >> 48) & 0xff;
        long b8 = (value >> 56) & 0xff;

        return b1 << 56 | b2 << 48 | b3 << 40 | b4 << 32 |
                b5 << 24 | b6 << 16 | b7 <<  8 | b8;
    }
    
    /**Byte swap a single float value.
     * @param value  Value to byte swap.
     * @return       Byte swapped representation.*/
    public static float swap (float value)
    {
        int intValue = Float.floatToIntBits (value);
        intValue = swap (intValue);
        return Float.intBitsToFloat (intValue);
    }
    
    /**Byte swap a single double value.
     * @param value  Value to byte swap.
     * @return Byte swapped representation.*/
    public static double swap (double value)
    {
        long longValue = Double.doubleToLongBits (value);
        longValue = swap (longValue);
        return Double.longBitsToDouble (longValue);
    }
  
    /**Byte swap an array of shorts. The result of the swapping
     * is put back into the specified array.
     * @param array  Array of values to swap*/
    public static void swap (short[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = swap (array[i]);
        }    
    }
    
    /**Byte swap an array of integer. The result of the swapping
     * is put back into the specified array.
     * @param array  Array of values to swap*/
    public static void swap (int[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = swap (array[i]);
        }
    }
    
    /**Byte swap an array of longs. The result of the swapping
     * is put back into the specified array.
     * @param array  Array of values to swap
     */
    public static void swap (long[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = swap (array[i]);
        }
    }
    
    /**Byte swap an array of floats. The result of the swapping
     * is put back into the specified array.
     * @param array  Array of values to swap
     */
    public static void swap (float[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = swap (array[i]);
        }
    }
    
    /**Byte swap an array of doubles. The result of the swapping
     * is put back into the specified array.
     * @param array  Array of values to swap
     */
    public static void swap (double[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = swap (array[i]);
        }    
    }
    
    public static Object[] swap(Object[] buffer)
    {
        List<Object> list = Arrays.asList(buffer);
        Collections.reverse(list);
        return list.toArray();
    }
    
    public static byte[] swap(byte[] buffer)
    {
        if (buffer == null) 
        {
            return buffer;
        }
        
        int i = 0;
        int j = buffer.length - 1;
        byte tmp;
        while (j > i) 
        {
            tmp = buffer[j];
            buffer[j] = buffer[i];
            buffer[i] = tmp;
            j--;
            i++;
        }
        return buffer;
    }
    
    public static byte[] swap(byte[] buffer,long byteAmount)
    {
        if (buffer == null) 
        {
            return buffer;
        }
        
        int i = 0;
        int j = buffer.length - 1;
        byte tmp;
        while (byteAmount > i) 
        {
            tmp = buffer[j];
            buffer[j] = buffer[i];
            buffer[i] = tmp;
            j--;
            i++;
            byteAmount--;
        }
        return buffer;
    }
    
}

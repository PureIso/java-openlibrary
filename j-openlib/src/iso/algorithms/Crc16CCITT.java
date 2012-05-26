/** Calculate CRC16-CCITT algorithms*/
package iso.algorithms;

/**@author Ola
 * @version 0.1*/
public class Crc16CCITT 
{
    public static byte[] compute(byte[] buffer) 
    { 
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12) 

        for (byte b : buffer) 
        {
            for (int i = 0; i < 8; i++) 
          {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
             }
        }
        crc &= 0xffff;
        
        byte[] results = Integer.toHexString(crc).getBytes();
        return results;
    }
}

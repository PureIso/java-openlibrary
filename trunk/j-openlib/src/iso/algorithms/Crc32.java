/** Calculate CRC32 algorithms*/
package iso.algorithms;

/** @author     PureIso
 * @version     0.1*/
public class Crc32
{
    public static byte[] compute(byte[] buffer)
    {
        int crc  = 0xFFFFFFFF;   // initial contents of LFBSR
        int poly = 0xEDB88320;   // reverse polynomial

        for (byte b : buffer) 
        {
            int temp = (crc ^ b) & 0xff;

            // read 8 bits one at a time
            for (int i = 0; i < 8; i++) {
                if ((temp & 1) == 1) temp = (temp >>> 1) ^ poly;
                else                 temp = (temp >>> 1);
            }
            crc = (crc >>> 8) ^ temp;
        }

        // flip bits
        crc = crc ^ 0xffffffff;
        byte[] results = Integer.toHexString(crc).getBytes();
        return results;
    }
}

/**calculate MD5 hash
 * MD5 is a hashing algorithm that takes a message 
 * of up to 264 bits and reduces it to a digest of 
 * 128 bits (16 bytes). 
 */
package iso.algorithms;

import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;

/** @author     PureIso
 * @version     0.1*/
public class MD5 
{
    /**
     * @param buffer - The byte array that will be calculated
     * @return byte array - Of the MD5 digest
     * @throws NoSuchAlgorithmException */
    public static byte[] Compute(byte[] buffer) throws NoSuchAlgorithmException
    {
        MessageDigest mDigest = MessageDigest.getInstance("MD5");
        mDigest.reset();
        return mDigest.digest(buffer);
    }
}

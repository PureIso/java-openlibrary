/**calculate SHA-1 hash
 * SHA-1 is a hashing algorithm similar in structure to MD5, 
 * but producing a digest of 160 bits (20 bytes).
 * Because of the large digest size, it is less likely 
 * that two different messages will have the same SHA-1 message digest. 
 * For this reason SHA-1 is recommended in preference to MD5. 
 */
package iso.algorithms;

import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;

/** @author     PureIso
 * @version     0.1*/
public class SHA1 
{
    /**
     * @param buffer - The byte array that will be calculated
     * @return byte array - Of the SHA-1 digest
     * @throws NoSuchAlgorithmException */
    public static byte[] Compute(byte[] buffer) throws NoSuchAlgorithmException
    {
        MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
        mDigest.reset();
        return mDigest.digest(buffer);
    }
}

/**
 */
package iso.io;

/**The ref method parameter keyword on a method parameter causes a method to refer
 * to the same variable that was passed into the method.
 * Any changes made to the parameter in the method will be reflected in that 
 * variable when control passes back to the calling method.
 *
 * @author PureIso
 */
public class ref<T>
{
    public T value;
    
    public ref(T value)
    {
        this.value = value;
    }
}

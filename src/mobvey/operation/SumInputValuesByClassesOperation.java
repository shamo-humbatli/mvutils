package mobvey.operation;
import java.util.ArrayList;
import java.util.Collection;
import mobvey.common.Strings;

/**
 *
 * @author Shamo Humbatli
 */
public class SumInputValuesByClassesOperation extends AbstractOperation {

    protected Collection<String> _inputClasses = new ArrayList<>();

    public SumInputValuesByClassesOperation() {
        super(OperationType.SUM_IVS_BY_CLASSES);
    }

    public Collection<String> getInputClasses() {

        if (_inputClasses == null) {
            _inputClasses = new ArrayList<>();
        }

        return _inputClasses;
    }

    public void setInputClasses(Collection<String> inputClasses) {
        this._inputClasses = inputClasses;
    }

    public void addInputClass(String className) {
        if (!Strings.hasContent(className)) {
            return;
        }

        getInputClasses().add(className);
    }
    
    public boolean containsAtLeastOneClass(Collection<String> otherClasses)
    {
        if(otherClasses == null)
            return false;
        
        
        for(String cn : getInputClasses())
        {
            if(otherClasses.contains(cn))
            {
                return true;
            }
        }
        
        return false;
    }
}

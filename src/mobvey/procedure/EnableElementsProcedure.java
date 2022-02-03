package mobvey.procedure;

import java.util.ArrayList;
import java.util.List;
import mobvey.form.enums.EventProcedureType;

/**
 *
 * @author ShamoHumbatli
 */
public class EnableElementsProcedure extends AbstractProcedure {

    public EnableElementsProcedure() {
        super(EventProcedureType.ENABLE_ELEMENTS);
    }
    
    private List<String> _elementsIdsToEnable;

    public List<String> getElementsIdsToEnable() {
        
        if(_elementsIdsToEnable == null)
            _elementsIdsToEnable = new ArrayList<>();
        
        return _elementsIdsToEnable;
    }

    public void setElementsIdsToEnable(List<String> elementsIdsToEnable) {
        this._elementsIdsToEnable = elementsIdsToEnable;
    }
}

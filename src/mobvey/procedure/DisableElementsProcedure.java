package mobvey.procedure;

import java.util.ArrayList;
import java.util.List;
import mobvey.form.enums.EventProcedureType;

/**
 *
 * @author ShamoHumbatli
 */
public class DisableElementsProcedure extends AbstractProcedure{

    public DisableElementsProcedure() {
        super(EventProcedureType.DISABLE_ELEMENTS);
    }
    
    private List<String> _elementsIdsToDisable;

    public List<String> getElementsIdsToDisable() {
        if(_elementsIdsToDisable == null)
            _elementsIdsToDisable = new ArrayList<>();
        
        return _elementsIdsToDisable;
    }

    public void setElementsIdsToDisable(List<String> elementsIdsToDisable) {
        this._elementsIdsToDisable = elementsIdsToDisable;
    }
}

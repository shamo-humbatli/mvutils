package mobvey.procedure;

import java.util.List;
import mobvey.common.ParamUtil;
import mobvey.form.enums.EventProcedureType;

/**
 *
 * @author ShamoHumbatli
 */
public class SetReturnRequiredProcedure extends AbstractProcedure{

    public SetReturnRequiredProcedure() {
        super(EventProcedureType.SET_RETURN_REQUIRED);
    }
    
    private List<String> _elements;
    private boolean _required = false;

    public List<String> getElements() {
        return _elements;
    }

    public void setElements(List<String> elements) {
        this._elements = elements;
    }

    public boolean isRequired() {
        return _required;
    }

    public void setRequired(boolean required) {
        this._required = required;
    }
    
    @Override
    public String[] getProcedureParams() {
        return new String[]{
            String.valueOf(_required),
            ParamUtil.getAsArray(getElements())
        };
    }
}

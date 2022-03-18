package mobvey.procedure;

import java.util.ArrayList;
import java.util.Collection;
import mobvey.common.ParamUtil;
import mobvey.form.enums.EventProcedureType;

/**
 *
 * @author Shamo Humbatli
 */
public class EnableElementsByClassProcedure extends AbstractProcedure {

    public EnableElementsByClassProcedure() {
        super(EventProcedureType.ENABLE_ELEMENTS_BY_CLASS);
    }

    private Collection<String> _elementsClasses;
    private boolean _enabled = false;

    public Collection<String> getElementsClasses() {

        if (_elementsClasses == null) {
            _elementsClasses = new ArrayList<>();
        }

        return _elementsClasses;
    }

    public void setElementsClasses(Collection<String> elementsClasses) {
        this._elementsClasses = elementsClasses;
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean enabled) {
        this._enabled = enabled;
    }

    @Override
    public String[] getProcedureParams() {
        return new String[]{
            String.valueOf(_enabled),
            ParamUtil.getAsArray(getElementsClasses())
        };
    }
}

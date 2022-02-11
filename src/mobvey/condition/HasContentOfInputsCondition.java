package mobvey.condition;

import java.util.Collection;

/**
 *
 * @author ShamoHumbatli
 */
public class HasContentOfInputsCondition extends AbstractCondition {
    protected Collection<String> _inputIds;

    public Collection<String> getInputIds() {
        return _inputIds;
    }

    public void setInputIds(Collection<String> inputIds) {
        this._inputIds = inputIds;
    }
    
    public HasContentOfInputsCondition() {
        super(ConditionType.HAS_CONTENT_IVS);
    }
}

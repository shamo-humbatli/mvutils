package mobvey.condition;

import java.util.Collection;

/**
 *
 * @author ShamoHumbatli
 */
public class HasNoContentOfInputsCondition extends AbstractCondition {

    public HasNoContentOfInputsCondition() {
        super(ConditionType.HAS_NO_CONTENT_IVS);
    }

    protected Collection<String> _inputIds;

    public Collection<String> getInputIds() {
        return _inputIds;
    }

    public void setInputIds(Collection<String> inputIds) {
        this._inputIds = inputIds;
    }

}

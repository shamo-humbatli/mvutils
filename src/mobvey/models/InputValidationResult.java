package mobvey.models;

import java.io.Serializable;
import mobvey.condition.AbstractCondition;
import mobvey.form.elements.AbstractInput;

/**
 *
 * @author Shamo Humbatli
 */
public class InputValidationResult implements Serializable {
    private final AbstractInput _input;
    private final boolean _isValid;
    private final AbstractCondition _condition;

    public InputValidationResult(AbstractInput input, AbstractCondition condition, boolean isValid) {
        this._isValid = isValid;
        this._condition = condition;
        _input = input;
    }
    
    public InputValidationResult(AbstractInput input, boolean isValid) {
        this._isValid = isValid;
        this._condition = null;
        _input = input;
    }
    
    public InputValidationResult(boolean isValid) {
        this._isValid = isValid;
        this._condition = null;
        _input = null;
    }

    public boolean isIsValid() {
        return _isValid;
    }

    public AbstractCondition getCondition() {
        return _condition;
    }

    public AbstractInput getInput() {
        return _input;
    }
    
    public String getInputId()   
    {
        return _input.getId();
    }

    @Override
    public String toString() {
        String condStr = _condition == null ? "null" : _condition.toString();
        return "InputValidationResult{input=" + getInputId() + "; isValid=" + _isValid + "; condition=" + condStr +'}';
    }
}

package mobvey.condition;

import java.io.Serializable;
import mobvey.form.enums.ConditionChoiceType;

/**
 *
 * @author Shamo Humbatli
 */
public class ConditionGroup implements Serializable {

    protected final ConditionCombination _combination;
    protected final AbstractCondition _condition;
    protected final ConditionChoiceType _choiceType;

    public ConditionGroup(ConditionCombination combination) {
        this._combination = combination;
        this._condition = null;
        this._choiceType = ConditionChoiceType.COMBINATION;
    }

    public ConditionGroup(AbstractCondition condition) {
        this._condition = condition;
        this._combination = null;
        this._choiceType = ConditionChoiceType.CONDITION;
    }

    public ConditionCombination getCombination() {
        return _combination;
    }

    public AbstractCondition getCondition() {
        return _condition;
    }

    public ConditionChoiceType getChoiceType() {
        return _choiceType;
    }

    @Override
    public String toString() {
        switch (getChoiceType()) {
            case COMBINATION:
                return String.format("(%s)", _combination.toString());
            case CONDITION:
                return _condition.toString();
            default:
                return null;
        }
    }

}

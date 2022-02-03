package mobvey.condition;

import java.io.Serializable;
/**
 *
 * @author Shamo Humbatli
 */
public class AbstractCondition implements Serializable{

    private final ConditionType conditionType;

    protected AbstractCondition(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }
}

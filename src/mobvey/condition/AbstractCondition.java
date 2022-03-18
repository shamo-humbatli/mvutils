package mobvey.condition;

import mobvey.form.enums.ConditionType;
import java.io.Serializable;
import mobvey.common.ConditionUtil;

/**
 *
 * @author Shamo Humbatli
 */
public abstract class AbstractCondition implements Serializable {

    private final ConditionType conditionType;

    protected AbstractCondition(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public String getConditionExpressionName() {
        return conditionType.toString();
    }

    public String getConditionExpression() {
        return ConditionUtil.getConditionExpression(getConditionExpressionName(), getConditionParams());
    }

    protected abstract String[] getConditionParams();

    @Override
    public String toString() {
        return getConditionExpression();
    }
}

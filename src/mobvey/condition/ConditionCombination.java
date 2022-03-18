package mobvey.condition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import mobvey.common.Strings;
import mobvey.form.enums.ConditionCombinationType;

/**
 *
 * @author Shamo Humbatli
 */
public class ConditionCombination implements Serializable {

    protected ConditionCombinationType _combinationType = ConditionCombinationType.AND;
    protected Collection<ConditionGroup> _conditionGroups;

    public ConditionCombination() {
    }

    public ConditionCombination(ConditionCombinationType conditionCombinationType) {
        _combinationType = conditionCombinationType;
    }

    public ConditionCombinationType getCombinationType() {
        return _combinationType;
    }

    public void setCombinationType(ConditionCombinationType combinationType) {
        this._combinationType = combinationType;
    }

    public Collection<ConditionGroup> getConditionGroups() {

        if (_conditionGroups == null) {
            _conditionGroups = new ArrayList<>();
        }

        return _conditionGroups;
    }

    public void setConditionGroups(Collection<ConditionGroup> conditionGroups) {
        this._conditionGroups = conditionGroups;
    }

    public void addConditions(Collection<ConditionGroup> conditionGroups) {

        if (conditionGroups == null) {
            return;
        }
        getConditionGroups().addAll(conditionGroups);
    }

    public void addConditionGroup(ConditionGroup conditionGroup) {

        if (conditionGroup == null) {
            return;
        }
        getConditionGroups().add(conditionGroup);
    }

    @Override
    public String toString() {

        String cmbs = _combinationType == ConditionCombinationType.AND ? " AND " : " OR ";

        Collection<String> condExps = new ArrayList<>();

        for (ConditionGroup cg : getConditionGroups()) {
            condExps.add(cg.toString());
        }

        return Strings.join(cmbs, condExps);
    }

}

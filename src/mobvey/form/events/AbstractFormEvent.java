package mobvey.form.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import mobvey.common.Strings;
import mobvey.condition.ConditionGroup;
import mobvey.form.enums.FormEventType;
import mobvey.procedure.AbstractProcedure;

/**
 *
 * @author Shamo Humbatli
 */
public abstract class AbstractFormEvent implements Serializable {

    protected final String _id;
    protected FormEventType _eventType;
    protected ConditionGroup _conditionGroup;
    protected Collection<AbstractProcedure> _procedures;

    public AbstractFormEvent() {
        this._id = Strings.getRandomUuidString();
    }

    public String getId() {
        return _id;
    }

    public FormEventType getEventType() {
        return _eventType;
    }

    public void setEventType(FormEventType eventType) {
        this._eventType = eventType;
    }

    public ConditionGroup getConditionGroup() {
        return _conditionGroup;
    }

    public void setConditionGroup(ConditionGroup conditionGroup) {
        this._conditionGroup = conditionGroup;
    }

    public Collection<AbstractProcedure> getProcedures() {

        if (_procedures == null) {
            _procedures = new ArrayList<>();
        }

        return _procedures;
    }

    public void setProcedures(Collection<AbstractProcedure> procedures) {
        this._procedures = procedures;
    }

    private String _toString = null;

    @Override
    public String toString() {

        if (_toString == null) {
            Collection<String> prcStrings = new ArrayList<>();

            for (AbstractProcedure ap : getProcedures()) {
                prcStrings.add(ap.toString());
            }

            String cgStr = "";

            if (_conditionGroup != null) {
                cgStr = _conditionGroup.toString();
            }

            _toString = String.format("on: %s; do: %s; if: %s", _eventType.toString(), Strings.join(", ", prcStrings), cgStr);
        }
        return _toString;
    }
}

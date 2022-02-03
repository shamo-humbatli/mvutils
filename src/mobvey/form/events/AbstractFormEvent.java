package mobvey.form.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import mobvey.condition.AbstractCondition;
import mobvey.form.enums.FormEventType;
import mobvey.procedure.AbstractProcedure;

/**
 *
 * @author ShamoHumbatli
 */
public abstract class AbstractFormEvent implements Serializable {
    protected FormEventType _eventType;
    protected AbstractCondition _condition;
    protected Collection<AbstractProcedure> _procedures;

    public FormEventType getEventType() {
        return _eventType;
    }

    public void setEventType(FormEventType eventType) {
        this._eventType = eventType;
    }

    public AbstractCondition getCondition() {
        return _condition;
    }

    public void setCondition(AbstractCondition condition) {
        this._condition = condition;
    }

    public Collection<AbstractProcedure> getProcedures() {
        
        if(_procedures == null)
            _procedures = new ArrayList<>();
        
        return _procedures;
    }

    public void setProcedures(Collection<AbstractProcedure> procedures) {
        this._procedures = procedures;
    }
}

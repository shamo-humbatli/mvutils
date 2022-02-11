package mobvey.form.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mobvey.form.enums.EventProcedureType;
import mobvey.form.enums.FormElementType;
import mobvey.form.events.AbstractFormEvent;
import mobvey.procedure.AbstractProcedure;

/**
 *
 * @author ShamoHumbatli
 */
public abstract class AbstractFormElement implements Serializable {

    protected boolean _enabled = true;
    protected String _id;
    protected String _parentId;
    protected final FormElementType _elementType;
    protected List<AbstractFormEvent> _events;

    public AbstractFormElement(FormElementType elementType) {
        this._elementType = elementType;
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean enabled) {
        this._enabled = enabled;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public FormElementType getElementType() {
        return _elementType;
    }

    public List<AbstractFormEvent> getEvents() {

        if (_events == null) {
            _events = new ArrayList<>();
        }

        return _events;
    }

    public boolean hasAtLeastOneEventByProcedureBy(EventProcedureType ept) {
        for (AbstractFormEvent afe : getEvents()) {
            Collection<AbstractProcedure> aps = afe.getProcedures();
            if (aps == null) {
                continue;
            }

            for (AbstractProcedure ap : aps) {
                if (ap.getProcedureType().equals(ept)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public void setEvents(List<AbstractFormEvent> events) {
        this._events = events;
    }

    public void addEvents(Collection<? extends AbstractFormEvent> events) {
        if (events == null) {
            return;
        }

        getEvents().addAll(events);
    }

    public void addEvent(AbstractFormEvent event) {
        if (event == null) {
            return;
        }

        getEvents().add(event);
    }

    public String getParentId() {
        return _parentId;
    }

    public void setParentId(String parentId) {
        this._parentId = parentId;
    }

    public void setParent(AbstractFormElement parent) {
        if (parent == null) {
            return;
        }

        setParentId(parent.getId());
    }

    public static void setParent(Collection<? extends AbstractFormElement> elements, AbstractFormElement parent) {
        if (elements == null) {
            return;
        }

        for (AbstractFormElement afe : elements) {
            afe.setParent(parent);
        }
    }

    public abstract AbstractFormElement CloneExact();

}

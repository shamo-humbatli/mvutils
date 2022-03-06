package mobvey.form.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import mobvey.common.Strings;
import mobvey.form.enums.EventProcedureType;
import mobvey.form.enums.FormElementType;
import mobvey.form.events.AbstractFormEvent;
import mobvey.procedure.AbstractProcedure;

/**
 *
 * @author Shamo Humbatli
 */
public abstract class AbstractFormElement implements Serializable {
    
    protected boolean _enabled = true;
    protected String _id;
    protected String _parentId;
    protected final FormElementType _elementType;
    protected Collection<AbstractFormEvent> _events;
    protected Collection<String> _classNames;
    
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
    
    public Collection<AbstractFormEvent> getEvents() {
        
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
    
    public void setEvents(Collection<AbstractFormEvent> events) {
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
    
    public Collection<String> getClassNames() {
        
        if (_classNames == null) {
            _classNames = new ArrayList<>();
        }
        
        return _classNames;
    }
    
    public void setClassNames(Collection<String> classNames) {
        this._classNames = classNames;
    }
    
    public void addClassName(String className) {
        
        if (Strings.isNullOrEmpty(className)) {
            return;
        }
        
        getClassNames().add(className);
    }
    
    public void addClassNames(Collection<? extends String> classNames) {
        
        if (classNames == null || classNames.isEmpty()) {
            return;
        }
        
        getClassNames().addAll(classNames);
    }
    
    public boolean hasClass(String className) {
        return getClassNames().contains(className);
    }
    
    public boolean hasAnyClass() {
        return !getClassNames().isEmpty();
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
    
    protected void applyCloneBase(AbstractFormElement newClone) {
        newClone.setId(_id);
        newClone.setEnabled(_enabled);
        newClone.setParentId(_parentId);
        newClone.setClassNames(new ArrayList<>(getClassNames()));
        newClone.setEvents(new ArrayList<>(getEvents()));
    }
}

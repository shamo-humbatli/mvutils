package mobvey.form.elements;

import java.util.ArrayList;
import java.util.Collection;
import mobvey.form.enums.FormElementType;

/**
 *
 * @author Shamo Humbatli
 */
public class FormBody extends AbstractFormElement {
    
    public FormBody() {
        super(FormElementType.FORM_BODY);
    }
    protected Collection<AbstractFormElement> _children;
    
    public Collection<AbstractFormElement> getChildren() {
        
        if (_children == null) {
            _children = new ArrayList<>();
        }
        
        return _children;
    }
    
    public void setChildren(Collection<AbstractFormElement> children) {
        
        if(children == null)
            return;
        
        setParent(children, this);
        
        this._children = children;
    }
    
    public void addChildren(Collection<AbstractFormElement> children) {
        
        if (children == null) {
            return;
        }
        
        setParent(children, this);
        
        getChildren().addAll(children);
    }
    
    public void addChild(AbstractFormElement child) {
        
        if (child == null) {
            return;
        }
        
        child.setParent(this);
        
        getChildren().add(child);
    }
    
    @Override
    public AbstractFormElement CloneExact() {
        FormBody fb = new FormBody();
        
        applyCloneBase(fb);
        
        for (AbstractFormElement child : getChildren()) {
            fb.addChild(child.CloneExact());
        }
        
        return fb;
    }
}

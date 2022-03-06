package mobvey.form.elements;

import java.util.ArrayList;
import java.util.Collection;
import static mobvey.form.elements.AbstractFormElement.setParent;
import mobvey.form.enums.FormElementType;

/**
 *
 * @author Shamo Humbatli
 */
public class Section extends AbstractFormElement {

    public Section() {
        super(FormElementType.SECTION);
    }
    protected String _title;
    protected String _description;

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

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    @Override
    public AbstractFormElement CloneExact() {
        Section sc = new Section();

        applyCloneBase(sc);

        sc.setTitle(_title);
        sc.setDescription(_description);

        for (AbstractFormElement child : getChildren()) {
            sc.addChild(child.CloneExact());
        }

        return sc;
    }

    @Override
    public String toString() {
        return "Section{" + "_title=" + _title + ", _description=" + _description + '}';
    }
}

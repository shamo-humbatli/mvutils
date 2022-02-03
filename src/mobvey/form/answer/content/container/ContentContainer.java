package mobvey.form.answer.content.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mobvey.form.base.AbstractFormElement;
import mobvey.form.base.AbstractInput;
import mobvey.form.enums.DisplayType;
import mobvey.form.enums.FormElementType;
import mobvey.form.enums.ResultType;

/**
 *
 * @author Shamo Humbatli
 */
public class ContentContainer extends AbstractFormElement {

    private String displayText = null;
    private ResultType resultType = ResultType.SINGLE;
    private DisplayType displayType;
    private boolean returnRequeired = false;

    private List<AbstractInput> contentInputs = new ArrayList<>();

    public ContentContainer() {
        super(FormElementType.CONTENT_CONTAINER);
    }

    public String getDisplayText() {
        return displayText;
    }

    public boolean isReturnRequeired() {
        return returnRequeired;
    }

    public void setReturnRequeired(boolean returnRequeired) {
        this.returnRequeired = returnRequeired;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
    }

    public List<AbstractInput> getContentInputs() {
        if (contentInputs == null) {
            contentInputs = new ArrayList<AbstractInput>();
        }
        return contentInputs;
    }

    public void AddContentInput(AbstractInput abstractInput) {

        if (abstractInput == null) {
            return;
        }

        abstractInput.setParent(this);

        this.getContentInputs().add(abstractInput);
    }

    public void AddContentInputsRange(Collection<? extends AbstractInput> abstractInputs) {

        if (abstractInputs == null) {
            return;
        }

        setParent(contentInputs, this);
        
        this.getContentInputs().addAll(abstractInputs);
    }

    @Override
    public ContentContainer CloneExact() {
        ContentContainer cc = new ContentContainer();

        cc.setDisplayText(displayText);
        cc.setDisplayType(displayType);
        cc.setId(_id);
        cc.setParentId(_parentId);
        cc.setEnabled(_enabled);
        cc.setResultType(resultType);
        cc.setReturnRequeired(returnRequeired);

        if (_events != null) {
            cc.setEvents(new ArrayList<>(_events));
        }

        if (contentInputs != null) {
            for (AbstractInput ai : getContentInputs()) {

                if (ai == null) {
                    continue;
                }

                AbstractInput clonedAi = (AbstractInput)ai.CloneExact();
                cc.AddContentInput(clonedAi);
            }
        }

        return cc;
    }

    public boolean hasInputs() {
        return contentInputs != null && contentInputs.size() > 0;
    }

    @Override
    public String toString() {
        return "ContentContainer{" + "id=" + _id + ", displayText=" + displayText + ", resultType=" + resultType + ", displayType=" + displayType + '}';
    }
}

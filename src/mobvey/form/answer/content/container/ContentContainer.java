package mobvey.form.answer.content.container;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mobvey.form.base.AbstractInput;
import mobvey.form.enums.DisplayTypes;
import mobvey.form.enums.ResultTypes;

/**
 *
 * @author Shamo Humbatli
 */
public class ContentContainer implements Serializable {

    private String id = null;
    private String displayText = null;
    private ResultTypes resultType = ResultTypes.SINGLE;
    private DisplayTypes displayType;
    private boolean returnRequeired = false;

    private List<AbstractInput> contentInputs = new ArrayList<>();

    public ContentContainer() {
    }

    public ContentContainer(DisplayTypes displayType) {
        this.displayType = displayType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ResultTypes getResultType() {
        return resultType;
    }

    public void setResultType(ResultTypes resultType) {
        this.resultType = resultType;
    }

    public DisplayTypes getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DisplayTypes displayType) {
        this.displayType = displayType;
    }

    public List<AbstractInput> getContentInputs() {
        return contentInputs;
    }

    public void AddContentInput(AbstractInput abstractInput) {
        this.contentInputs.add(abstractInput);
    }

    public void AddContentInputsRange(Collection<? extends AbstractInput> abstractInput) {
        this.contentInputs.addAll(abstractInput);
    }

    public ContentContainer CloneExact() {
        ContentContainer cc = new ContentContainer();

        cc.setDisplayText(displayText);
        cc.setDisplayType(displayType);
        cc.setId(id);
        cc.setResultType(resultType);
        cc.setReturnRequeired(returnRequeired);

        if (contentInputs != null) {
            for (AbstractInput ai : getContentInputs()) {

                if (ai == null) {
                    continue;
                }

                AbstractInput clonedAi = ai.CloneExact();
                cc.AddContentInput(clonedAi);
            }
        }

        return cc;
    }
    
    public boolean hasInputs()
    {
        return contentInputs != null && contentInputs.size() > 0;
    }

    @Override
    public String toString() {
        return "ContentContainer{" + "id=" + id + ", displayText=" + displayText + ", resultType=" + resultType + ", displayType=" + displayType + '}';
    }
}

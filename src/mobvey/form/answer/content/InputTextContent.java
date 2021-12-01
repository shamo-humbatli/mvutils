package mobvey.form.answer.content;

import java.util.ArrayList;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractInput;
import mobvey.form.enums.InputTypes;

/**
 *
 * @author Shamo Humbatli
 */
public class InputTextContent extends AbstractInput {

    private String placeHolder;
    private boolean readonly = false;

    public InputTextContent() {
        super(InputTypes.TEXT);
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public AbstractInput CloneExact() {
        InputTextContent itc = new InputTextContent();

        itc.setColumnDefinition(columnDefinition);
        itc.setColumnDefinitionDeclaredByDefault(columnDefinitionDeclaredByDefault);
        itc.setColumnDefinitionType(columnDefinitionType);
        itc.setComplex(complex);
        itc.setContentItemIndex(contentItemIndex);
        itc.setId(id);
        itc.setInputValueType(inputValueType);
        itc.setOperation(operation);
        itc.setParentId(parentId);
        itc.setReturnContent(returnContent);
        itc.setDisplayContent(displayContent);
        itc.setReadonly(readonly);
        itc.setPlaceHolder(placeHolder);
        itc.setMinValue(minValue);
        itc.setMaxValue(maxValue);

        if (containersRequired != null) {
            itc.setContainersRequired(new ArrayList<>(containersRequired));
        }

        if (questionsToDisable != null) {
            itc.setQuestionsToDisable(new ArrayList<>(questionsToDisable));
        }

        if (questionsToEnable != null) {
            itc.setQuestionsToEnable(new ArrayList<>(questionsToEnable));
        }

        if (contentContainers != null) {
            for (ContentContainer cc : getContentContainers()) {

                if (cc == null) {
                    continue;
                }

                ContentContainer ccCloned = cc.CloneExact();
                itc.AddContentContainer(ccCloned);
            }
        }

        return itc;
    }
}

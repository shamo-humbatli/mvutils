package mobvey.form.answer.content;

import java.util.ArrayList;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.base.AbstractInput;
import mobvey.form.enums.InputTypes;

/**
 *
 * @author Shamo Humbatli
 */
public class InputOptionContent extends AbstractInput {

    public InputOptionContent() {
        super(InputTypes.OPTION);
    }

    @Override
    public AbstractInput CloneExact() {
        InputOptionContent ioc = new InputOptionContent();

        ioc.setColumnDefinition(columnDefinition);
        ioc.setColumnDefinitionDeclaredByDefault(columnDefinitionDeclaredByDefault);
        ioc.setColumnDefinitionType(columnDefinitionType);
        ioc.setComplex(complex);
        ioc.setContentItemIndex(contentItemIndex);
        ioc.setId(id);
        ioc.setInputValueType(inputValueType);
        ioc.setOperation(operation);
        ioc.setParentId(parentId);
        ioc.setReturnContent(returnContent);
        ioc.setDisplayContent(displayContent);

        if (elementsToEnable != null) {
            ioc.setElementsToEnable(new ArrayList<>(elementsToEnable));
        }
        
        if (elementsToDisable != null) {
            ioc.setElementsToDisable(new ArrayList<>(elementsToDisable));
        }

        if (containersRequired != null) {
            ioc.setContainersRequired(new ArrayList<>(containersRequired));
        }

        if (questionsToDisable != null) {
            ioc.setQuestionsToDisable(new ArrayList<>(questionsToDisable));
        }

        if (questionsToEnable != null) {
            ioc.setQuestionsToEnable(new ArrayList<>(questionsToEnable));
        }

        if (contentContainers != null) {
            for (ContentContainer cc : getContentContainers()) {

                if (cc == null) {
                    continue;
                }

                ContentContainer ccCloned = cc.CloneExact();
                ioc.AddContentContainer(ccCloned);
            }
        }

        return ioc;
    }
}

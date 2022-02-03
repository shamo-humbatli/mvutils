package mobvey.form.base;

import java.util.ArrayList;
import java.util.List;
import mobvey.form.answer.content.container.ContentContainer;
import mobvey.form.enums.FormElementType;

/**
 *
 * @author Shamo Humbatli
 */
public abstract class AbstractAnswer extends AbstractFormElement {

    public AbstractAnswer(FormElementType elementType) {
        super(elementType);
    }

    protected List<ContentContainer> answerContentContainers = new ArrayList<ContentContainer>();

    public List<ContentContainer> getAnswerContentContainers() {

        if (answerContentContainers == null) {
            answerContentContainers = new ArrayList<ContentContainer>();
        }

        return answerContentContainers;
    }

    public void setAnswerContentContainers(List<ContentContainer> answerContentContainers) {
        this.answerContentContainers = answerContentContainers;
        setParent(answerContentContainers, this);
    }

    public void AddContentContainer(ContentContainer contentContainer) {
        if(contentContainer == null)
            return;
        
        contentContainer.setParent(this);
        getAnswerContentContainers().add(contentContainer);
    }


    @Override
    public String toString() {
        return "AbstractAnswer{" + "answerId=" + _id + ", enabled=" + _enabled + '}';
    }
}

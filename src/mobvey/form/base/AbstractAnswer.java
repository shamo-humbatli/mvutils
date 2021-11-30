package mobvey.form.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mobvey.form.answer.content.container.ContentContainer;

/**
 *
 * @author Shamo Humbatli
 */
public abstract class AbstractAnswer implements Serializable{

    protected String answerId;
    protected boolean enabled = true;
    protected List<ContentContainer> answerContentContainers = new ArrayList<ContentContainer>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public List<ContentContainer> getAnswerContentContainers() {
        return answerContentContainers;
    }

    public void setAnswerContentContainers(List<ContentContainer> answerContentContainers) {
        this.answerContentContainers = answerContentContainers;
    }

    public void AddContentContainer(ContentContainer contentContainer) {
        answerContentContainers.add(contentContainer);
    }

    public abstract AbstractAnswer CloneExact();

    @Override
    public String toString() {
        return "AbstractAnswer{" + "answerId=" + answerId + ", enabled=" + enabled + '}';
    }
}

package mobvey.form;

import java.util.ArrayList;
import java.util.List;
import mobvey.form.base.AbstractFormElement;
import mobvey.form.enums.FormElementType;
import mobvey.form.question.Question;

/**
 *
 * @author Shamo Humbatli
 */
public class QuestionForm extends AbstractFormElement {

    public QuestionForm() {
        super(FormElementType.FORM);
    }
    
    protected List<Question> _questions;
    protected String _language;
    protected String _version;
    protected String _description;
    protected String _title;
    protected String _actualShortDescription;

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public String getLanguage() {
        return _language;
    }

    public void setLanguage(String formLanguage) {
        this._language = formLanguage;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String formVersion) {
        this._version = formVersion;
    }

    public void addQuestion(Question question) {
        if (_questions == null) {
            _questions = new ArrayList<>();
        }

        if(question == null)
            return;
        
        question.setParent(this);
        
        _questions.add(question);
    }

    public List<Question> getQuestions() {

        if (_questions == null) {
            _questions = new ArrayList<Question>();
        }

        return _questions;
    }

    public boolean hasQuestions() {
        if (_questions == null || _questions.isEmpty()) {
            return false;
        }

        return true;
    }

    public String getActualShortDescription() {
        return _actualShortDescription;
    }

    public void setActualShortDescription(String actualShortDescription) {
        this._actualShortDescription = actualShortDescription;
    }
    
    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        this._title = title;
    }
    
     @Override
    public QuestionForm CloneExact() {

        QuestionForm qf = new QuestionForm();

        qf.setDescription(getDescription());
        qf.setActualShortDescription(getActualShortDescription());
        qf.setId(getId());
        qf.setLanguage(getLanguage());
        qf.setVersion(getVersion());
        qf.setTitle(getTitle());

        if (_questions != null) {
            for (Question q : getQuestions()) {

                if (q == null) {
                    continue;
                }

                Question qCloned = q.CloneExact();
                qf.addQuestion(qCloned);
            }
        }

        return qf;
    }

    @Override
    public String toString() {
        return "QuestionFormData{" + "formId=" + _id + ", formLanguage=" + _language + ", formVersion=" + _version + ", description=" + _description + ", title=" + _title + '}';
    }

}

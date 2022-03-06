package mobvey.form.elements;

import mobvey.form.elements.AbstractFormElement;
import mobvey.form.elements.FormBody;
import mobvey.form.enums.FormElementType;

/**
 *
 * @author Shamo Humbatli
 */
public class QuestionForm extends AbstractFormElement {
    
    public QuestionForm() {
        super(FormElementType.FORM);
    }
    
    protected FormBody _body;
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
    
    public FormBody getBody() {
        return _body;
    }
    
    public void setBody(FormBody body) {
        
        if(body == null)
            return;
        
        body.setParent(this);
        
        this._body = body;
    }
    
    @Override
    public QuestionForm CloneExact() {
        
        QuestionForm qf = new QuestionForm();
        
        applyCloneBase(qf);
        
        qf.setDescription(getDescription());
        qf.setActualShortDescription(getActualShortDescription());
        
        qf.setLanguage(getLanguage());
        qf.setVersion(getVersion());
        qf.setTitle(getTitle());
        
        if (_body != null) {
            qf.setBody((FormBody) _body.CloneExact());
        }
        
        return qf;
    }
    
    @Override
    public String toString() {
        return "QuestionFormData{" + "formId=" + _id + ", formLanguage=" + _language + ", formVersion=" + _version + ", description=" + _description + ", title=" + _title + '}';
    }
    
}

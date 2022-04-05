package mobvey.form.enums;

/**
 *
 * @author ShamoHumbatli
 */
public enum ConditionType {
    COMPARE_WITH_DV,
    COMPARE_WITH_IV,
    COMPARE_IV_DV,
    COMPARE_IV_IV,
    
    IS_IN_RANGE_DVS,
    IS_IN_RANGE_IVS,
    
    HAS_CONTENT,
    HAS_NO_CONTENT,   
    
    CHECK_ENABLED,    
    CHECK_ENABLED_ELMS,
    
    CHECK_CHECKED,
    CHECK_CHECKED_ELMS,
    
    CHECK_REGEX,
    CHECK_REGEX_ELMS,
    
    CHECK_LENGTH,
    CHECK_LENGTH_ELMS,
    CHECK_LENGTH_RANGE,
    CHECK_LENGTH_RANGE_ELMS,
    
    HAS_CONTENT_IVS,
    HAS_NO_CONTENT_IVS,
}

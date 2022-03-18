package mobvey.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import mobvey.condition.AbstractCondition;
import mobvey.condition.CheckCheckedCondition;
import mobvey.condition.CheckElementsAreCheckedCondition;
import mobvey.condition.CheckElementsAreEnabledCondition;
import mobvey.condition.CheckEnabledCondition;
import mobvey.condition.CompareInputValueWithDirectValueCondition;
import mobvey.condition.CompareInputValueWithInputValueCondition;
import mobvey.condition.CompareWithDirectValueCondition;
import mobvey.condition.CompareWithInputValueCondition;
import mobvey.condition.ConditionCombination;
import mobvey.condition.ConditionGroup;
import mobvey.condition.HasContentCondition;
import mobvey.condition.HasContentOfInputsCondition;
import mobvey.condition.HasNoContentCondition;
import mobvey.condition.HasNoContentOfInputsCondition;
import mobvey.condition.IsInRangeOfDirectValuesCondition;
import mobvey.condition.IsInRangeOfInputValuesCondition;
import mobvey.form.enums.ComparisonType;
import mobvey.form.enums.ConditionCombinationType;
import mobvey.form.enums.ConditionType;

/**
 *
 * @author Shamo Humbatli
 */
public class ConditionUtil {

    public static String getConditionExpression(String conditionName, String... params) {
        if (params == null || params.length == 0) {
            return conditionName;
        }

        return String.format("%s(%s)", conditionName, Strings.join(",", params));
    }

    public static Collection<AbstractCondition> buildConditions(String conditionsStr) {
        List<AbstractCondition> conditions = new ArrayList<>();
        if (Strings.isNullOrEmpty(conditionsStr)) {
            return conditions;
        }

        String[] conditionLines = ParamUtil.getParamArrayItems(conditionsStr);

        for (String conditionLine : conditionLines) {
            AbstractCondition ac = buildCondition(conditionLine);

            if (ac == null) {
                continue;
            }

            conditions.add(ac);
        }

        return conditions;
    }

    public static ConditionGroup buildConditionGroup(String conditionChoiceStr) {

        if (Strings.isNullOrEmpty(conditionChoiceStr)) {
            return null;
        }

        conditionChoiceStr = conditionChoiceStr.trim();
        int vl = conditionChoiceStr.length();

        if (conditionChoiceStr.indexOf("(") == 0 && conditionChoiceStr.lastIndexOf(")") == vl - 1) {
            conditionChoiceStr = conditionChoiceStr.substring(1, vl - 1);
        }

        String[] cgs = ParamUtil.stringSplitByDepth(conditionChoiceStr, " AND ", 0, "(", ")");

        if (cgs.length == 0) {
            return null;
        }

        ConditionCombinationType cct = ConditionCombinationType.AND;
        if (cgs.length < 2) {
            cgs = ParamUtil.stringSplitByDepth(conditionChoiceStr, " OR ", 0, "(", ")");
            cct = ConditionCombinationType.OR;
        }

        if (cgs.length == 1) {
            AbstractCondition ac = buildCondition(cgs[0]);

            if (ac == null) {
                return null;
            }

            return new ConditionGroup(ac);
        }

        
        ConditionCombination condComb = new ConditionCombination(cct);

        for (String cg : cgs) {
            ConditionGroup cgSub = buildConditionGroup(cg);

            if (cgSub == null) {
                continue;
            }

            condComb.addConditionGroup(cgSub);
        }

        if (condComb.getConditionGroups().isEmpty()) {
            return null;
        }

        return new ConditionGroup(condComb);
    }

    public static AbstractCondition buildCondition(String conditionStr) {

        if (Strings.isNullOrEmpty(conditionStr)) {
            return null;
        }

        int p11 = conditionStr.indexOf("(");
        int p12 = conditionStr.indexOf(")");
        String contentRaw = "";
        String cmd = "";

        if (p11 >= 0 && p12 >= 0) {
            contentRaw = conditionStr.substring(p11 + 1, p12).trim();
            cmd = conditionStr.substring(0, p11).trim();
        } else {
            cmd = conditionStr.trim();
        }

        ConditionType opType = ConditionType.valueOf(cmd.toUpperCase());

        AbstractCondition abstractCondition = null;

        switch (opType) {
            case COMPARE_WITH_DV: {
                String[] params = ParamUtil.getParamArrayItems(contentRaw);

                CompareWithDirectValueCondition cvdv = new CompareWithDirectValueCondition();
                cvdv.setCompareWith(Double.valueOf(params[0].trim()));
                cvdv.setComparisonType(ComparisonType.valueOf(params[1].trim().toUpperCase()));
                abstractCondition = cvdv;
            }
            break;
            case COMPARE_WITH_IV: {
                String[] params = ParamUtil.getParamArrayItems(contentRaw);

                CompareWithInputValueCondition condition = new CompareWithInputValueCondition();
                condition.setCompareWith(params[0].trim());
                condition.setComparisonType(ComparisonType.valueOf(params[1].trim().toUpperCase()));
                abstractCondition = condition;
            }
            break;
            case IS_IN_RANGE_DVS: {
                String[] params = ParamUtil.getParamArrayItems(contentRaw);

                IsInRangeOfDirectValuesCondition condition = new IsInRangeOfDirectValuesCondition();
                condition.setMinValue(Double.valueOf(params[0].trim()));
                condition.setMaxValue(Double.valueOf(params[1].trim()));
                abstractCondition = condition;
            }
            break;
            case IS_IN_RANGE_IVS: {
                String[] params = ParamUtil.getParamArrayItems(contentRaw);

                IsInRangeOfInputValuesCondition condition = new IsInRangeOfInputValuesCondition();
                condition.setMinValueInputId(params[0].trim());
                condition.setMaxValueInputId(params[1].trim());
                abstractCondition = condition;
            }
            break;
            case COMPARE_IV_DV: {
                String[] params = ParamUtil.getParamArrayItems(contentRaw);

                CompareInputValueWithDirectValueCondition condition = new CompareInputValueWithDirectValueCondition();
                condition.setComparingInputId(params[0].trim());
                condition.setCompareWith(Double.valueOf(params[1].trim()));
                condition.setComparisonType(ComparisonType.valueOf(params[2].trim().toUpperCase()));
                abstractCondition = condition;
            }
            break;
            case COMPARE_IV_IV: {
                String[] params = ParamUtil.getParamArrayItems(contentRaw);

                CompareInputValueWithInputValueCondition condition = new CompareInputValueWithInputValueCondition();
                condition.setComparingInputId(params[0].trim());
                condition.setCompareWithInputId(params[1].trim());
                condition.setComparisonType(ComparisonType.valueOf(params[2].trim().toUpperCase()));
                abstractCondition = condition;
            }
            break;
            case HAS_CONTENT: {
                abstractCondition = new HasContentCondition();
            }
            break;
            case HAS_NO_CONTENT: {
                abstractCondition = new HasNoContentCondition();
            }
            break;
            case HAS_CONTENT_IVS: {
                String[] params = ParamUtil.getParamArrayItems(contentRaw);

                HasContentOfInputsCondition condition = new HasContentOfInputsCondition();
                condition.setInputIds(Arrays.asList(params));
                abstractCondition = condition;
            }
            break;
            case HAS_NO_CONTENT_IVS: {
                String[] params = ParamUtil.getParamArrayItems(contentRaw);

                HasNoContentOfInputsCondition condition = new HasNoContentOfInputsCondition();
                condition.setInputIds(Arrays.asList(params));
                abstractCondition = condition;
            }
            break;
            case CHECK_ENABLED: {
                CheckEnabledCondition condition = new CheckEnabledCondition();

                if (Strings.hasContent(contentRaw)) {
                    condition.setEnabled(Boolean.valueOf(contentRaw.toLowerCase()));
                }

                abstractCondition = condition;
            }
            break;
            case CHECK_ENABLED_ELMS: {
                String[] params = ParamUtil.getParamArrayItems(contentRaw);

                CheckElementsAreEnabledCondition condition = new CheckElementsAreEnabledCondition();

                if (params.length > 1) {
                    condition.setEnabled(Boolean.valueOf(params[0].toLowerCase()));
                    condition.setElementsIds(Arrays.asList(ParamUtil.getParamArrayItems(params[1])));
                } else {
                    condition.setElementsIds(Arrays.asList(ParamUtil.getParamArrayItems(params[0])));
                }

                abstractCondition = condition;
            }
            break;
            case CHECK_CHECKED: {
                CheckCheckedCondition condition = new CheckCheckedCondition();

                if (Strings.hasContent(contentRaw)) {
                    condition.setChecked(Boolean.valueOf(contentRaw.toLowerCase()));
                }

                abstractCondition = condition;
            }
            break;
            case CHECK_CHECKED_ELMS: {
                String[] params = ParamUtil.getParamArrayItems(contentRaw);

                CheckElementsAreCheckedCondition condition = new CheckElementsAreCheckedCondition();

                if (params.length > 1) {
                    condition.setChecked(Boolean.valueOf(params[0].toLowerCase()));
                    condition.setElementsIds(Arrays.asList(ParamUtil.getParamArrayItems(params[1])));
                } else {
                    condition.setElementsIds(Arrays.asList(ParamUtil.getParamArrayItems(params[0])));
                }

                abstractCondition = condition;
            }
            break;
            default:
                return null;

        }

        return abstractCondition;
    }
}

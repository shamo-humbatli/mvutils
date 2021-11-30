/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobvey.form.answer.content.operation;

import mobvey.form.enums.ComparisonType;

/**
 *
 * @author ShamoHumbatli
 */
public class ComparisonOperation extends AbstractOperation {

    private String compareWith;
    private ComparisonType comparisonType;

    public String getCompareWith() {
        return compareWith;
    }

    public void setCompareWith(String compareWith) {
        this.compareWith = compareWith;
    }

    public ComparisonType getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(ComparisonType comparisonType) {
        this.comparisonType = comparisonType;
    }

    public ComparisonOperation() {
        super(OperationType.COMPARE);
    }
}

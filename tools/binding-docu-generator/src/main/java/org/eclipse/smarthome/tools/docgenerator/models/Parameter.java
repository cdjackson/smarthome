package org.eclipse.smarthome.tools.docgenerator.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.smarthome.tools.docgenerator.util.BooleanUtils;

public class Parameter implements Model<org.eclipse.smarthome.tools.docgenerator.schemas.Parameter> {
    /**
     * Instance from the XML parser.
     */
    private org.eclipse.smarthome.tools.docgenerator.schemas.Parameter delegate;

    /**
     * Default constructor.
     */
    public Parameter() {
    }

    /**
     * Constructor.
     *
     * @param delegate Instance from the XML parser.
     */
    public Parameter(org.eclipse.smarthome.tools.docgenerator.schemas.Parameter delegate) {
        this.delegate = delegate;
    }

    /**
     * @return The original instance from the XML parser.
     */
    @Override
    public org.eclipse.smarthome.tools.docgenerator.schemas.Parameter getRealImpl() {
        return delegate;
    }

    /**
     * Set the model.
     *
     * @param parameter The instance from the XML parser.
     */
    @Override
    public void setModel(org.eclipse.smarthome.tools.docgenerator.schemas.Parameter parameter) {
        this.delegate = parameter;
    }

    /**
     * @return Type of the parameter.
     */
    public String type() {
        return delegate.getType().toString();
    }

    /**
     * @return Name of the parameter.
     */
    public String name() {
        return delegate.getName();
    }

    /**
     * @return Label of the parameter.
     */
    public String label() {
        return delegate.getLabel();
    }

    /**
     * @return Description of the parameter.
     */
    public String description() {
        return delegate.getDescription();
    }

    /**
     * @return Whether the parameter is readonly.
     */
    public Boolean readOnly() {
        return delegate.isReadOnly();
    }

    /**
     * Wrapper method for mustache.
     *
     * @return "True" if it is readonly, "False" if not.
     */
    public String isReadOnly() {
        return BooleanUtils.booleanToTrueOrFalse(readOnly());
    }

    /**
     * @return Whether the parameter is required.
     */
    public Boolean required() {
        return delegate.isRequired();
    }

    /**
     * @return "True" if it is required, "False" if not.
     */
    public String isRequired() {
        return BooleanUtils.booleanToTrueOrFalse(required());
    }

    /**
     * @return The default value of the parameter.
     */
    public String defaultValue() {
        return delegate.getDefault();
    }

    /**
     * @return The minimal value of the parameter.
     */
    public BigDecimal min() {
        return delegate.getMin();
    }

    /**
     * @return The maximal value of the parameter.
     */
    public BigDecimal max() {
        return delegate.getMax();
    }

    /**
     * @return The step in between the values for this parameter.
     */
    public BigDecimal step() {
        return delegate.getStep();
    }

    /**
     * @return A list of options.
     */
    public List<org.eclipse.smarthome.tools.docgenerator.schemas.OptionsType.Option> options() {
        List<org.eclipse.smarthome.tools.docgenerator.schemas.OptionsType.Option> optionList = new ArrayList<org.eclipse.smarthome.tools.docgenerator.schemas.OptionsType.Option>();
        if (delegate.getOptions() != null) {
            for (org.eclipse.smarthome.tools.docgenerator.schemas.OptionsType.Option option : delegate.getOptions()
                    .getOption()) {
                optionList.add(option);
            }
        }
        return optionList;
    }

    /**
     * @return The parameter of the parameter.
     */
    public String pattern() {
        return delegate.getPattern();
    }
}

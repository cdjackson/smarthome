package org.eclipse.smarthome.tools.docgenerator.models;

import java.util.ArrayList;
import java.util.List;

public class ConfigDescription implements Model<org.eclipse.smarthome.tools.docgenerator.schemas.ConfigDescription> {
    /**
     * The original instance from the XML parser.
     */
    private org.eclipse.smarthome.tools.docgenerator.schemas.ConfigDescription delegate;

    /**
     * Default constructor.
     */
    public ConfigDescription() {
    }

    /**
     * Constructor.
     *
     * @param delegtae The instance from the XML parser.
     */
    public ConfigDescription(org.eclipse.smarthome.tools.docgenerator.schemas.ConfigDescription delegtae) {
        this.delegate = delegtae;
    }

    /**
     * @return The instance from the XML parser.
     */
    @Override
    public org.eclipse.smarthome.tools.docgenerator.schemas.ConfigDescription getRealImpl() {
        return delegate;
    }

    /**
     * Set the model.
     *
     * @param config The instance from the XML parser.
     */
    @Override
    public void setModel(org.eclipse.smarthome.tools.docgenerator.schemas.ConfigDescription config) {
        this.delegate = config;
    }

    /**
     * @return The URI of the configuration.
     */
    public String uri() {
        return delegate.getUri();
    }

    /**
     * @return A list of parameters.
     */
    public List<org.eclipse.smarthome.tools.docgenerator.schemas.Parameter> parameter() {
        List<org.eclipse.smarthome.tools.docgenerator.schemas.Parameter> parameterList = new ArrayList<org.eclipse.smarthome.tools.docgenerator.schemas.Parameter>();
        for (org.eclipse.smarthome.tools.docgenerator.schemas.Parameter param : delegate.getParameter()) {
            parameterList.add(param);
        }
        return parameterList;
    }
}

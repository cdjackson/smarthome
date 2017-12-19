package org.eclipse.smarthome.tools.docgenerator.data;

import org.eclipse.smarthome.tools.docgenerator.models.ChannelType;

public class ChannelTypeList extends ModelList {
    /**
     * @return Returns a new {@link ChannelType} object.
     */
    @Override
    public ChannelType getNewModel() {
        return new ChannelType();
    }
}


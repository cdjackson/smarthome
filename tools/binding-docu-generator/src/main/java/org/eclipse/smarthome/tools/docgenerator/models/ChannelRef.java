package org.eclipse.smarthome.tools.docgenerator.models;

import org.eclipse.smarthome.tools.docgenerator.schemas.Channel;
import org.eclipse.smarthome.tools.docgenerator.schemas.ChannelType;

public class ChannelRef implements Model<org.eclipse.smarthome.tools.docgenerator.schemas.Channel> {
    /**
     * Instance from the XML parser.
     */
    private Channel delegate;

    /**
     * Instance from the XML parser.
     */
    private ChannelType channelType;

    /**
     * Default constructor.
     */
    public ChannelRef() {
    }

    /**
     * Constructor.
     *
     * @param delegate Instance from the XML parser.
     */
    public ChannelRef(Channel delegate) {
        this.delegate = delegate;
    }

    /**
     * @return Instance from the XML parser.
     */
    @Override
    public Channel getRealImpl() {
        return delegate;
    }

    /**
     * @param channel Instance from the XML parser.
     */
    @Override
    public void setModel(Channel channel) {
        this.delegate = channel;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    /**
     * @return Id of the channel reference.
     */
    public String id() {
        return delegate.getId();
    }

    /**
     * @return Label of the channel reference.
     */
    public String label() {
        return delegate.getLabel();
    }

    /**
     * @return Id of the channel referenced.
     */
    public String typeId() {
        return delegate.getTypeId();
    }
}

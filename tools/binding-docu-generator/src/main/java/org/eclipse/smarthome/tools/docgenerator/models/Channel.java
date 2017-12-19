package org.eclipse.smarthome.tools.docgenerator.models;

/**
 * Wrapper class to not fully depend on the existing models.
 */
public class Channel implements Model<org.eclipse.smarthome.tools.docgenerator.schemas.Channel> {
    /**
     * The object we obtained by the XML parser.
     */
    private org.eclipse.smarthome.tools.docgenerator.schemas.ChannelType typeDelegate;
    private org.eclipse.smarthome.tools.docgenerator.schemas.Channel delegate;

    /**
     * Default constructor.
     */
    public Channel() {
    }

    /**
     * @param delegate The object from the XML parser.
     */
    public Channel(org.eclipse.smarthome.tools.docgenerator.schemas.Channel delegate) {
        this.delegate = delegate;
    }

    /**
     * @param delegate The object from the XML parser.
     */
    public void setChannelType(org.eclipse.smarthome.tools.docgenerator.schemas.ChannelType typeDelegate) {
        this.typeDelegate = typeDelegate;
    }

    /**
     * @return Returns the {@link Channel} instance.
     */
    @Override
    public org.eclipse.smarthome.tools.docgenerator.schemas.Channel getRealImpl() {
        return delegate;
    }

    /**
     * Setter for model.
     *
     * @param channel The real model.
     */
    @Override
    public void setModel(org.eclipse.smarthome.tools.docgenerator.schemas.Channel channel) {
        this.delegate = channel;
    }

    /**
     * @return The id of the channel.
     */
    public String id() {
        return delegate.getId();
    }

    /**
     * @return The id of the channel.
     */
    public String typeId() {
        return delegate.getTypeId();
    }

    /**
     * @return The item type of the channel.
     */
    public String itemType() {
        if (typeDelegate == null) {
            return null;
        }
        return typeDelegate.getItemType();
    }

    /**
     * @return The state of the channel.
     */
    public State state() {
        if (typeDelegate == null) {
            return null;
        }
        return new State(typeDelegate.getState());
    }

    /**
     * @return The description of the channel.
     */
    public String description() {
        if (typeDelegate == null) {
            return null;
        }
        return typeDelegate.getDescription();
    }

    /**
     * @return The label of the channel.
     */
    public String label() {
        if (delegate.getLabel() != null) {
            return delegate.getLabel();
        }
        if (typeDelegate == null) {
            return null;
        }
        return typeDelegate.getLabel();
    }

    /**
     * @return The category of the channel.
     */
    public String category() {
        if (typeDelegate == null) {
            return null;
        }
        return typeDelegate.getCategory();
    }

    /**
     * @return A list of URIs for the configuration.
     */
    public String configDescriptionRef() {
        if (typeDelegate == null) {
            return null;
        }

        if (typeDelegate.getConfigDescriptionRef() != null) {
            return typeDelegate.getConfigDescriptionRef().getUri();
        } else {
            return "";
        }
    }

    /**
     * @return The configuration of the channel.
     */
    public ConfigDescription configDescription() {
        if (typeDelegate == null) {
            return null;
        }

        if (typeDelegate.getConfigDescription() != null) {
            return new ConfigDescription(typeDelegate.getConfigDescription());
        } else {
            return null;
        }
    }
}

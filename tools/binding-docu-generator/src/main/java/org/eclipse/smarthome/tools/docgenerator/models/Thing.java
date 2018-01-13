package org.eclipse.smarthome.tools.docgenerator.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.tools.docgenerator.schemas.Property;
import org.eclipse.smarthome.tools.docgenerator.schemas.ThingType;

/**
 * Wrapper class to not fully depend on the existing models.
 */
public class Thing implements Model<ThingType> {
    /**
     * The instance from the XML parser.
     */
    private ThingType delegate;

    private List<Channel> channels = null;

    /**
     * Default constructor.
     */
    public Thing() {
    }

    /**
     * Constructor.
     *
     * @param delegate Original instance from the XML parser.
     */
    public Thing(ThingType delegate) {
        this.delegate = delegate;
    }

    /**
     * @return The instance from the XML parser.
     */
    @Override
    public ThingType getRealImpl() {
        return delegate;
    }

    /**
     * Set the model.
     *
     * @param type Original instance from the XML parser.
     */
    @Override
    public void setModel(ThingType type) {
        this.delegate = type;
    }

    /**
     * @return Id of the thing.
     */
    public String id() {
        return delegate.getId();
    }

    /**
     * @return Label of the thing.
     */
    public String label() {
        return delegate.getLabel();
    }

    /**
     * @return Description of the thing.
     */
    public String description() {
        return delegate.getDescription();
    }

    /**
     * @return the thing category
     */
    public String category() {
        return delegate.getCategory();
    }

    /**
     * @return Configuration reference of the thing.
     */
    public String configDescriptionRef() {
        if (delegate.getConfigDescriptionRef() != null) {
            return delegate.getConfigDescriptionRef().getUri();
        } else {
            return "";
        }
    }

    /**
     * @return A list of channels.
     */
    public List<Channel> channels() {
        if (this.channels == null) {
            List<Channel> channels = new ArrayList<Channel>();
            if (delegate.getChannels() != null) {
                for (org.eclipse.smarthome.tools.docgenerator.schemas.Channel channel : delegate.getChannels()
                        .getChannel()) {
                    Channel newChannel = new Channel(channel);
                    channels.add(newChannel);
                }
            }
            this.channels = channels;
        }
        return this.channels;
    }

    /**
     * @return A list of channel groups.
     */
    public List<org.eclipse.smarthome.tools.docgenerator.schemas.ChannelGroup> channelGroups() {
        List<org.eclipse.smarthome.tools.docgenerator.schemas.ChannelGroup> channels = new ArrayList<org.eclipse.smarthome.tools.docgenerator.schemas.ChannelGroup>();
        if (delegate.getChannelGroups() != null) {
            for (org.eclipse.smarthome.tools.docgenerator.schemas.ChannelGroup group : delegate.getChannelGroups()
                    .getChannelGroup()) {
                channels.add(group);
            }
        }
        return channels;
    }

    /**
     * @return The configuration for the thing.
     */
    public ConfigDescription configDescription() {
        if (delegate.getConfigDescription() != null) {
            return new ConfigDescription(delegate.getConfigDescription());
        } else {
            return null;
        }
    }

    public Map<String, String> property() {
        if (delegate.getProperties() == null) {
            return null;
        }
        org.eclipse.smarthome.tools.docgenerator.schemas.Properties properties = delegate.getProperties();

        Map<String, String> propertiesMap = new HashMap<String, String>();
        List<Property> propertyList = properties.getProperty();
        for (Property property : propertyList) {
            propertiesMap.put(property.getName(), property.getValue());
        }

        return propertiesMap;
    }

}

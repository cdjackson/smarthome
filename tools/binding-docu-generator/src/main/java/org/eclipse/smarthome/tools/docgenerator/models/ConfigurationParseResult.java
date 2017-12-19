package org.eclipse.smarthome.tools.docgenerator.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smarthome.tools.docgenerator.data.BridgeList;
import org.eclipse.smarthome.tools.docgenerator.data.ChannelGroupList;
import org.eclipse.smarthome.tools.docgenerator.data.ConfigurationList;
import org.eclipse.smarthome.tools.docgenerator.schemas.BridgeType;
import org.eclipse.smarthome.tools.docgenerator.schemas.ChannelGroupType;
import org.eclipse.smarthome.tools.docgenerator.schemas.ChannelType;

public class ConfigurationParseResult {

    private final ArrayList<ChannelType> channelTypes;
    private final List<Thing> things;
    private final ChannelGroupList channelGroups;
    private final BridgeList bridges;
    private final ConfigurationList configList;
    private final Binding binding;

    public ConfigurationParseResult() {
        channelTypes = new ArrayList<ChannelType>();
        things = new ArrayList<Thing>();
        channelGroups = new ChannelGroupList();
        bridges = new BridgeList();
        configList = new ConfigurationList();
        binding = new Binding();
    }

    /**
     * Adds the channel type to the channel list.
     *
     * @param channelType the channel type
     */
    public void putChannelType(ChannelType channelType) {
        channelTypes.add(channelType);
    }

    /**
     * Adds the thing type to the thing list.
     *
     * @param thingType the thing type
     */
    public void putThing(Thing thingType) {
        things.add(thingType);
    }

    /**
     * Adds the channel group type to the channel group list.
     *
     * @param channelGroupType the channel group type
     */
    public void putChannelGroup(ChannelGroupType channelGroupType) {
        channelGroups.put(channelGroupType);
    }

    /**
     * Adds the bridge type to the bridge list.
     *
     * @param bridgeType the bridge type
     */
    public void putBridge(BridgeType bridgeType) {
        bridges.put(bridgeType);
    }

    /**
     * Adds the config description to the config list.
     *
     * @param configDescription the config description
     */
    public void putConfigDescription(
            org.eclipse.smarthome.tools.docgenerator.schemas.ConfigDescription configDescription) {
        configList.put(configDescription);
    }

    /**
     * Sets the binding definition to the result.
     *
     * @param binding the binding object
     */
    public void setBinding(org.eclipse.smarthome.tools.docgenerator.schemas.Binding binding) {
        this.binding.setModel(binding);
    }

    public List<ChannelType> getChannels() {
        return channelTypes;
    }

    public List<Thing> getThings() {
        return things;
    }

    public ChannelGroupList getChannelGroups() {
        return channelGroups;
    }

    public BridgeList getBridges() {
        return bridges;
    }

    public ConfigurationList getConfigList() {
        return configList;
    }

    public Binding getBinding() {
        return binding;
    }
}

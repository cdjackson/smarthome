package org.eclipse.smarthome.tools.docgenerator.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.smarthome.tools.docgenerator.EshConfigurationParser;
import org.eclipse.smarthome.tools.docgenerator.ParserException;
import org.eclipse.smarthome.tools.docgenerator.data.BridgeList;
import org.eclipse.smarthome.tools.docgenerator.data.ChannelGroupList;
import org.eclipse.smarthome.tools.docgenerator.data.ConfigurationList;
import org.eclipse.smarthome.tools.docgenerator.models.Binding;
import org.eclipse.smarthome.tools.docgenerator.models.Bridge;
import org.eclipse.smarthome.tools.docgenerator.models.ConfigurationParseResult;
import org.eclipse.smarthome.tools.docgenerator.models.Thing;
import org.junit.Test;

public class DefaultEshConfigurationParserTest {
    @Test
    public void testParseEshConfiguration() throws ParserException {
        Path eshPath = Paths.get("src/test/resources/testESH-INF/");

        EshConfigurationParser parser = new DefaultEshConfigurationParser(mock(Log.class));

        ConfigurationParseResult result = parser.parseEshConfiguration(eshPath);

        assertNotNull(result);
        assertNotNull(result.getBinding());
        assertNotNull(result.getBridges());
        assertNotNull(result.getChannelGroups());
        assertNotNull(result.getChannels());
        assertNotNull(result.getConfigList());
        assertNotNull(result.getThings());

        Binding binding = result.getBinding();
        assertThat(binding.id(), is("binding-1"));
        assertThat(binding.description(), is("This is the binding number one"));
        assertThat(binding.author(), is("Author #1"));
        assertThat(binding.name(), is("Binding #1"));

        BridgeList bridgeList = result.getBridges();
        assertThat(bridgeList.size(), is(1));
        assertThat(bridgeList.get(0).getModel(), instanceOf(Bridge.class));
        Bridge bridge = (Bridge) bridgeList.get(0).getModel();
        assertThat(bridge.label(), is("Bridge #1"));
        assertThat(bridge.description(), is("The bridge number one."));
        assertThat(bridge.id(), is("bridge"));

        ChannelGroupList channelGroupList = result.getChannelGroups();
        assertThat(channelGroupList.size(), is(0));

        List<org.eclipse.smarthome.tools.docgenerator.schemas.ChannelType> channelList = result.getChannels();
        assertThat(channelList.size(), is(18));
        assertThat(channelList.get(0), instanceOf(org.eclipse.smarthome.tools.docgenerator.schemas.ChannelType.class));
        org.eclipse.smarthome.tools.docgenerator.schemas.ChannelType channel = channelList.get(0);
        assertThat(channel.getId(), is("roomSetpoint"));
        // assertThat(channel.label(), is("Room setpoint"));
        // assertThat(channel.description(), is("The room temperature setpoint."));
        // assertThat(channel.category(), is("Temperature"));
        // assertThat(channel.itemType(), is("Number"));
        // assertFalse(channel.state().readOnly());

        ConfigurationList configurationList = result.getConfigList();
        assertThat(configurationList.size(), is(0));

        List<Thing> thingList = result.getThings();
        assertThat(thingList.size(), is(2));
        assertThat(thingList.get(0), instanceOf(Thing.class));
        Thing thing = thingList.get(0);
        assertThat(thing.id(), is("thermostat"));
        assertThat(thing.label(), is("Thermostat"));
        assertThat(thing.description(), is("An thermostat."));
        assertThat(thing.channels().size(), is(16));
    }
}

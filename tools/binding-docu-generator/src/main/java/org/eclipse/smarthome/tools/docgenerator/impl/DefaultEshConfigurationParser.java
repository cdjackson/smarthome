package org.eclipse.smarthome.tools.docgenerator.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.smarthome.tools.docgenerator.EshConfigurationParser;
import org.eclipse.smarthome.tools.docgenerator.ParserException;
import org.eclipse.smarthome.tools.docgenerator.models.ConfigurationParseResult;
import org.eclipse.smarthome.tools.docgenerator.models.Thing;
import org.eclipse.smarthome.tools.docgenerator.schemas.Binding;
import org.eclipse.smarthome.tools.docgenerator.schemas.BridgeType;
import org.eclipse.smarthome.tools.docgenerator.schemas.ChannelGroupType;
import org.eclipse.smarthome.tools.docgenerator.schemas.ChannelType;
import org.eclipse.smarthome.tools.docgenerator.schemas.ConfigDescription;
import org.eclipse.smarthome.tools.docgenerator.schemas.ConfigDescriptions;
import org.eclipse.smarthome.tools.docgenerator.schemas.ThingDescriptions;
import org.eclipse.smarthome.tools.docgenerator.schemas.ThingType;
import org.eclipse.smarthome.tools.docgenerator.util.XmlUtils;

/**
 * Default implementation for a {@link EshConfigurationParser}.
 */
public class DefaultEshConfigurationParser implements EshConfigurationParser {
    private static final String THING_SUBDIR = "thing/";
    private static final String BINDING_SUBDIR = "binding/";
    private static final String CONFIG_SUBDIR = "config/";

    private final Log logger;

    /**
     * Constructor.
     *
     * @param logger Logger to use.
     */
    public DefaultEshConfigurationParser(Log logger) {
        this.logger = logger;
    }

    @Override
    public ConfigurationParseResult parseEshConfiguration(Path eshConfigurationPath) throws ParserException {
        final ConfigurationParseResult result = new ConfigurationParseResult();

        Path bindingDirectory = eshConfigurationPath.resolve(BINDING_SUBDIR);
        // Scan the binding directory.
        Path binding = bindingDirectory.resolve("binding.xml");
        if (!Files.exists(binding)) {
            throw new ParserException("File '" + binding.toAbsolutePath() + "' not found");
        }

        logger.debug("Found binding xml: " + binding);
        parseBindingDescription(binding, result);

        // Scan the things directory.
        Path thingsDirectory = eshConfigurationPath.resolve(THING_SUBDIR);
        XmlUtils.handleXmlFiles(thingsDirectory.toFile(), new XmlUtils.Consumer<File>() {
            @Override
            public void accept(File thingFile) {
                logger.info("Processing " + thingFile.getName());
                DefaultEshConfigurationParser.this.parseThingDescriptions(thingFile.toPath(), result);
            }
        });

        // Scan the config directory.
        Path configsDirectory = eshConfigurationPath.resolve(CONFIG_SUBDIR);
        XmlUtils.handleXmlFiles(configsDirectory.toFile(), new XmlUtils.Consumer<File>() {
            @Override
            public void accept(File configFile) {
                logger.info("Processing " + configFile.getName());
                DefaultEshConfigurationParser.this.parseConfigDescriptions(configFile, result);
            }
        });

        // Do some consolidation
        for (Thing thingType : result.getThings()) {
            for (org.eclipse.smarthome.tools.docgenerator.models.Channel channel : thingType.channels()) {
                boolean set = false;
                for (ChannelType channelType : result.getChannels()) {
                    if (channelType.getId().equals(channel.typeId())) {
                        channel.setChannelType(channelType);
                        set = true;
                        break;
                    }
                }
                if (!set) {
                    System.out.println("Not set channel type " + channel.id());
                }
            }
        }

        return result;
    }

    /**
     * Parses the xml with the available channels.
     *
     * @param file XML file.
     */
    private void parseThingDescriptions(Path file, ConfigurationParseResult result) {

        ThingDescriptions thingDesc = XmlUtils.convertXmlToObject(file.toFile(), ThingDescriptions.class);

        // Go through all the available types
        List<Object> objs = thingDesc.getThingTypeOrBridgeTypeOrChannelType();
        for (Object obj : objs) {
            if (obj instanceof ChannelType) {
                result.putChannelType((ChannelType) obj);
            } else if (obj instanceof BridgeType) {
                result.putBridge((BridgeType) obj);
            } else if (obj instanceof ChannelGroupType) {
                result.putChannelGroup((ChannelGroupType) obj);
            } else if (obj instanceof ThingType) {
                result.putThing(new Thing((ThingType) obj));
            } else {
                logger.warn("Unsupported class. " + obj.getClass().toString());
            }
        }
    }

    /**
     * Parses the xml with the available configuration.
     *
     * @param file XML file.
     */
    private void parseConfigDescriptions(File file, ConfigurationParseResult result) {
        try {
            ConfigDescriptions configDesc = XmlUtils.convertXmlToObject(file, ConfigDescriptions.class);

            for (ConfigDescription c : configDesc.getConfigDescription()) {
                result.putConfigDescription(c);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * Parses the xml with the available binding.
     *
     * @param file XML file.
     */
    private void parseBindingDescription(Path file, ConfigurationParseResult result) {
        try {
            Binding bindingDescription = XmlUtils.convertXmlToObject(file.toFile(), Binding.class);
            result.setBinding(bindingDescription);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}

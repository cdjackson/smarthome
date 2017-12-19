package org.eclipse.smarthome.tools.docgenerator;

import java.nio.file.Path;

import org.eclipse.smarthome.tools.docgenerator.models.ConfigurationParseResult;

/**
 * Parses an ESH configuration.
 */
public interface EshConfigurationParser {
    /**
     * Parse all ESH XML files that can be found into a ConfigurationParseResult object
     * that can later be used to generate documentation.
     *
     * @param eshConfigurationPath the path to the ESH-INF directory
     * @return Parsed ESH configuration.
     */
    ConfigurationParseResult parseEshConfiguration(Path eshConfigurationPath) throws ParserException;
}

package org.eclipse.smarthome.tools.docgenerator;

import java.nio.file.Paths;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.eclipse.smarthome.tools.docgenerator.impl.DefaultEshConfigurationParser;
import org.eclipse.smarthome.tools.docgenerator.impl.HandlebarsDocumentationGenerator;
import org.eclipse.smarthome.tools.docgenerator.models.ConfigurationParseResult;
import org.eclipse.smarthome.tools.docgenerator.models.Thing;

public class MainForTest {
    private final static Log logger = new SystemStreamLog();

    /**
     * The directory in which your binding xml files are.
     */
    // private static String eshDir = "/Users/chris/Development/openHAB-2/git/org.openhab.binding.zigbee/ESH-INF";
    private static String eshDir = "/Users/chris/Development/openHAB-2/git/org.openhab.binding.zwave/ESH-INF";

    /**
     * Directory which contains the partials.
     */
    private static String partialsDir = "${basedir}/doc/template/template";

    /**
     * Name of your readme template file.
     */
    private static String templateReadme = "/Users/chris/Development/openHAB-2/git/org.openhab.binding.zwave/doc-template/README.md.mustache";
    private static String templateThings = "/Users/chris/Development/openHAB-2/git/org.openhab.binding.zwave/doc-template/things.md.mustache";
    private static String templateThing = "/Users/chris/Development/openHAB-2/git/org.openhab.binding.zwave/doc-template/things-template.md.mustache";

    /**
     * The name of the generated docu file.
     */
    private static String outputFile = "/Users/chris/Development/openHAB-2/git/org.openhab.binding.zwave";

    public static void main(String[] args) {
        ConfigurationParseResult eshConfiguration;
        try {
            EshConfigurationParser configurationParser = new DefaultEshConfigurationParser(logger);
            DocumentationGenerator generator = new HandlebarsDocumentationGenerator(logger);

            eshConfiguration = configurationParser.parseEshConfiguration(Paths.get(eshDir));

            generator.generateDocumentation(eshConfiguration, Paths.get(outputFile + "/README.md"),
                    Paths.get(partialsDir), Paths.get(templateReadme), null);

            generator.generateDocumentation(eshConfiguration, Paths.get(outputFile + "/doc/things.md"),
                    Paths.get(partialsDir), Paths.get(templateThings), null);

            for (Thing thing : eshConfiguration.getThings()) {
                System.out.println("Exporting thing " + thing.id());

                generator.generateDocumentation(eshConfiguration, Paths.get(outputFile + "/doc/" + thing.id() + ".md"),
                        Paths.get(partialsDir), Paths.get(templateThing), thing);
            }
        } catch (ParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

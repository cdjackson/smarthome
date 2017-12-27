package org.eclipse.smarthome.tools.docgenerator.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.smarthome.tools.docgenerator.DocumentationGenerator;
import org.eclipse.smarthome.tools.docgenerator.GeneratorException;
import org.eclipse.smarthome.tools.docgenerator.models.ConfigurationParseResult;
import org.eclipse.smarthome.tools.docgenerator.models.Thing;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.common.base.Charsets;
import com.overzealous.remark.Remark;

/**
 * Documentation Generator using Mustache templates.
 */
public class HandlebarsDocumentationGenerator implements DocumentationGenerator {
    /**
     * Logger.
     */
    private final Log logger;

    /**
     * Create a Mustache documentation generator using the given logger and templates directory.
     *
     * @param logger the logger
     */
    public HandlebarsDocumentationGenerator(Log logger) {
        this.logger = logger;
    }

    @Override
    public void generateDocumentation(ConfigurationParseResult eshConfiguration, Path outputFile, Path partialsDir,
            Path readmeTemplate, Thing thing) {
        try {
            Context scope = createDataScope(eshConfiguration, thing);
            writeDocumentation(scope, partialsDir, readmeTemplate, outputFile);
        } catch (IOException e) {
            throw new GeneratorException("Could not write README.", e);
        }
    }

    /**
     * Creates the scope for Handlebars.
     *
     * @param configuration Configuration.
     * @return Context for Handlebars.
     */
    private Context createDataScope(ConfigurationParseResult configuration, Thing thing) {
        // Put everything into the scope
        Map<String, Object> scope = new HashMap<>();
        scope.put("binding", configuration.getBinding());
        scope.put("bridgeList", configuration.getBridges());
        scope.put("thingList", configuration.getThings());
        scope.put("channelList", configuration.getChannels());
        scope.put("channelGroupList", configuration.getChannelGroups());
        scope.put("configList", configuration.getConfigList());

        if (thing != null) {
            scope.put("thing", thing);
        }

        return Context.newBuilder(scope)
                .resolver(MapValueResolver.INSTANCE, MethodValueResolver.INSTANCE, FieldValueResolver.INSTANCE).build();
    }

    private void writeDocumentation(Context scope, Path partialsDir, Path readmeTemplate, Path outputFile)
            throws IOException {

        // com.kotcrab.remark
        // String output = template.apply(scope);
        // System.out.println(output);
        // DocumentConverter converter= new DocumentConverter(null);
        // converter.
        final Remark converter = new Remark();

        TemplateLoader loader = new FileTemplateLoader("/");
        loader.setSuffix(null);
        Handlebars handlebars = new Handlebars(loader);

        handlebars.registerHelper("html2md", new Helper<String>() {
            @Override
            public CharSequence apply(String context, Options options) throws IOException {
                if (context == null) {
                    return "";
                }
                String markdown = converter.convert(context);
                if (markdown == null) {
                    markdown = "";
                }
                return new Handlebars.SafeString(markdown);
            }
        });

        handlebars.registerHelper("ellipsis", new Helper<String>() {
            @Override
            public CharSequence apply(String context, Options options) throws IOException {
                if (context == null) {
                    return "";
                }
                String markdown = converter.convert(context);
                if (markdown == null) {
                    markdown = "";
                }
                int lineLength = 85;
                if (options.params.length > 0) {
                    lineLength = (int) options.params[0];
                }
                String[] lines = markdown.split("\n");
                if (lines.length == 0 || lines[0] == null) {
                    return "";
                }
                markdown = lines[0].trim();
                if (markdown.length() > lineLength) {
                    markdown = markdown.substring(0, lineLength) + "...";
                }
                return new Handlebars.SafeString(markdown);
            }
        });

        System.out.println("Opening " + readmeTemplate);
        Template template = handlebars.compile(readmeTemplate.toString());

        // Write README to file
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, Charsets.UTF_8)) {
            writer.write(template.apply(scope));
        }
    }

}

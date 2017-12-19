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
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.common.base.Charsets;

/**
 * Documentation Generator using Mustache templates.
 */
public class MustacheDocumentationGenerator implements DocumentationGenerator {
    /**
     * Logger.
     */
    private final Log logger;

    /**
     * Create a Mustache documentation generator using the given logger and templates directory.
     *
     * @param logger the logger
     */
    public MustacheDocumentationGenerator(Log logger) {
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
        // Compile mustache template
        // MustacheFactory mf = new DefaultMustacheFactory(new OverridableMustacheResolver(logger, partialsDir,
        // readmeTemplate));
        // Mustache mustache = mf.compile(OverridableMustacheResolver.README_NAME);

        TemplateLoader loader = new FileTemplateLoader("/");
        // loader.setPrefix("/templates/partials");
        // loader.setSuffix(".mustache");
        loader.setSuffix(null);
        Handlebars handlebars = new Handlebars(loader);

        System.out.println("Opening " + readmeTemplate);
        // Template template = handlebars.compile("/README.md.mustache");
        Template template = handlebars.compile(readmeTemplate.toString());

        // com.kotcrab.remark
        // String output = template.apply(scope);
        // System.out.println(output);

        // Write README to file
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, Charsets.UTF_8)) {
            writer.write(template.apply(scope));
        }
    }

    /**
     * A mustache resolver which has the following resolution strategy:
     *
     * If the template name is 'readme', use the given readmeTemplate. If the readmeTemplate doesn't exist as a file,
     * README.md.mustache is loaded from classpath.
     *
     * For every other template name, first try file with that name in the partialsDir. If the file doesn't exist,
     * it is loaded from classpath.
     *
     */
    /*
     * private static class OverridableMustacheResolver implements MustacheResolver {
     * public static final String README_NAME = "readme";
     * public static final String README_TEMPLATE_NAME = "README.md.mustache";
     * public static final String TEMPLATE_PREFIX = "/templates/";
     *
     * private final Log logger;
     * private final Path partialsDir;
     * private final Path readmeTemplate;
     *
     * public OverridableMustacheResolver(Log logger, Path partialsDir, Path readmeTemplate) {
     * this.logger = logger;
     * this.partialsDir = partialsDir;
     * this.readmeTemplate = readmeTemplate;
     * }
     *
     * @Override
     * public Reader getReader(String resourceName) {
     * try {
     * if (resourceName.equals(README_NAME)) {
     * return getReadmeTemplate(resourceName);
     * } else {
     * return getPartialTemplate(resourceName);
     * }
     * } catch (IOException e) {
     * throw new RuntimeException(e);
     * }
     * }
     *
     * private Reader getPartialTemplate(String resourceName) throws IOException {
     * String template = resourceName + ".mustache";
     * Path path = partialsDir.resolve(template);
     *
     * if (Files.exists(path)) {
     * logger.debug(resourceName + " -> file:" + path.toAbsolutePath());
     * return Files.newBufferedReader(path, Charsets.UTF_8);
     * } else {
     * logger.debug(resourceName + " -> classpath:" + TEMPLATE_PREFIX + template);
     * return findInClasspath("/templates/" + template);
     * }
     * }
     *
     * private Reader getReadmeTemplate(String resourceName) throws IOException {
     * if (Files.exists(readmeTemplate)) {
     * logger.debug(resourceName + " -> file:" + readmeTemplate.toAbsolutePath());
     * return Files.newBufferedReader(readmeTemplate, Charsets.UTF_8);
     * } else {
     * logger.debug(resourceName + " -> classpath:/templates/" + README_TEMPLATE_NAME);
     * return findInClasspath("/templates/" + README_TEMPLATE_NAME);
     * }
     * }
     *
     * private InputStreamReader findInClasspath(String name) throws IOException {
     * InputStream resourceAsStream = MustacheDocumentationGenerator.class.getResourceAsStream(name);
     * if (resourceAsStream == null) {
     * throw new IOException("Template '" + name + "' not found in classpath");
     * }
     *
     * return new InputStreamReader(resourceAsStream, Charsets.UTF_8);
     * }
     * }
     */
}

# binding-docu-generator Maven Plugin

Large parts of the documentation for a Eclipse SmartHome binding can be generated automatically from the XML files. This is done via this Maven plugin.

## Using the plugin

Execute `mvn org.eclipse.smarthome.tools:binding-docu-generator:$VERSION:generate-docu`. This will generate a file named README.md in the directory of your binding.

You can take a look at a generated readme [here](README-example.md).

## Customizing the README

If you want to customize the generated readme, create a file named `README.md.mustache` in the directory of your binding. This template is using the handlebars template language. You can use the following partials to include generated documentation:

* `{{binding.name}}` - Name of the binding

* `{{binding.description}}` - Description of the binding

* `{{>partials/bridges}}` - Bridges from the ESH XML

* `{{>partials/bridgeConfig}}` - Bridge configuration from the ESH XML

* `{{>partials/things}}` - Things from the ESH XML

* `{{>partials/thingConfig}}` - Thing config from the ESH XML

* `{{>partials/channelGroups}}` - Channel groups from the ESH XML

* `{{>partials/channels}}` - Channels from the ESH XML

## Mustache Helpers

* `{{html2md context}}` - Converts any HTML in context into markdown
* `{{ellipsis context length}}` - Takes the first line of context only. If the line is longer than the optional `length` bytes then an ellipsis will be added (ie ...) to the end of the line.

## Original author

* [Alexander Kammerer](https://github.com/kummerer94)

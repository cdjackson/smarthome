/**
 * Copyright (c) 2015 Deutsche Telekom AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.core.thing.binding

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

import org.eclipse.smarthome.config.core.ConfigDescription
import org.eclipse.smarthome.config.core.ConfigDescriptionParameter
import org.eclipse.smarthome.config.core.ConfigDescriptionParameterBuilder
import org.eclipse.smarthome.config.core.ConfigDescriptionProvider
import org.eclipse.smarthome.config.core.ConfigDescriptionRegistry
import org.eclipse.smarthome.config.core.Configuration
import org.eclipse.smarthome.core.thing.ChannelUID
import org.eclipse.smarthome.core.thing.ManagedThingProvider
import org.eclipse.smarthome.core.thing.Thing
import org.eclipse.smarthome.core.thing.ThingStatus
import org.eclipse.smarthome.core.thing.ThingTypeUID
import org.eclipse.smarthome.core.thing.ThingUID
import org.eclipse.smarthome.core.thing.type.ChannelDefinition
import org.eclipse.smarthome.core.thing.type.ChannelGroupType
import org.eclipse.smarthome.core.thing.type.ChannelGroupTypeUID
import org.eclipse.smarthome.core.thing.type.ChannelType
import org.eclipse.smarthome.core.thing.type.ChannelTypeProvider
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID
import org.eclipse.smarthome.core.thing.type.ThingType
import org.eclipse.smarthome.core.thing.type.ThingTypeRegistry
import org.eclipse.smarthome.core.types.Command
import org.eclipse.smarthome.test.OSGiTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.osgi.service.component.ComponentContext

/**
 * @author Simon Kaufmann - initial contribution
 */
class ChangeThingTypeOSGiTest extends OSGiTest {


    def ManagedThingProvider managedThingProvider
    def ThingHandlerFactory thingHandlerFactory

    final static String BINDING_ID = "testBinding"
    final static String THING_TYPE_GENERIC_ID = "generic"
    final static String THING_TYPE_SPECIFIC_ID = "specific"
    final static ThingTypeUID THING_TYPE_GENERIC_UID = new ThingTypeUID(BINDING_ID, THING_TYPE_GENERIC_ID)
    final static ThingTypeUID THING_TYPE_SPECIFIC_UID = new ThingTypeUID(BINDING_ID, THING_TYPE_SPECIFIC_ID)
    final static String THING_ID = "testThing"

    def Map<ThingTypeUID, ThingType> thingTypes = new HashMap<>()
    def Map<URI, ConfigDescription> configDescriptions = new HashMap<>()
    def Map<ChannelTypeUID, ChannelType> channelTypes = new HashMap<>()
    def Map<ChannelGroupTypeUID, ChannelGroupType> channelGroupTypes = new HashMap<>()
    def ComponentContext componentContext
    def ConfigDescriptionRegistry configDescriptionRegistry

    def ThingType thingTypeGeneric
    def ThingType thingTypeSpecific


    @Before
    void setup() {
        registerVolatileStorageService()
        managedThingProvider = getService(ManagedThingProvider)
        assertThat managedThingProvider, is(notNullValue())

        configDescriptionRegistry = getService(ConfigDescriptionRegistry)
        assertThat configDescriptionRegistry, is(notNullValue())

        componentContext = [getBundleContext: { bundleContext }] as ComponentContext
        thingHandlerFactory = new SampleThingHandlerFactory()
        thingHandlerFactory.activate(componentContext)
        registerService(thingHandlerFactory, ThingHandlerFactory.class.name)

        thingTypeGeneric = registerThingTypeAndConfigDescription(THING_TYPE_GENERIC_UID)
        thingTypeSpecific = registerThingTypeAndConfigDescription(THING_TYPE_SPECIFIC_UID)

        registerService([
            getThingType: { aThingTypeUID,locale ->
                return thingTypes.get(aThingTypeUID)
            }
        ] as ThingTypeProvider)

        registerService([
            getThingType:{ aThingTypeUID ->
                return thingTypes.get(aThingTypeUID)
            }
        ] as ThingTypeRegistry)

        registerService([
            getConfigDescription: { uri, locale ->
                return configDescriptions.get(uri)
            }
        ] as ConfigDescriptionProvider)

        registerService([
            getChannelTypes: { channelTypes.values() },
            getChannelType: { ChannelTypeUID uid, Locale locale ->
                channelTypes.get(uid)
            },
            getChannelGroupTypes: { channelGroupTypes.values() },
            getChannelGroupType: { ChannelGroupTypeUID uid, Locale locale ->
                channelGroupTypes.get(uid)
            },
        ] as ChannelTypeProvider)
    }

    @After
    void teardown() {
        unregisterService(ThingHandlerFactory.class.name)
        unregisterService(ThingTypeProvider)
        unregisterService(ThingTypeRegistry)
        unregisterService(ConfigDescriptionProvider)
        unregisterService(ChannelTypeProvider)
        thingHandlerFactory.deactivate(componentContext)

        managedThingProvider.getAll().each {
            managedThingProvider.remove(it.getUID())
        }
    }

    class SampleThingHandlerFactory extends BaseThingHandlerFactory {

        @Override
        public boolean supportsThingType(ThingTypeUID thingTypeUID) {
            true
        }

        @Override
        protected ThingHandler createHandler(Thing thing) {
            if (thing.getThingTypeUID().equals(THING_TYPE_GENERIC_UID)) {
                return new GenericThingHandler(thing)
            }
            if (thing.getThingTypeUID().equals(THING_TYPE_SPECIFIC_UID)) {
                return new SpecificThingHandler(thing)
            }
        }
    }

    class GenericThingHandler extends BaseThingHandler {

        GenericThingHandler(Thing thing) {
            super(thing)
        }

        @Override
        public void handleCommand(ChannelUID channelUID, Command command) {
            println "Generic Handle Command"
        }
    }

    class SpecificThingHandler extends BaseThingHandler {

        SpecificThingHandler(Thing thing) {
            super(thing)
        }

        @Override
        public void handleCommand(ChannelUID channelUID, Command command) {
            println "Specific Handle Command"
        }
    }

    @Test
    void 'assert TBD'() {
        def thing = ThingFactory.createThing(thingTypeGeneric, new ThingUID("testBinding", "testThing"), new Configuration(), null, configDescriptionRegistry)
        thing.setProperty("universal", "survives")
        managedThingProvider.add(thing)

        // Pre-flight checks - see below
        assertThat thing.getHandler(), isA(GenericThingHandler)
        assertThat thing.getConfiguration().get("parametergeneric"), is("defaultgeneric")
        assertThat thing.getChannels().size(), is(1)
        assertThat thing.getChannels().get(0).getUID().getId(), containsString("generic")
        assertThat thing.getProperties().get("universal"), is("survives")
        def handlerOsgiService = getService(ThingHandler, {
            it.getProperty(ThingHandler.SERVICE_PROPERTY_THING_ID).toString() == "testBinding::testThing"
        })
        assertThat handlerOsgiService, is(thing.getHandler())
        thing.getHandler().handleCommand(null, null)
        assertThat thing.getStatus(), is(ThingStatus.ONLINE)

        // Now do the actual migration
        thing.getHandler().changeThingType(THING_TYPE_SPECIFIC_UID, new Configuration(['providedspecific':'there']))

        // Ensure that the provided configuration has been applied and default values have been added
        assertThat thing.getConfiguration().get("parameterspecific"), is("defaultspecific")
        assertThat thing.getConfiguration().get("parametergeneric"), is(nullValue())
        assertThat thing.getConfiguration().get("providedspecific"), is("there")

        // Ensure that the new set of channels is there
        assertThat thing.getChannels().size(), is(1)
        assertThat thing.getChannels().get(0).getUID().getId(), containsString("specific")

        // Ensure that the properties are still there
        assertThat thing.getProperties().get("universal"), is("survives")

        // Ensure that the ThingHandler has been registeres as an OSGi service correctly
        assertThat thing.getHandler(), isA(SpecificThingHandler)
        def handlerOsgiService2 = getService(ThingHandler, {
            it.getProperty(ThingHandler.SERVICE_PROPERTY_THING_ID).toString() == "testBinding::testThing"
        })
        assertThat handlerOsgiService2, is(thing.getHandler())
        thing.getHandler().handleCommand(null, null)

        // Ensure the Thing is ONLINE again
        assertThat thing.getStatus(), is(ThingStatus.ONLINE)

    }

    private ThingType registerThingTypeAndConfigDescription(ThingTypeUID thingTypeUID) {
        def URI configDescriptionUri = new URI("test:" + thingTypeUID.getId());
        def thingType = new ThingType(thingTypeUID, null, "label", null, getChannelDefinitions(thingTypeUID), null, null, configDescriptionUri)
        def configDescription = new ConfigDescription(configDescriptionUri, [
            ConfigDescriptionParameterBuilder.create("parameter"+thingTypeUID.getId(), ConfigDescriptionParameter.Type.TEXT).withRequired(true).withDefault("default"+thingTypeUID.getId()).build(),
            ConfigDescriptionParameterBuilder.create("provided"+thingTypeUID.getId(), ConfigDescriptionParameter.Type.TEXT).withRequired(true).build()
        ] as List);

        thingTypes.put(thingTypeUID, thingType)
        configDescriptions.put(configDescriptionUri, configDescription)

        return thingType
    }

    private List getChannelDefinitions(ThingTypeUID thingTypeUID){
        List channelDefinitions = new ArrayList<ChannelDefinition>()
        def channelTypeUID = new ChannelTypeUID("test:"+thingTypeUID.getId())
        def channelType = new ChannelType(channelTypeUID, false, "itemType", "channelLabel", "description", "category", new HashSet<String>(), null, new URI("scheme", "channelType:"+thingTypeUID.getId(), null))

        channelTypes.put(channelTypeUID, channelType)

        def cd = new ChannelDefinition("channel"+thingTypeUID.getId(), channelTypeUID)
        channelDefinitions.add(cd)
        return channelDefinitions;
    }

}

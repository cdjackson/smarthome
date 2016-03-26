/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus;

import java.util.Map;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.Variant;

@DBusInterfaceName("org.bluez.MediaEndpoint1")
public interface MediaEndpoint1 extends DBusInterface {

    /**
     * Sets the configuration for the transport.
     *
     * @param transport
     * @param properties
     */
    public void SetConfiguration(DBusInterface path, Map<String, Variant> properties);

    /**
     * Select the preferable configuration from the supported capabilities.
     *
     * @param transport
     * @param capabilities
     * @return Returns a configuration which can be used to setup a transport.
     */
    public Map<String, Variant> SelectConfiguration(Map<String, Variant> capabilities);

    /**
     * Clears the transport configuration.
     *
     * @param transport
     */
    public void ClearConfiguration(DBusInterface path);

    /**
     * This method gets called when the service daemon unregisters the endpoint. An endpoint can use it to do cleanup
     * tasks. There is no need to unregister the endpoint, because when this method gets called it has already been
     * unregistered.
     */
    public void Release();

}

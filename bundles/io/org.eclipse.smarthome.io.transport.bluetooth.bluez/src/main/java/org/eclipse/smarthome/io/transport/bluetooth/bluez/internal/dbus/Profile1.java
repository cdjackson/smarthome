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

@DBusInterfaceName("org.bluez.Profile1")
public interface Profile1 extends DBusInterface {

    /**
     * This method gets called when a new service level connection has been made and authorized.
     *
     * @param device
     * @param fd
     * @param properties
     */
    public void NewConnection(Device1 device, int fd, Map<String, Variant> properties);

    /**
     * This method gets called when a profile gets disconnected.
     *
     * The file descriptor is no longer owned by the service daemon and the profile implementation needs to take care of
     * cleaning up all connections.
     *
     * If multiple file descriptors are indicated via NewConnection, it is expected that all of them are disconnected
     * before returning from this method call..
     *
     * @param device
     */
    public void RequestDisconnection(Device1 device);

    /**
     * This method gets called when the service daemon unregisters the profile. A profile can use it to do cleanup
     * tasks. There is no need to unregister the profile, because when this method gets called it has already been
     * unregistered.
     */
    public void Release();

}

/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus;

import org.freedesktop.DBus.Binding.Triplet;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.UnixFD;

@DBusInterfaceName("org.bluez.MediaTransport1")
public interface MediaTransport1 extends DBusInterface {

    /**
     * Acquire transport file descriptor and the MTU for read and write respectively.
     *
     * @return
     */
    public Triplet<UnixFD, UInt16, UInt16> Acquire();

    /**
     * Acquire transport file descriptor only if the transport is in "pending" state at the time the message is received
     * by BlueZ. Otherwise no request will be sent to the remote device and the function will just fail with
     * org.bluez.Error.NotAvailable.
     *
     * @return
     */
    public Triplet<UnixFD, UInt16, UInt16> TryAcquire();

    public void Release();

}

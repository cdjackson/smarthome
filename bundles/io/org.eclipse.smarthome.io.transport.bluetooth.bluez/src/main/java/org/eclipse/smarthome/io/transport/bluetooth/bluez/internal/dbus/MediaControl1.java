/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;

@DBusInterfaceName("org.bluez.MediaControl1")
public interface MediaControl1 extends DBusInterface {

    public void Play();

    public void Pause();

    public void Stop();

    public void Next();

    public void Previous();

    public void FastForward();

    public void Rewind();

    public void VolumeUp();

    public void VolumeDown();
}

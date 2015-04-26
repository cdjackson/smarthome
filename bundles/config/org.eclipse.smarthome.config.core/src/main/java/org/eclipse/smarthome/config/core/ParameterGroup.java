/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.config.core;

/**
 * The {@link ParameterGroup} specifies information about parameter groups.
 * A parameter group is used to group a number of parameters together so they can
 * be displayed together in the UI (eg in a single tab).
 * <p>
 * A {@link ConfigDescriptionParameter} instance must also contain the group name. It should be permissible to use the
 * group name in the {@link ConfigDesctiptionParameter} without supplying a corresponding {@link ParameterGroup} - in
 * this way the UI can group the parameters together, but doesn't have the group information.
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class ParameterGroup {

    private String name;
    private String context;
    private String label;
    private String description;

    /**
     * Create a Parameter Group. A group is used by the user interface to display groups
     * of parameters together.
     *
     * @param name
     *            the group name, used to link the group, to the parameter
     * @param context
     *            a context string. Can be used to provide some context to the group
     * @param label
     *            the human readable group label
     * @param description
     *            a description that can be provided to the user
     */
    public ParameterGroup(String name, String context, String label, String description) {
        this.name = name;
        this.context = context;
        this.label = label;
        this.description = description;
    }

    /**
     * Get the name of the group.
     *
     * @return group name as string
     */
    public String getName() {
        return name;
    }

    /**
     * Get the context of the group.
     *
     * @return group context as a string
     */
    public String getContext() {
        return context;
    }

    /**
     * Get the human readable label of the group
     *
     * @return group label as a string
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the human readable description of the parameter group
     *
     * @return group description as a string
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [name=\"" + name + "\", context=\"" + context + "\", label=\""
                + label + "\"" + label + "\", description=\"" + description + "\"]";
    }
}

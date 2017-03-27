/*
 * Copyright (C) 2017 ZeXtras S.r.l.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.fasterxml.jackson.datatype.container;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zextras.lib.Container;
import com.zextras.lib.ContainerList;

public class ContainerModule extends SimpleModule
{
    private final static String NAME = "ContainerModule";
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */
    
    public ContainerModule()
    {
        super(NAME, ModuleVersion.instance.version());
        addDeserializer(ContainerList.class, ContainerListDeserializer.instance);
        addDeserializer(Container.class, ContainerDeserializer.instance);
        addSerializer(ContainerListSerializer.instance);
        addSerializer(ContainerSerializer.instance);
    }
}
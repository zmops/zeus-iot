/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.zmops.zeus.iot.server.core.storage;


import com.zmops.zeus.server.library.module.ModuleDefine;

/**
 * StorageModule provides the capabilities(services) to interact with the database. With different databases, this
 * module could have different providers, such as currently, H2, MySQL, ES, TiDB.
 */
public class StorageModule extends ModuleDefine {

    public static final String NAME = "storage";

    public StorageModule() {
        super(NAME);
    }

    @Override
    public Class[] services() {
        return new Class[]{
                IBatchDAO.class,
                StorageDAO.class
        };
    }
}

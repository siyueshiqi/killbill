/*
 * Copyright 2010-2013 Ning, Inc.
 * Copyright 2014-2018 Groupon, Inc
 * Copyright 2014-2018 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing;

import org.killbill.billing.platform.api.KillbillConfigSource;
import org.killbill.billing.platform.test.PlatformDBTestingHelper;
import org.killbill.billing.platform.test.config.TestKillbillConfigSource;
import org.killbill.billing.platform.test.glue.TestPlatformModuleWithEmbeddedDB;
import org.killbill.billing.util.glue.GlobalLockerModule;
import org.killbill.billing.util.glue.IDBISetup;
import org.killbill.clock.ClockMock;
import org.skife.jdbi.v2.IDBI;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

public class GuicyKillbillTestWithEmbeddedDBModule extends GuicyKillbillTestModule {

    private final boolean withOSGI;

    public GuicyKillbillTestWithEmbeddedDBModule(final KillbillConfigSource configSource, final ClockMock clock) {
        this(false, configSource, clock);
    }

    public GuicyKillbillTestWithEmbeddedDBModule(final boolean withOSGI, final KillbillConfigSource configSource, final ClockMock clock) {
        super(configSource, clock);
        this.withOSGI = withOSGI;
    }

    @Override
    protected void configure() {
        super.configure();

        install(new KillbillTestPlatformModuleWithEmbeddedDB(configSource));
        install(new GlobalLockerModule(configSource));
    }

    private final class KillbillTestPlatformModuleWithEmbeddedDB extends TestPlatformModuleWithEmbeddedDB {

        public KillbillTestPlatformModuleWithEmbeddedDB(final KillbillConfigSource configSource) {
            super(configSource, withOSGI, (TestKillbillConfigSource) configSource);
        }

        @Provides
        @Singleton
        @Named(IDBISetup.MAIN_RO_IDBI_NAMED)
        protected IDBI provideRoIDBIInAComplicatedWayBecauseOf627(final IDBI idbi) {
            return idbi;
        }

        @Override
        protected PlatformDBTestingHelper getPlatformDBTestingHelper() {
            return DBTestingHelper.get();
        }

        protected void configureKillbillNodesApi() {}
    }
}

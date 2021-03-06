/*
 * Copyright @ 2015-2017 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.videobridge.octo;

import net.java.sip.communicator.util.*;
import org.jitsi.service.configuration.*;
import org.osgi.framework.*;

import java.net.*;

/**
 * A {@link BundleActivator} for a bridge-to-bridge (Octo) relay.
 *
 * @author Boris Grozev
 */
public class OctoRelayService
    implements BundleActivator
{
    /**
     * The {@link Logger} used by the {@link OctoRelay} class and its
     * instances to print debug information.
     */
    private static final Logger logger
        = Logger.getLogger(OctoRelay.class);

    /**
     * The name of the configuration property which controls the address on
     * which the Octo relay should bind.
     */
    public static final String ADDRESS_PNAME
        = "org.jitsi.videobridge.octo.BIND_ADDRESS";

    /**
     * The name of the property which controls the port number which the Octo
     * relay should use.
     */
    public static final String PORT_PNAME
        = "org.jitsi.videobridge.octo.BIND_PORT";

    /**
     * The Octo relay instance used by this {@link OctoRelayService}.
     */
    private OctoRelay relay;

    /**
     * The {@code ConfigurationService} which looks up values of configuration
     * properties.
     */
    private ConfigurationService cfg;

    /**
     * @return the {@link OctoRelay} managed by this
     * {@link OctoRelayService}.
     */
    public OctoRelay getRelay()
    {
        return relay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BundleContext bundleContext)
    {
        cfg
            = ServiceUtils.getService(
                    bundleContext, ConfigurationService.class);

        String address = cfg.getString(ADDRESS_PNAME, null);
        int port = cfg.getInt(PORT_PNAME, -1);

        if (address != null && NetworkUtils.isValidPortNumber(port))
        {
            try
            {
                relay = new OctoRelay(address, port);

                bundleContext
                    .registerService(OctoRelayService.class.getName(), this,
                                     null);
                logger.info("Initialized an Octo relay with address "
                                + address + ":" + port);
            }
            catch (UnknownHostException | SocketException e)
            {
                logger.error("Failed to initialize Octo relay with address "
                                 + address + ":" + port + ". ", e);
            }
        }
        else
        {
            logger.info("Octo relay not configured.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        if (relay != null)
        {
            relay.stop();
        }
    }

    /**
     * @return the ID of the Octo relay managed by this {@link OctoRelayService}.
     */
    public String getRelayId()
    {
        return relay.getId();
    }
}

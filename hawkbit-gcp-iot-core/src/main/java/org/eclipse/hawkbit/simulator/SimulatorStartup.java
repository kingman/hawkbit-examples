/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.simulator;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.hawkbit.google.gcp.GcpFireStore;
import org.eclipse.hawkbit.google.gcp.GcpSubscriber;
import org.eclipse.hawkbit.simulator.amqp.AmqpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Execution of operations after startup. Set up of simulations.
 *
 */
@Component
@ConditionalOnProperty(prefix = "hawkbit.device.simulator", name = "autostart", matchIfMissing = true)
public class SimulatorStartup implements ApplicationListener<ApplicationReadyEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorStartup.class);

	@Autowired
	private SimulationProperties simulationProperties;

	@Autowired
	private DeviceSimulatorRepository repository;

	@Autowired
	private SimulatedDeviceFactory deviceFactory;

	@Autowired
	private AmqpProperties amqpProperties;

	@Autowired
	private GcpSubscriber gcpSubscriber;

	@Autowired
	private GcpFireStore gcpFireStore;

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		System.out.println("AutoStarting application ...");
		LOGGER.debug("{} autostarts will be executed", simulationProperties.getAutostarts().size());
		
		
		amqpProperties.setEnabled(true);
		
		LOGGER.info("Init Firestore ... ");
		gcpFireStore.init();
		LOGGER.info("Init Subscriber ... ");
		gcpSubscriber.init();

		//TODO: Nice to have: at startup read the Hawkbit artifacts and upload them to the bucket
		simulationProperties.getAutostarts().forEach(autostart -> {
			LOGGER.info("Autostart runs for tenant {} and API {}", autostart.getTenant(), autostart.getApi());
			for (int i = 0; i < autostart.getAmount(); i++) {
				final String deviceId = autostart.getName() + i;
				try {
					if (amqpProperties.isEnabled()) {
						repository.add(deviceFactory.createSimulatedDeviceWithImmediatePoll(deviceId,
								autostart.getTenant(), autostart.getApi(), autostart.getPollDelay(),
								new URL(autostart.getEndpoint()), autostart.getGatewayToken()));
					}

				} catch (final MalformedURLException e) {
					LOGGER.error("Creation of simulated device at startup failed.", e);
				}
			}
		});
	}

}

/**
 * Copyright 2021 Google LLC
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.hawkbit.google.gcp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties(GcpProperties.class)
public class GcpConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(GcpConfiguration.class);

    @Autowired
    private GcpProperties gcpProperties;

    @Bean
    GoogleCredentials googleCredentials() throws IOException {
        Path keyPath = Paths.get(gcpProperties.getServiceAccountKeyPath());
        return GoogleCredentials.fromStream(Files.newInputStream(keyPath));
    }

    @Bean
    GoogleCredential googleCredential() throws IOException {
        Path keyPath = Paths.get(gcpProperties.getServiceAccountKeyPath());
        return GoogleCredential.fromStream(Files.newInputStream(keyPath));
    }

    @Bean
    CredentialsProvider credentialsProvider() throws IOException {
        Path keyPath = Paths.get(gcpProperties.getServiceAccountKeyPath());
        return FixedCredentialsProvider.create(
                        ServiceAccountCredentials.fromStream(Files.newInputStream(keyPath)));
    }
}

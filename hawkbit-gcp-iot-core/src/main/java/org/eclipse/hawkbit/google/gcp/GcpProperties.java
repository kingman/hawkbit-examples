/**
 * Copyright 2019 Google LLC
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.hawkbit.google.gcp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("hawkbit.google.gcp.configuration")
public class GcpProperties {

		public static final String CONFIGURATION_PREFIX = "hawkbit.google.gcp.configuration";
		public static final String FW_MSG_RECEIVED = "msg-received";
		public static final String FW_INSTALLING = "installing";
		public static final String FW_DOWNLOADING = "downloading";
		public static final String FW_INSTALLED = "installed";
        
        private String projectId;
        private String iotCoreRegion;
        private String iotCoreRegistryId;
        private String otaStorageBucketName;
		private String serviceAccountKeyPath;

		private String subscriptionStateId;
		private String subscriptionFwState;
		private String deviceId;

		private String firestoreDevicesCollection;
		private String firestoreConfigCollection;
		private String fwUpdate;

		private String objectName;
		private String url;
		private String md5hash;
	
		private boolean fwViaCommand;

        public void setProjectId(final String projectId) {
            this.projectId = projectId;
        }

        public void setIotCoreRegion(final String iotCoreRegion) {
            this.iotCoreRegion = iotCoreRegion;
        }

        public void setIotCoreRegistryId(final String iotCoreRegistryId) {
            this.iotCoreRegistryId = iotCoreRegistryId;
        }

        public void setOtaStorageBucketName(final String otaStorageBucketName) {
            this.otaStorageBucketName = otaStorageBucketName;
        }

        public void setServiceAccountKeyPath(final String serviceAccountKeyPath) {
            this.serviceAccountKeyPath = serviceAccountKeyPath;
		}
		
		public void setSubscriptionStateId(final String subscriptionStateId) {
			this.subscriptionStateId = subscriptionStateId;
		}
		public void setSubscriptionFwState(final String subscriptionFwState) {
			this.subscriptionFwState = subscriptionFwState;
		}
		public void setDeviceId(final String deviceId) {
			this.deviceId = deviceId;
		}

		public void setFirestoreDevicesCollection(final String firestoreDevicesCollection) {
			this.firestoreDevicesCollection = firestoreDevicesCollection;
		}
		public void setFirestoreConfigCollection(final String firestoreConfigCollection) {
			this.firestoreConfigCollection = firestoreConfigCollection;
		}
		public void setFwUpdate(final String fwUpdate) {
			this.fwUpdate = fwUpdate;
		}

		public void setObjectName(final String objectName) {
			this.objectName = objectName;
		}
		public void setUrl(final String url) {
			this.url = url;
		}
		public void setMd5hash(final String md5hash) {
			this.md5hash = md5hash;
		}
	
		public void setFwViaCommand(final boolean fwViaCommand) {
			this.fwViaCommand = fwViaCommand;
		}

		public String getSubscriptionStateId() {
			return subscriptionStateId;
		}
		public String getSubscriptionFwState() {
			return subscriptionFwState;
		}
		public String getDeviceId() {
			return deviceId;
		}
		public String getFirestoreDevicesCollection() {
			return firestoreDevicesCollection;
		}
		public String getFirestoreConfigCollection() {
			return firestoreConfigCollection;
		}
		public String getFwUpdate() {
			return fwUpdate;
		}
		public String getObjectName() {
			return objectName;
		}
		public String getUrl() {
			return url;
		}
		public String getMd5hash() {
			return md5hash;
		}
		public boolean getFwViaCommand() {
			return fwViaCommand;
		}

        public String getProjectId() {
            return projectId;
        }

        public String getIotCoreRegion() {
            return iotCoreRegion;
        }

        public String getIotCoreRegistryId() {
            return iotCoreRegistryId;
        }

        public String getOtaStorageBucketName() {
            return otaStorageBucketName;
        }

        public String getServiceAccountKeyPath() {
            return serviceAccountKeyPath;
		}	
}